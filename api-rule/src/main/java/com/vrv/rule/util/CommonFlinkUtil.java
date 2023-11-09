package com.vrv.rule.util;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import com.vrv.rule.source.DatasourceFactory;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月11日 下午2:40:38 
* 类说明   flink运行公共类
*/
public class CommonFlinkUtil<T> {

	/**
	 * 获得数据流
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public  DataStream<Tuple2<Boolean,T>> flinkRunningTime(String[] args,StreamExecutionEnvironment env) throws Exception{
		//应该存在的字段
		String rule_code = args[0]; //规则编码 args[0]
		System.out.println("rule_code:"+rule_code);
		String jobName = args[1]; //任务名称args[1]
		System.out.println("jobName:"+jobName);
		String orignalLogPath = args[2];//args[2]; //原始日志路径
		System.out.println("orignalLogPath:"+orignalLogPath);
		String sql = args[3]; //flink sql  "select * from switchVO";args[3];
		System.out.println("sql:"+sql);
		String tableName = args[4];//args[4];
		System.out.println("tableName:"+tableName);
		Class<?> forName = Class.forName(orignalLogPath);
		Class<T> clazz= (Class<T>)forName;
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		Table table = sTableEnv.fromTableSource(DatasourceFactory.create(clazz));
		sTableEnv.registerTable(tableName,table); //windows_log
		
		Table sqlQuery = sTableEnv.sqlQuery(sql);  //"select * from windows_log where severity='ERROR'"
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean,T>> retractStream = sTableEnv.toRetractStream(sqlQuery, clazz);
		return retractStream;
	}
	
	
}
