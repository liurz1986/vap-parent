package com.flink.demo.sqltest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.logVO.selfsafe.DwDeviceServicelog;
import com.vrv.rule.util.DateUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月5日 下午4:11:53
* 类说明 处理时间
*/
public class UserUrlActionSource implements StreamTableSource<DwDeviceServicelog>,DefinedProctimeAttribute{
    
	
	private static Logger logger = LoggerFactory.getLogger(UserUrlActionSource.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static final String KAFKA_PRODUCER_URL  = "192.168.18.159:9092";
	public static final String TOPIC_PRODUCER_NAME = "flink-wiki-demo";
	public static final String KAFKA_CONSUMER_URL = "192.168.18.159:9092";
	public static final String TOPIC_CONSUMER_NAME = "terminal-Intranet-softwareService-standard-v1";
	public static final String KAFKA_GROUP_ID = "test";
	
	@Override
	public String explainSource() {
		return "UserActions";
	}

	@Override
	public TypeInformation<DwDeviceServicelog> getReturnType() {
		TypeInformation<DwDeviceServicelog> typeInformationTypes = TypeInformation.of(DwDeviceServicelog.class);
		return typeInformationTypes;
	}

	@Override
	public TableSchema getTableSchema() {
		TypeInformation<DwDeviceServicelog> typeInformationTypes = TypeInformation.of(DwDeviceServicelog.class);
		TableSchema tableSchema = TableSchema.fromTypeInfo(typeInformationTypes);
		return tableSchema;
		
	}

	@Override
	public String getProctimeAttribute() {
		return "triggerTime"; //machine local time
	}

	@Override
	public DataStream<DwDeviceServicelog> getDataStream(StreamExecutionEnvironment env){
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CONSUMER_URL);
		properties.setProperty("group.id", KAFKA_GROUP_ID);
		DataStream<String> addSource = env.addSource(new FlinkKafkaConsumer<>(TOPIC_CONSUMER_NAME,
				new SimpleStringSchema(), properties));
		DataStream<DwDeviceServicelog> returns = addSource.map(new MapFunction<String, DwDeviceServicelog>() {
			@Override
			public DwDeviceServicelog map(String value) throws Exception {
				Gson gson = DateUtil.parseGsonTime();
				try {
					DwDeviceServicelog DwDeviceServicelog = gson.fromJson(value, DwDeviceServicelog.class);
					return DwDeviceServicelog;
				}catch(Exception e) {
					logger.error("日志解析失败", e);
					return new DwDeviceServicelog();
				}
			}
		});
		
		return returns;
	
	}
	
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型(处理时间)
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		UserUrlActionSource userUrlActionSource = new UserUrlActionSource();
		Table table = sTableEnv.fromTableSource(userUrlActionSource);
		sTableEnv.registerTable("UserActions", table);
		
		Table sqlQuery = sTableEnv.sqlQuery("select * from UserActions");
		
		DataStream<Tuple2<Boolean,DwDeviceServicelog>> retractStream = sTableEnv.toRetractStream(sqlQuery, DwDeviceServicelog.class);
		retractStream.print();
//		retractStream.map(new MapFunction<Tuple2<Boolean,DwDeviceServicelog>, String>() {
//			@Override
//			public String map(Tuple2<Boolean, DwDeviceServicelog> value) throws Exception {
//				DwDeviceServicelog  DwDeviceServicelog = value.f1;
//				String json = gson.toJson(DwDeviceServicelog);
//				System.out.println("json数据："+json);
//				return json;
//			}
//		}).writeAsText("D:\\tmp\\flink");
		env.execute();
	}
	
	
}
