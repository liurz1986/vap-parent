package com.flink.demo.jdbc;

import java.util.Map;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class MysqlMainFunction {

	public static void main(String[] args) throws Exception {
        System.out.println("===============》 flink任务开始  ==============》");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置时间类型
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        //获得对应的table
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
        //设置检查点时间间隔
        env.enableCheckpointing(5000);
        //设置检查点模式
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
      
        DataStream<String> dataStream = env.socketTextStream("192.168.89.131", 9999).map(x ->{
            return x;
        });
        
        DataStreamSource<Map<String,Object>> dataStreamSource = env.addSource(new MysqlSource());
        dataStreamSource.print().setParallelism(2);
//        DataStream<List<Map<String, Object>>> unorderedWait = AsyncDataStream.unorderedWait(dataStream, new AsyncReadJdbc() , 1000, TimeUnit.MICROSECONDS, 1);
//        unorderedWait.print();
        sTableEnv.registerDataStream("socket", dataStream,"name");
       sTableEnv.registerDataStream("mysql", dataStream,"id1,name,age");
//		
		String sql = "select * from socket INNER JOIN mysql ON socket.name = mysql.name";
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		DataStream<Tuple2<Boolean,Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		retractStream.print();
		env.execute("Window Table WordCount");
       
    
	}
}
