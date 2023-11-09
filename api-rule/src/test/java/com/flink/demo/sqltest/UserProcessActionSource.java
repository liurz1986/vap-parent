package com.flink.demo.sqltest;

import java.sql.Timestamp;
import java.util.Properties;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月5日 下午4:11:53
* 类说明 处理时间
*/
public class UserProcessActionSource implements StreamTableSource<Row>,DefinedProctimeAttribute{
    
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static final String KAFKA_PRODUCER_URL  = "192.168.120.105:9092";
	public static final String TOPIC_PRODUCER_NAME = "flink-wiki-demo";
	public static final String KAFKA_CONSUMER_URL = "192.168.120.105:9092";
	public static final String TOPIC_CONSUMER_NAME = "flink-kafka";
	public static final String KAFKA_GROUP_ID = "test";
	
	@Override
	public String explainSource() {
		return "UserActions";
	}

	@Override
	public TypeInformation<Row> getReturnType() {
		TypeInformation<Row> typeInformationTypes = getTypeInformationTypes();
		return typeInformationTypes;
	}

	@Override
	public TableSchema getTableSchema() {
		return new TableSchema(
				new String[] {"user","url","triggerTime"},
				new TypeInformation[] {Types.STRING(), Types.STRING(),Types.SQL_TIMESTAMP()});
	}

	@Override
	public String getProctimeAttribute() {
		return "triggerTime"; //machine local time
	}

	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env){
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CONSUMER_URL);
		properties.setProperty("group.id", KAFKA_GROUP_ID);
		TypeInformation<Row> row = getTypeInformationTypes();
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(TOPIC_CONSUMER_NAME,new JsonRowDeserializationSchema(row) , properties));
		
		return addSource;
	
	}
	
	
	/**
	 * 获得TypeInformationTypes对应的信息(pre_dynamic_tables)
	 * @return
	 */
	private static TypeInformation<Row> getTypeInformationTypes() {
		String[] names = new String[] {"user","url"};
		TypeInformation[] types = new TypeInformation[] {Types.STRING(), Types.STRING()};
		TypeInformation<Row> row = Types.ROW(names, types);
		return row;
	}
	
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型(处理时间)
		
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		
		UserProcessActionSource userProcessActionSource = new UserProcessActionSource();
		Table table = sTableEnv.fromTableSource(userProcessActionSource);
		sTableEnv.registerTable("UserActions", table);
		
		
		Table sqlQuery = sTableEnv.sqlQuery("select user,COUNT(url),TUMBLE_END(triggerTime,INTERVAL '8' SECOND) from UserActions group by user,TUMBLE(triggerTime,INTERVAL '8' SECOND) HAVING COUNT(url)>2 ");
		TupleTypeInfo<Tuple3<String,Long,Timestamp>> tupleTypeInfo = new TupleTypeInfo<>(Types.STRING(),Types.LONG(),Types.SQL_TIMESTAMP()); //after_dynamic_table
		DataStream<Tuple2<Boolean,Tuple3<String,Long,Timestamp>>> dataStream = sTableEnv.toRetractStream(sqlQuery, tupleTypeInfo);
		
		dataStream.writeAsText("D:\\tmp\\flink");
		//dataStream.print();
		env.execute();
	}
	
	
}
