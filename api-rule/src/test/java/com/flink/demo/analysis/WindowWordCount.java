package com.flink.demo.analysis;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author wudi 
 * E-mail:wudi891012@163.com
 * @version 
 * 创建时间：2018年10月31日 上午11:44:26 
 * 类说明 对以空格为边界的分词的统计工作
 */
public class WindowWordCount {

	public static void main(String[] args) throws Exception {
		
		
		
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		env.enableCheckpointing(1000);
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env,fsSettings);
		
//		DataStream<Tuple3<String, String, String>> mapStream = env.socketTextStream("192.168.118.81", 9999).map(new TableMapApiSplitter());
//		DataStream<Tuple2<Boolean,Tuple3<String,String,Long>>> dataStream = getStreamTableWithWindow(sTableEnv);
//		DataStream<Tuple2<Boolean, Tuple2<String, Long>>> dataStream = getStreamTableWithOutTime(env); //Table Convert Stream
		DataStream<Tuple2<String, Integer>> dataStream = env.socketTextStream("192.168.118.81", 9000).flatMap(new Splitter())
				.keyBy(0).timeWindow(Time.seconds(5)).allowedLateness(Time.seconds(5)).sum(1);
//		DataStream<Tuple2<Boolean, Tuple3<String, Timestamp, Long>>> dataStream = getStreamTableWithOtherTime(sTableEnv, mapStream);
		//TODO 这里通过TABLE AND SQL 查询完成对应的数据流的处理工作
		dataStream.print();
		env.execute("Window Table WordCount");
	}

	public static class TableMapApiSplitter implements MapFunction<String, Tuple3<String,String,String>>{
		@Override
		public Tuple3<String, String, String> map(String value) throws Exception {
			String[] split = value.split(",");
			Tuple3<String, String, String> tuple3 = new Tuple3<String, String,String>(split[0],split[1],split[2]);
			return tuple3;
		}
	}

    
	/**
	 * table api 完成
	 * @author wd-pc
	 *
	 */
	public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>>{
		@Override
		public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
			for (String word : sentence.split(" ")) {
				out.collect(new Tuple2<String, Integer>(word, 1));
			}
		}
	}

	
	
	
	
}
