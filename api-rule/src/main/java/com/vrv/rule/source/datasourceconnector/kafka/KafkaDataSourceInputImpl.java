package com.vrv.rule.source.datasourceconnector.kafka;

import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.source.datasourceconnector.DataSourceStreamInputAbs;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.impl.KafkaRunnerParams;
import com.vrv.rule.vo.DataStreamInputVO;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * kafka数据源输入
 */
public class KafkaDataSourceInputImpl extends DataSourceStreamInputAbs {
    @Override
    public DataStream<Row> getDataSourceStreamInput(StreamExecutionEnvironment env, DataStreamInputVO dataStreamInputVO, List<FieldInfoVO> fieldInfoVOs) {
        Tables tables = dataStreamInputVO.getTables();
        String topicName = tables.getTopicName();
        String groupId = dataStreamInputVO.getGroupId();
        KafkaRunnerParams kafkaRunnerParams = KafkaRunnerParams.builder().topicName(topicName).groupId(groupId).build();
        kafkaRunnerParams.setFieldInfoVOs(fieldInfoVOs);
        DataStreamSourceRunner<KafkaRunnerParams> kafkaDataStreamSourceRunner = new KafkaDataStreamSourceRunner();
        DataStream<Row> dataStreamSource = kafkaDataStreamSourceRunner.getDataStreamSource(env, kafkaRunnerParams);
        return dataStreamSource;
    }
}
