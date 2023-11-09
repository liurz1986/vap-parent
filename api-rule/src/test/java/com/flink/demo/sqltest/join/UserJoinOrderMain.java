package com.flink.demo.sqltest.join;


import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月24日 下午4:51:25 
* 类说明   用户与订单联合查询(统计一段时间内，用户产生的订单个数（哪些用户在一段时间）)
*/
public class UserJoinOrderMain {
	
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	public static void main(String[] args) throws Exception {	
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); //设置对应的时间类型
		
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		Table orderSource = sTableEnv.fromTableSource(new OrderInfoSource());
		Table userSource = sTableEnv.fromTableSource(new UserInfoSource());
		
		
		sTableEnv.registerTable("OrderSource", orderSource);
		sTableEnv.registerTable("UserInfoSource", userSource);
		//select UserName,COUNT(URL) AS cnt,TUMBLE_END(UserActionTime,INTERVAL '1' HOUR) AS endT from UserActions group by UserName,TUMBLE(UserActionTime,INTERVAL '1' HOUR)
		Table sqlQuery = sTableEnv.sqlQuery("select u_s.name,count(o_s.order_no) as orderCount"
				+ " from OrderSource AS o_s "
				+ " LEFT JOIN UserInfoSource AS u_s ON o_s.user_id = u_s.id "
				+ " GROUP BY u_s.name");
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean, Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		//DataStream<Tuple2<Boolean, JoinVO>> retractStream = sTableEnv.toRetractStream(sqlQuery, JoinVO.class);
		retractStream.map(new MapFunction<Tuple2<Boolean, Row>, String>() {
			@Override
			public String map(Tuple2<Boolean, Row> value) throws Exception {
				String json = gson.toJson(value.f1);
				return json;
			}
		});
		retractStream.print();
		//.addSink(new FlinkKafkaProducer010<String>(KAFKA_PRODUCER_URL, TOPIC_PRODUCER_NAME, new SimpleStringSchema()));
		//dataStream.writeAsText(file_path);
		//dataStream.print();
		env.execute("AlarmAnalysisSource Job");
	}
	

}
