package com.vrv.rule.ruleInfo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.logVO.StreamMidVO;
import com.vrv.rule.source.DataStreamSource;
import com.vrv.rule.source.DataStreamSourceJdbc;
import com.vrv.rule.util.JdbcConnectionUtil;
import com.vrv.rule.util.YmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.parsing.json.JSON;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年9月6日 下午4:09:39 
* 类说明       过滤规则数据连接JDBC的FUNCTION函数
*/
public class FlinkJdbcFilterFunction {
      
	private static Logger logger = LoggerFactory.getLogger(DataStreamSource.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static void main(String[] args) throws Exception {
		logger.info("lps_2");
		//应该存在的字段
		String rule_code = args[0]; //规则编码
		System.out.println("rule_code:"+rule_code);
		String jobName = args[1]; //任务名称
		System.out.println("jobName:"+jobName);
		String sql = args[2]; //flink sql
		System.out.println("sql:"+sql);
		String tableName = args[3];
		logger.info("tableName:"+tableName);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES),Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型(处理时间)
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		DataStreamSourceJdbc userProcessActionSourceJdbc2 = new DataStreamSourceJdbc(tableName);
		
		Table table = sTableEnv.fromTableSource(userProcessActionSourceJdbc2);
		sTableEnv.registerTable(tableName, table);
		
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		Map<String, Object> map = JdbcConnectionUtil.querySqlForMap(JdbcConnectionUtil.event_table_sql+"'"+tableName+"'");
		String id = map.get("id").toString(); //event_table的id
		List<Map<String,Object>> querySqlForList = JdbcConnectionUtil.querySqlForList(JdbcConnectionUtil.event_column_field_sql+"'"+id+"'");
		DataStream<Tuple2<Boolean,Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		logger.info("message:"+url+":"+port);
		retractStream.map(new MapFunction<Tuple2<Boolean,Row>,String>() {
			@Override
			public String map(Tuple2<Boolean, Row> value) throws Exception {
				Map<String,Object> map  = new HashMap<>();
				Row row = value.f1;
				logger.info("lps_row: "+ row.toString());
				int arity = row.getArity();
				String[] field2 = userProcessActionSourceJdbc2.getField();
				String src_Ip = null;
				String dst_Ip = null;
				String relate_ip  = null;
				String src_port=null;
				String dst_port=null;
				String logInfo = null;
				Timestamp triggerTime = null;
				for (int i = 0; i < arity; i++) {
					String columnName = field2[i];
					Boolean result1=(Boolean)querySqlForList.get(i).get("srcIp");
					if(result1){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							src_Ip = fieldValue.toString();
							logger.info("src_Ip:"+src_Ip);
							map.put(columnName, src_Ip);
							continue;
						}
					}
					Boolean result2=(Boolean)querySqlForList.get(i).get("dstIp");
					if(result2){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							dst_Ip = fieldValue.toString();
							logger.info("src_Ip:"+dst_Ip);
							map.put(columnName, dst_Ip);
							continue;
						}
					}
					
					Boolean result3=(Boolean)querySqlForList.get(i).get("relateIp");
					if(result3){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							relate_ip = fieldValue.toString();
							map.put(columnName, relate_ip);
							continue;
						}
					}
					if(columnName.equals("src_port")){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							src_port=fieldValue.toString();
							map.put(columnName, src_port);
							continue;
						}
					}
					if(columnName.equals("dst_port")){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							dst_port=fieldValue.toString();
							map.put(columnName, dst_port);
							continue;
						}
					}
					if(columnName.equals("triggerTime")){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							triggerTime = (Timestamp)fieldValue;
							triggerTime = new Timestamp(triggerTime.getTime()+60*60*8*1000);
							map.put(columnName, triggerTime);
							continue;
						}
					}
					
					//时间修改
					if(columnName.equals("triggerTime")){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							triggerTime = (Timestamp)fieldValue;
							triggerTime = new Timestamp(triggerTime.getTime()+60*60*8*1000);
							map.put(columnName, triggerTime);
							continue;
						}
					}
					
					map.put(columnName, row.getField(i));
				}
				logInfo = gson.toJson(map);
				StreamMidVO streamMidVO  = new StreamMidVO(UUID.randomUUID().toString(), 
						rule_code,new Date(), src_Ip, dst_Ip,relate_ip, logInfo,src_port, dst_port);
				String json = gson.toJson(streamMidVO);
				return json;
			}
		}).addSink(new FlinkKafkaProducer<String>(url+":"+port, YmlUtil.getValue("application.yml", "topic_producer_name").toString(), new SimpleStringSchema()));;
		env.execute(jobName);
	}
	
}
