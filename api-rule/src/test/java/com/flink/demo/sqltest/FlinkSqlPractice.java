package com.flink.demo.sqltest;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.wmstrategies.BoundedOutOfOrderTimestamps;
import org.apache.flink.types.Row;

import com.flink.demo.vo.FlinkSqlVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月6日 上午11:51:21
* 类说明  基于事件时间 Flink sql的演练（主要实现方式：flink sql +flink api 综合实现）
*/
public class FlinkSqlPractice implements StreamTableSource<Row>,DefinedRowtimeAttributes {

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static final String KAFKA_PRODUCER_URL  = "192.168.118.81:9092";
	public static final String TOPIC_PRODUCER_NAME = "flink-wiki-demo";
	public static final String KAFKA_CONSUMER_URL = "192.168.118.81:9092";
	public static final String TOPIC_CONSUMER_NAME = "flink-kafka";
	public static final String KAFKA_GROUP_ID = "test";
	
	@Override
	public String explainSource() {
		//TODO 流数据源说明
		return "flinkSqlPractice";
	}

	@Override
	public TypeInformation<Row> getReturnType() {
		TypeInformation<Row> types = getTypeInformationTypes();
		return types;
	}

	@Override
	public TableSchema getTableSchema() {
		return new TableSchema(
				new String[] {"user","url","trigger_time","event_time"},
				new TypeInformation[] {Types.STRING(), Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP()});
	}

	@Override
	public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
		return Collections.
			   singletonList(new RowtimeAttributeDescriptor("event_time", 
					         new ExistingField("event_time"), 
					         new BoundedOutOfOrderTimestamps(1000))); 
	}

	@SuppressWarnings("deprecation")
	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CONSUMER_URL);
		properties.setProperty("group.id", KAFKA_GROUP_ID);
		TypeInformation<Row> row = getTypeInformationTypes();
		FlinkKafkaConsumer<Row> flinkKafkaConsumer010 = new FlinkKafkaConsumer<>(TOPIC_CONSUMER_NAME,new JsonRowDeserializationSchema(row) , properties);
//		flinkKafkaConsumer010.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Row>() {
//			@Override
//			public long extractAscendingTimestamp(Row row) {
//				String datetime = row.getField(2).toString();
//				long time = Timestamp.valueOf(datetime).getTime();
//				return time;
//			}
//	        
//	});
		DataStream<Row> addSource = env.addSource(flinkKafkaConsumer010);
		return addSource.map(new EventTimeToRow()).returns(getReturnType());
	}

	/**
	 * 获得TypeInformationTypes对应的信息
	 * @return
	 */
	private static TypeInformation<Row> getTypeInformationTypes() {
		String[] names = new String[] {"user","url","trigger_time","event_time"};
		TypeInformation[] types = new TypeInformation[] {Types.STRING(), Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP()};
		TypeInformation<Row> row = Types.ROW(names, types);
		return row;
	}
	
	/**
	 * Converts TaxiRide records into table Rows.
	 */
	public static class EventTimeToRow implements MapFunction<Row, Row> {

		@Override
		public Row map(Row row) throws Exception {
			String datetime = row.getField(2).toString();
			Timestamp timestamp = Timestamp.valueOf(datetime);
			Row row2 = Row.of(
					row.getField(0),
					row.getField(1),
					row.getField(2),
					timestamp);
			return row2;
		
		}
	}
	
	
	public static void main(String[] args) throws Exception {	
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); //设置对应的时间类型
		
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		
		FlinkSqlPractice flinkSqlPractice = new FlinkSqlPractice();
		Table table = sTableEnv.fromTableSource(flinkSqlPractice);
		sTableEnv.registerTable("flinkSqlPractice", table);
		
		//select UserName,COUNT(URL) AS cnt,TUMBLE_END(UserActionTime,INTERVAL '1' HOUR) AS endT from UserActions group by UserName,TUMBLE(UserActionTime,INTERVAL '1' HOUR)
		Table sqlQuery = sTableEnv.sqlQuery("select user,COUNT(url) AS cnt,TUMBLE_END(event_time,INTERVAL '8' SECOND) AS trigger_time"
				+ " from flinkSqlPractice group by user,TUMBLE(event_time,INTERVAL '8' SECOND)");
		
//		Table sqlQuery = sTableEnv.sqlQuery("select user,url,event_time from flinkSqlPractice");
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<FlinkSqlVO> appendStream = sTableEnv.toAppendStream(sqlQuery, FlinkSqlVO.class);
		appendStream.map(new MapFunction<FlinkSqlVO, String>() {
			@Override
			public String map(FlinkSqlVO value) throws Exception {
				String json = gson.toJson(value);
				System.out.println("jsons数据:"+json);
				return json;
			}
		}).addSink(new FlinkKafkaProducer<String>(KAFKA_PRODUCER_URL, TOPIC_PRODUCER_NAME, new SimpleStringSchema()));
		//appendStream.print();
		env.execute("Flink Sql Job");
	}

	
	
}
