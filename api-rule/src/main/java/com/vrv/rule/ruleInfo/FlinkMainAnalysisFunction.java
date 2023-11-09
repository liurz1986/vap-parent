package com.vrv.rule.ruleInfo;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.TableSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.logVO.RelateField;
import com.vrv.rule.logVO.MainDesc;
import com.vrv.rule.logVO.StreamMidVO;
import com.vrv.rule.source.DataStreamFunction;
import com.vrv.rule.source.DatasourceFactory;
import com.vrv.rule.source.MyMapFunction;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.OutPutVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午5:42:34 
* 类说明   分析查询公共函数
*/
@MainDesc(type="性能告警公共main函数",description = "入侵告警测试")
public class FlinkMainAnalysisFunction {

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	@SuppressWarnings("deprecation")
	public static <T> void main(String[] args) throws Exception {
		//应该存在的字段
		String rule_code = args[0]; //规则编码
		System.out.println("rule_code:"+rule_code);
		String jobName = args[1]; //任务名称
		System.out.println("jobName:"+jobName);
		String orignalLogPath = args[2]; //原始日志路径
		System.out.println("orignalLogPath:"+orignalLogPath);
		String sql = args[3]; //flink sql
		System.out.println("sql:"+sql);
		String tableName = args[4];
		System.out.println("tableName:"+tableName);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES),Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		
		
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		
		
		Class<?> forName = Class.forName(orignalLogPath);
		Class<T> clazz= (Class<T>)forName;
		
		TableSource<T> tableSource = DatasourceFactory.create(clazz);
		Table  table = sTableEnv.fromTableSource(tableSource);
		
		//DataStreamFunction<T> dataFunction = new  DataStreamFunction<>(clazz);
		//DataStream<T> dataStream = dataFunction.getDataStream(env);
		//sTableEnv.registerDataStream(tableName, dataStream,"dst_ip,download_bytes,proctime.proctime");
		 sTableEnv.registerTable(tableName,table); //windows_log
		Table sqlQuery = sTableEnv.sqlQuery(sql);  //"select * from windows_log where severity='ERROR'"
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean,OutPutVO>> retractStream = sTableEnv.toRetractStream(sqlQuery, OutPutVO.class);
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		retractStream.map(new MyMapFunction<Tuple2<Boolean,OutPutVO>, String>() {
			@Override
			public String map(Tuple2<Boolean, OutPutVO> value) throws Exception {
				OutPutVO outPutVO = value.f1;
				String ip = outPutVO.getIp();
				Timestamp triggerTime = outPutVO.getTriggerTime();
				long timestamp = triggerTime.getTime()+8*60*60*1000;
				outPutVO.setTriggerTime(new Timestamp(timestamp));
				StreamMidVO streamMidVO  = new StreamMidVO(UUID.randomUUID().toString(), 
						rule_code, new Date(), ip, ip,ip, gson.toJson(outPutVO),null, null);
				String json = gson.toJson(streamMidVO);
				return json;
			}

			@Override
			public TypeInformation<String> getProducedType() {
				TypeInformation<String> typeInformation = TypeInformation.of(String.class);
				return typeInformation;
			}
		}).addSink(new FlinkKafkaProducer<String>(url+":"+port, YmlUtil.getValue("application.yml", "topic_producer_name").toString(), new SimpleStringSchema()));
		//retractStream.print();
		env.execute(jobName);
	}
	
	

}
