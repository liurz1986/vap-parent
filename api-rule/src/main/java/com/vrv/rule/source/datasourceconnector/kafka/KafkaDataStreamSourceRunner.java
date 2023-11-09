package com.vrv.rule.source.datasourceconnector.kafka;

import com.vrv.rule.source.datasourceconnector.DataSourceConnector;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.impl.KafkaDataSourceParam;
import com.vrv.rule.source.datasourceparam.impl.KafkaRunnerParams;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * kafka数据源启动类
 */
public class KafkaDataStreamSourceRunner implements DataStreamSourceRunner<KafkaRunnerParams> {
    @Override
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, KafkaRunnerParams kafkaRunnerParams) {

        KafkaDataSourceParam kafkaDataSourceParam = constructKafkaParam(kafkaRunnerParams);
        DataSourceConnector<KafkaDataSourceParam> dataSourceConnector = new KafkaDataSourceConnector();
        DataStream<Row> dataStreamSource = dataSourceConnector.getDataStreamSource(env, kafkaDataSourceParam);
        return dataStreamSource;
    }

    /**
     * 构造
     * @param kafkaRunnerParams
     * @return
     */
    private static KafkaDataSourceParam constructKafkaParam(KafkaRunnerParams kafkaRunnerParams) {
        String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
        String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
        String kafkaUserName = YmlUtil.getValue("application.yml", "KAFKA_AUTH_USERNAME").toString();
        String kafkaPassword = YmlUtil.getValue("application.yml", "KAFKA_AUTH_PASSWORD").toString();
        String topicName = kafkaRunnerParams.getTopicName();
        List<FieldInfoVO> fieldInfoVOs = kafkaRunnerParams.getFieldInfoVOs();
        String groupId = kafkaRunnerParams.getGroupId();
        KafkaDataSourceParam kafkaDataSourceParam = KafkaDataSourceParam.builder().kafkaUrl(url).kafkaPort(port)
                .kafkaAuthUserName(kafkaUserName).kafkaAuthPassword(kafkaPassword)
                .groupId(groupId).topicName(topicName).build();

        kafkaDataSourceParam.setFieldInfoVOs(fieldInfoVOs);
        return kafkaDataSourceParam;
    }
}
