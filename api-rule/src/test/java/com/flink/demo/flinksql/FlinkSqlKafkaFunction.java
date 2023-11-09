package com.flink.demo.flinksql;

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

import com.flink.demo.flinksql.model.DimensionUser;
import com.google.gson.Gson;

/**
 * kafka数据通过flink sql与维表关联
 * @author Administrator
 *
 */
public class FlinkSqlKafkaFunction {

		private static String CREATE_KAFKA_TABLE_SQL = "CREATE TABLE kafka_table (\n" + 
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
	
	
	    private static String CREATE_JDBC_TABLE_SQL = "CREATE TABLE MyUserTable (\n" + 
	    		"  id BIGINT,\n" + 
	    		"  name STRING,\n" + 
	    		"  age INT,\n" + 
	    		"  status BOOLEAN,\n" + 
	    		"  PRIMARY KEY (id) NOT ENFORCED\n" + 
	    		") WITH (\n" + 
	    		"   'connector' = 'jdbc',\n" + 
	    		"   'username' = 'root',\n" + 
	    		"   'password'  = 'mysql',\n" + 
	    		"   'url' = 'jdbc:mysql://localhost:3306/mp',\n" + 
	    		"   'table-name' = 'users'\n" + 
	    		")"; 
		
	    private static String CREATE_ES_TABLE_SQL = "CREATE TABLE MyUserEsTable (\n" + 
	    		"  user_id STRING,\n" + 
	    		"  user_name STRING,\n" + 
	    		"  uv BIGINT,\n" + 
	    		"  pv BIGINT,\n" + 
	    		"  PRIMARY KEY (user_id) NOT ENFORCED\n" + 
	    		") WITH (\n" + 
	    		"  'connector' = 'elasticsearch-7',\n" + 
	    		"  'hosts' = 'http://192.168.120.86:9200',\n" + 
	    		"  'username' = 'admin',\n" + 
	    		"  'password' = 'admin@12345',\n" + 
	    		"  'index' = 'users'\n" + 
	    		")";
	    
	    
	    
		
		//FOR SYSTEM_TIME AS OF kafka_table.proctime
		private static String SELECT_SQL = "select * from kafka_table where user_id = 1";
		private static String DIMENSION_SQL = "SELECT user_id,name,age FROM kafka_table LEFT JOIN MyUserTable  ON kafka_table.user_id = MyUserTable.id";
		private static String ES_SQL = "SELECT user_name,uv,pv FROM kafka_table LEFT JOIN MyUserEsTable ON kafka_table.behavior = MyUserEsTable.user_name";
		
	    private static Gson gson  =new Gson();
		
	    private static String url = "192.168.120.104";
	    private static String port = "9092";
	    
	
	public static void main(String[] args) throws Exception {
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,environmentSettings);
		
		TableEnvironment tableEnv = TableEnvironment.create(environmentSettings);
		tableEnv.executeSql(CREATE_KAFKA_TABLE_SQL);  //创建KAFKA表语句
		tableEnv.executeSql(CREATE_JDBC_TABLE_SQL);   //创建JDBC
		tableEnv.executeSql(CREATE_ES_TABLE_SQL); //创建ES连接
		
		Table table = tableEnv.sqlQuery(DIMENSION_SQL);   //维表SQL查询
		DataStream<Tuple2<Boolean, DimensionUser>> retractStream = sTableEnv.toRetractStream(table, DimensionUser.class);
		
		retractStream.map(new MapFunction<Tuple2<Boolean,DimensionUser>, String>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String map(Tuple2<Boolean, DimensionUser> value) throws Exception {
				DimensionUser user = value.f1;
				return gson.toJson(user);
			}
		}).addSink(new FlinkKafkaProducer<String>(url+":"+port, "test123" , new SimpleStringSchema()));
		
		env.execute("FlinkSqlKafka");
		
	}
	
	
}
