package com.flink.demo.sqltest.join;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.wmstrategies.BoundedOutOfOrderTimestamps;
import org.apache.flink.types.Row;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 上午10:18:52 
* 类说明 
*/
public class OrderInfoSource implements StreamTableSource<Row>,DefinedRowtimeAttributes {

	
	public static final String KAFKA_CONSUMER_URL = "192.168.120.104:9092";
	public static final String TOPIC_CONSUMER_NAME = "order-consumer";
	public static final String KAFKA_GROUP_ID = "test";
	
	
	@Override
	public String explainSource() {
		//TODO 流数据源说明
		return "OrderSource";
	}

	@Override
	public TypeInformation<Row> getReturnType() {
		TypeInformation<Row> types = getTypeInformationTypes();
		return types;
	}

	@Override
	public TableSchema getTableSchema() {
		return new TableSchema(
				new String[] {"id","user_id","order_no","trigger_time"},
				new TypeInformation[] {Types.STRING(), Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP()});
	}

	@Override
	public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
		return Collections.
			   singletonList(new RowtimeAttributeDescriptor("trigger_time", 
					         new ExistingField("trigger_time"), 
					         new BoundedOutOfOrderTimestamps(1000))); 
	}

	@SuppressWarnings("deprecation")
	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CONSUMER_URL);
		properties.setProperty("group.id", KAFKA_GROUP_ID);
		TypeInformation<Row> row = getTypeInformationTypes();
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(TOPIC_CONSUMER_NAME,new JsonRowDeserializationSchema(row) , properties));
		return addSource;
	}

	/**
	 * 获得TypeInformationTypes对应的信息
	 * @return
	 */
	private static TypeInformation<Row> getTypeInformationTypes() {
		String[] names = new String[] {"id","user_id","order_no","trigger_time"};
		TypeInformation[] types = new TypeInformation[] {Types.STRING(), Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP()};
		TypeInformation<Row> row = Types.ROW(names, types);
		return row;
	}
	

}
