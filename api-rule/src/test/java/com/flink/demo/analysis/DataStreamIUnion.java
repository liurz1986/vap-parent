package com.flink.demo.analysis;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

/**
 * flink 基于union的测试
 * @author wd-pc
 *
 */
public class DataStreamIUnion {

	
	public static void main(String[] args) throws Exception {
        /**运行环境*/
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        /**输入数据源source1*/
//        DataStreamSource<Tuple3<String, String, String>> source1 = env.fromElements(
//                new Tuple3<>("productID1", "click", "user_1")
//        );
//
//        /**输入数据源source2*/
//        DataStreamSource<Tuple3<String, String, String>> source2 = env.fromElements(
//                new Tuple3<>("productID3", "click", "user_1"),
//                new Tuple3<>("productID3", "click", "user_2")
//        );
//
//        /**输入数据源source3*/
//        DataStreamSource<Tuple3<String, String, String>> source3 = env.fromElements(
//                new Tuple3<>("productID2", "browse", "user_1"),
//                new Tuple3<>("productID2", "click", "user_2"),
//                new Tuple3<>("productID2", "click", "user_1")
//        );

        DataStreamSource<Row> source1 = env.fromElements(Row.of("1","2","3"));
        DataStreamSource<Row> source2 = env.fromElements(Row.of("1",2,"3"));
        DataStreamSource<Row> source3 = env.fromElements(Row.of("1","2",3));
        /**合并流*/
        source1.union(source2,source3).print();
        env.execute();
	}
	
	
}
