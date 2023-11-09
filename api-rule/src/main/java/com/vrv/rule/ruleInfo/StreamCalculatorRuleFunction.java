package com.vrv.rule.ruleInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.source.DataStreamSourceJdbc;
import com.vrv.rule.util.YmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年9月6日 下午4:09:39 
* 类说明   流计算规则
*/
public class StreamCalculatorRuleFunction {
      
	private static Logger logger = LoggerFactory.getLogger(StreamCalculatorRuleFunction.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static void main(String[] args) throws Exception {
		//应该存在的字段
		String selectField = args[0]; //规则编码
		System.out.println("selectField:"+selectField);
		String jobName = args[1]; //任务名称
		System.out.println("jobName:"+jobName);
		String sql = args[2]; //flink sql
		System.out.println("sql:"+sql);
		String tableName = args[3];
		System.out.println("tableName:"+tableName);
		String outPutTopic = args[4];
		System.out.println("outPutTopic:"+outPutTopic);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES),Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型(处理时间)


		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		DataStreamSourceJdbc userProcessActionSourceJdbc2 = new DataStreamSourceJdbc(tableName);
		Table table = sTableEnv.fromTableSource(userProcessActionSourceJdbc2);
		sTableEnv.registerTable(tableName, table);
		
		
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		DataStream<Tuple2<Boolean,Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		logger.info("message:"+url+":"+port);
		retractStream.map(new MapFunction<Tuple2<Boolean,Row>,String>() {
			@Override
			public String map(Tuple2<Boolean, Row> value) throws Exception {
				Map<String,Object> map  = new HashMap<>();
				Row row = value.f1;
				int arity = row.getArity();
				if(selectField.equals("*")){ //查询所有的
					String[] field2 = userProcessActionSourceJdbc2.getField();
					for (int i = 0; i < arity; i++) {
						String columnName = field2[i];
						map.put(columnName, row.getField(i));
					}
			   }else{
					String[] fieldArr = selectField.split(",");
					for (int i = 0; i < arity; i++) {
						if(fieldArr.length==fieldArr.length){
							String columnName = fieldArr[i];
							map.put(columnName, row.getField(i));
						}else{
							throw new RuntimeException("字段查询不匹配！，请检查");
						}
					}
				  }
				   String json = gson.toJson(map);
				   return json;
				}
			}).addSink(new FlinkKafkaProducer<String>(url+":"+port,outPutTopic, new SimpleStringSchema()));;
		env.execute(jobName);
	}
	
}
