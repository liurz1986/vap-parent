package com.vrv.rule.ruleInfo.assetRisk;

import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.StreamTableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.logVO.MainDesc;
import com.vrv.rule.source.DataStreamAssetRiskSource;
import com.vrv.rule.source.MyMapFunction;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.AssetRiskOutPutVO;
import com.vrv.rule.vo.AssetRiskVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午5:42:34 
* 类说明  资产威胁频率分析
*/
@MainDesc(type="资产威胁频率分析",description = "入侵告警测试")
public class FlinkAssetAnalysisFunction {

	private static Logger logger = LoggerFactory.getLogger(FlinkAssetAnalysisFunction.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static <T> void main(String[] args) throws Exception {
		//应该存在的字段
		String rule_code = "asset_risk"; //规则编码args[0] 
		System.out.println("rule_code:"+rule_code);
		String jobName = "asset-risk-job"; //任务名称
		System.out.println("jobName:"+jobName);
		String orignalLogPath ="com.vrv.logVO.alarmdeal.WarnResultLogVO"; //原始日志路径 args[2]
		System.out.println("orignalLogPath:"+orignalLogPath);
		String sql ="select dstIps as ip,count(dstIps) as num,weight as weight,TUMBLE_START(eventTime,INTERVAL '10' SECOND) as startTime "
				   + " from warn_result group by dstIps,weight,TUMBLE(eventTime,INTERVAL '10' SECOND)"; //flink sql args[3] ,TUMBLE_END(eventTime,INTERVAL '30' SECOND) as endTime
		System.out.println("sql:"+sql);
		String tableName = "warn_result";  //args[4]
		System.out.println("tableName:"+tableName);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES),Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		//StreamTableEnvironment sTableEnv = TableEnvironment.getTableEnvironment(env);  //获得Stream流信息并变成Table信息
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env,fsSettings);
		
		
		Class<?> forName = Class.forName(orignalLogPath);
		Class<T> clazz= (Class<T>)forName;
		StreamTableSource<T> streamTableSource = new DataStreamAssetRiskSource(clazz);
		Table table = tableEnvironment.fromTableSource(streamTableSource);
		tableEnvironment.registerTable(tableName,table); //windows_log
		Table sqlQuery = tableEnvironment.sqlQuery(sql);
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean,AssetRiskOutPutVO>> retractStream = tableEnvironment.toRetractStream(sqlQuery, AssetRiskOutPutVO.class);
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		logger.info("url输出:{},port输出:{}", url,port);
		DataStream<AssetRiskVO> unorderedWait = AsyncDataStream.unorderedWait(retractStream, new AssetGuidRiskByRedis(), 5000, TimeUnit.MICROSECONDS, 1);
		unorderedWait.map(new MyMapFunction<AssetRiskVO, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public String map(AssetRiskVO value) throws Exception {
				String json = gson.toJson(value);
				logger.info("json输出:{}", json);
				return json;
			}
			@Override
			public TypeInformation<String> getProducedType() {
				TypeInformation<String> typeInformation = TypeInformation.of(String.class);
				return typeInformation;
			}
		}).addSink(new FlinkKafkaProducer<String>(url+":"+port,"assetTheatFrequency" , new SimpleStringSchema()));
		
		env.execute(jobName);
	}
	
	

}
