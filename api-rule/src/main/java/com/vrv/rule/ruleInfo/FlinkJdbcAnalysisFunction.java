package com.vrv.rule.ruleInfo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.logVO.StreamMidVO;
import com.vrv.rule.source.DataStreamSourceJdbc;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.OutPutVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年9月6日 下午4:09:39 
* 类说明       分析规则数据连接JDBC的FUNCTION函数（聚合规则）
*/
public class FlinkJdbcAnalysisFunction {
      
	private static Logger logger = LoggerFactory.getLogger(FlinkJdbcAnalysisFunction.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static void main(String[] args) throws Exception {
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
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型(处理时间)
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		DataStreamSourceJdbc userProcessActionSourceJdbc2 = new DataStreamSourceJdbc(tableName);
		Table table = sTableEnv.fromTableSource(userProcessActionSourceJdbc2);
		sTableEnv.registerTable(tableName, table);
		
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		DataStream<Tuple2<Boolean,OutPutVO>> retractStream = sTableEnv.toRetractStream(sqlQuery, OutPutVO.class);
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		retractStream.map(new MapFunction<Tuple2<Boolean,OutPutVO>,String>(){
			@Override
			public String map(Tuple2<Boolean, OutPutVO> value) throws Exception {
				OutPutVO outPutVO = value.f1;
				logger.info("输出结果：{}"+gson.toJson("outPutVO"));
				String ip = outPutVO.getIp();
				Timestamp triggerTime = outPutVO.getTriggerTime();
				long timestamp = triggerTime.getTime()+8*60*60*1000;
				outPutVO.setTriggerTime(new Timestamp(timestamp));
				StreamMidVO streamMidVO  = new StreamMidVO(UUID.randomUUID().toString(), 
						rule_code, new Date(), ip, ip,ip, gson.toJson(outPutVO),null, null);
				String json = gson.toJson(streamMidVO);
				return json;
			}
		}).addSink(new FlinkKafkaProducer<String>(url+":"+port, YmlUtil.getValue("application.yml", "topic_producer_name").toString(), new SimpleStringSchema()));;
		env.execute(jobName);
	}
	
}
