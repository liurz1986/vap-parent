package com.vrv.rule.source.datasourceconnector.kafka;

import com.vrv.rule.source.CustomJsonRowDeserializationSchema;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.source.datasourceconnector.DataSourceConnector;
import com.vrv.rule.source.datasourceparam.impl.KafkaDataSourceParam;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.List;
import java.util.Properties;

/**
 * kafka连接器
 */
public class KafkaDataSourceConnector implements DataSourceConnector<KafkaDataSourceParam> {
    @Override
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, KafkaDataSourceParam dataSourceParams) {


        Properties properties = getKafkaProperties(dataSourceParams);

        String topicName = dataSourceParams.getTopicName();
        List<FieldInfoVO> fieldInfoVOs = dataSourceParams.getFieldInfoVOs();

        TypeInformation<Row> row = TypeInformationClass.getTypeInformationTypes(fieldInfoVOs);
        DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(topicName,new CustomJsonRowDeserializationSchema.Builder(row).build() , properties));
        return addSource;
    }

    /**
     * 获得kafka对应的数据源
     * @param dataSourceParams
     * @return
     */
    private static Properties getKafkaProperties(KafkaDataSourceParam dataSourceParams) {
        Properties properties = new Properties();
        String url = dataSourceParams.getKafkaUrl();
        String port = dataSourceParams.getKafkaPort();
        String groupId  = dataSourceParams.getGroupId();
        String kafkaUserName = dataSourceParams.getKafkaAuthUserName();
        String kafkaPassword = dataSourceParams.getKafkaAuthPassword();

        properties.setProperty("bootstrap.servers", url+":"+port);
        properties.setProperty("group.id", groupId);

        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000);   //一次性拉取5000条
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 5*1048576);


        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username="+kafkaUserName+" password="+kafkaPassword+";");
        return properties;
    }
}
