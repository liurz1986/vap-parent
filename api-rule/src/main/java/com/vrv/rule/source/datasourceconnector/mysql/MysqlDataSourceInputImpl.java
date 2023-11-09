package com.vrv.rule.source.datasourceconnector.mysql;

import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.source.datasourceconnector.DataSourceStreamInputAbs;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.impl.MysqlRunnerParams;
import com.vrv.rule.vo.DataStreamInputVO;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * kafka数据源输入
 */
public class MysqlDataSourceInputImpl extends DataSourceStreamInputAbs {
    @Override
    public DataStream<Row> getDataSourceStreamInput(StreamExecutionEnvironment env, DataStreamInputVO dataStreamInputVO, List<FieldInfoVO> fieldInfoVOs) {
        Tables tables = dataStreamInputVO.getTables();
        String name = tables.getName();
        String startConfig = dataStreamInputVO.getStartConfig();

        MysqlRunnerParams mysqlRunnerParams = MysqlRunnerParams.builder().tableName(name).startConfig(startConfig).build();
        mysqlRunnerParams.setFieldInfoVOs(fieldInfoVOs);
        DataStreamSourceRunner<MysqlRunnerParams> kafkaDataStreamSourceRunner = new MysqlDataStreamSourceRunner();
        DataStream<Row> dataStreamSource = kafkaDataStreamSourceRunner.getDataStreamSource(env, mysqlRunnerParams);
        return dataStreamSource;

    }
}
