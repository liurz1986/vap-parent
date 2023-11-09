package com.vrv.rule.source.datasourceconnector.es;

import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.source.datasourceconnector.DataSourceStreamInputAbs;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.impl.ESRunnerParams;
import com.vrv.rule.vo.DataStreamInputVO;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.List;


/**
 * ES数据源输入
 */

public class EsDataSourceInputImpl  extends DataSourceStreamInputAbs {
    @Override
    public DataStream<Row> getDataSourceStreamInput(StreamExecutionEnvironment env, DataStreamInputVO dataStreamInputVO, List<FieldInfoVO> fieldInfoVOs) {
        Tables tables = dataStreamInputVO.getTables();
        String indexName = tables.getName();
        String startConfig = dataStreamInputVO.getStartConfig();
        ESRunnerParams esRunnerParams = new ESRunnerParams();
        esRunnerParams.setIndexName(indexName+"-*");  //TODO索引加匹配
        esRunnerParams.setStartConfig(startConfig);
        esRunnerParams.setFieldInfoVOs(fieldInfoVOs);
        DataStreamSourceRunner<ESRunnerParams> esRunnerParamsDataStreamSourceRunner =  new ElasticSearchDataSourceRunner();
        DataStream<Row> dataStreamSource = esRunnerParamsDataStreamSourceRunner.getDataStreamSource(env, esRunnerParams);
        return dataStreamSource;
    }
}
