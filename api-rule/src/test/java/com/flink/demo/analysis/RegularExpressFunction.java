package com.flink.demo.analysis;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import com.flink.demo.flinksql.model.User;
import com.google.gson.Gson;
import com.vrv.rule.ruleInfo.udf.IpResourceFunction;
import com.vrv.rule.ruleInfo.udf.RegularExpressionFunction;

/**
 * sql正则表达式测试函数
 * @author Administrator
 *
 */
public class RegularExpressFunction {

	private static String CREATE_KAFKA_TABLE_SQL = "CREATE TABLE kafkaTable (\n" + 
			"  `user_id` BIGINT,\n" + 
			"  `item_id` BIGINT,\n" + 
			"  `behavior` STRING,\n" + 
			"  `ts` TIMESTAMP(3) METADATA FROM 'timestamp'\n" + 
			") WITH (\n" + 
			"  'connector' = 'kafka',\n" + 
			"  'topic' = 'user_behavior',\n" + 
			"  'properties.bootstrap.servers' = '192.168.120.104:9092',\n" + 
			"  'properties.group.id' = 'testGroup',\n" + 
			"  'format' = 'json'\n" + 
			")";
	
	private static String udf_sql = "select * from kafkaTable where (regularExpressionFunction(behavior,'/^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$/')=1)";
	
	private static Gson gson  = new Gson();
	
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,environmentSettings);
		
		TableEnvironment tableEnv = TableEnvironment.create(environmentSettings);
		
		tableEnv.executeSql(CREATE_KAFKA_TABLE_SQL);  //创建KAFKA表语句
		sTableEnv.createTemporarySystemFunction("regularExpressionFunction", new RegularExpressionFunction("1", "2"));
		
		
		Table table = tableEnv.sqlQuery(udf_sql);   //维表SQL查询
		DataStream<Tuple2<Boolean, User>> retractStream = sTableEnv.toRetractStream(table, User.class);
		retractStream.map(new MapFunction<Tuple2<Boolean,User>, String>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String map(Tuple2<Boolean, User> value) throws Exception {
				User user = value.f1;
				return gson.toJson(user);
			}
		}).addSink(new FlinkKafkaProducer<String>("192.168.120.104:9092", "test123" , new SimpleStringSchema()));
		
		env.execute("FlinkSqlKafka");
		
	}
	
	
}
