package com.vrv.rule.ruleInfo;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.TableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.logVO.RelateField;
import com.vrv.rule.logVO.MainDesc;
import com.vrv.rule.logVO.StreamMidVO;
import com.vrv.rule.ruleInfo.udf.AccountArrayFunction;
import com.vrv.rule.ruleInfo.udf.SumArrayFunction;
import com.vrv.rule.source.DataStreamFunction;
import com.vrv.rule.source.DatasourceFactory;
import com.vrv.rule.source.MyMapFunction;
import com.vrv.rule.util.YmlUtil;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午5:42:34 
* 类说明    蜜罐告警-入侵告警
*/
@MainDesc(type="公共main函数",description = "入侵告警测试")
public class FlinkMainFunction {

	private static Logger logger = LoggerFactory.getLogger(FlinkMainFunction.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static <T> void main(String[] args) throws Exception {
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
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		logger.info("url:{}", url);
		logger.info("port:{}", port);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES),Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		
		
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		
		Class<?> forName = Class.forName(orignalLogPath);
		Class<T> clazz= (Class<T>)forName;
		
		TableSource<T> tableSource = DatasourceFactory.create(clazz);
		Table table = sTableEnv.fromTableSource(tableSource);
		sTableEnv.registerTable(tableName,table); //windows_log
		
		sTableEnv.registerFunction("sumArrayFunction", new SumArrayFunction()); //数组总和统计
		sTableEnv.registerFunction("accountArrayFunction", new AccountArrayFunction());  //个数统计
		Table sqlQuery = sTableEnv.sqlQuery(sql);  //"select * from windows_log where severity='ERROR'"
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean,T>> retractStream = sTableEnv.toRetractStream(sqlQuery, clazz);
		retractStream.map(new MyMapFunction<Tuple2<Boolean,T>, String>() {
			@Override
			public String map(Tuple2<Boolean, T> value) throws Exception {
				T t = value.f1;
				Field[] declaredFields = t.getClass().getDeclaredFields();
				String src_Ip = null;
				String dst_Ip = null;
				String src_port = null;
				String dst_port = null;
				String relate_ip  = null;
				String areaName = null;
				String areaCode = null;
				for (Field field : declaredFields) {
					RelateField relateField = field.getAnnotation(RelateField.class);
					field.setAccessible(true); //加上对应的认证
					if(relateField!=null){
						String relateFieldValue = relateField.value();
						switch (relateFieldValue) {
						case "src_Ip":
							src_Ip = (String)field.get(t);
							relate_ip = (String)field.get(t);
							break;
						case "dst_Ip":
							dst_Ip = (String)field.get(t);
							break;
						case "src_port":
							src_port = (String)field.get(t);
							break;
						case "dst_port":
							dst_port = (String)field.get(t);
							break;
						case "relate_ip":
							relate_ip = (String)field.get(t);
							break;
						case "areaName":
							areaName = (String)field.get(t);
							break;
						case "areaCode":
							areaCode = (String)field.get(t);
							break;
						default:
							break;
						}
					}else {
						String name = field.getName();
						if(name.equals("triggerTime")){
							Timestamp time = (Timestamp)field.get(t);
							long timestamp = time.getTime()+8*60*60*1000;
							field.set(t, new Timestamp(timestamp));
						}
					}
					
				}
				//"/honeypot/invadefiled"
				StreamMidVO streamMidVO  = new StreamMidVO(UUID.randomUUID().toString(), 
						rule_code, new Date(), src_Ip, dst_Ip,relate_ip, gson.toJson(t),src_port, dst_port);
				String json = gson.toJson(streamMidVO);
				return json;
			}

			@Override
			public TypeInformation<String> getProducedType() {
				TypeInformation<String> typeInformation = TypeInformation.of(String.class);
				return typeInformation;
			}
		}).addSink(new FlinkKafkaProducer<String>(url+":"+port, YmlUtil.getValue("application.yml", "topic_producer_name").toString(), new SimpleStringSchema()));
		retractStream.print();
		env.execute(jobName);
	}
	
	

}
