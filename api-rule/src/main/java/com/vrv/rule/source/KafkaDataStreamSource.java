package com.vrv.rule.source;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.FieldInfoVO;

public class KafkaDataStreamSource {

	private static Logger logger = LoggerFactory.getLogger(KafkaDataStreamSource.class);
	/**
	 * 制造kafka数据
	 * @param localEnvironment
	 * @param fieldInfoList
	 * @return
	 */
	public static DataStream<Row> getDataStreamSource(LocalStreamEnvironment localEnvironment,List<FieldInfoVO> fieldInfoList){
		Properties properties = new Properties();
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		properties.setProperty("bootstrap.servers", url+":"+port);
		String kafkaTopicName = "table-test";
		properties.setProperty("group.id", kafkaTopicName+UUID.randomUUID().toString());
		TypeInformation<Row> row = TypeInformationClass.getTypeInformationTypes(fieldInfoList);
		DataStream<Row> addSource = localEnvironment.addSource(new FlinkKafkaConsumer<>(kafkaTopicName,new JsonRowDeserializationSchema.Builder(row).build(), properties));
		return addSource;
	}
	
	
    
	/**
	 * 获得kafka的源数据
	 * @param env
	 * @param topicName
	 * @param fieldInfoList
	 * @return
	 */
	public static DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env,String topicName,List<FieldInfoVO> fieldInfoList,String groupId){
		Properties properties = new Properties();
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		properties.setProperty("bootstrap.servers", url+":"+port);
		properties.setProperty("group.id", groupId);

		properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000);   //一次性拉取5000条
		properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 5*1048576);

		String kafkaUserName = YmlUtil.getValue("application.yml", "KAFKA_AUTH_USERNAME").toString();
		String kafkaPassword = YmlUtil.getValue("application.yml", "KAFKA_AUTH_PASSWORD").toString();

		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
		properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		properties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username="+kafkaUserName+" password="+kafkaPassword+";");



		TypeInformation<Row> row = TypeInformationClass.getTypeInformationTypes(fieldInfoList);
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(topicName,new CustomJsonRowDeserializationSchema.Builder(row).build() , properties));;
		return addSource;
	}
	
}
