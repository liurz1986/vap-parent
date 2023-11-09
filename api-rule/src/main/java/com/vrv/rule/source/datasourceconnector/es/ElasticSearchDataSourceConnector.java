package com.vrv.rule.source.datasourceconnector.es;

import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.source.datasourceconnector.DataSourceConnector;
import com.vrv.rule.source.datasourceparam.impl.EsDatasourceParam;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * es数据源连接器
 */
public class ElasticSearchDataSourceConnector implements DataSourceConnector<EsDatasourceParam> {
    @Override
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, EsDatasourceParam dataSourceParams) {
        List<FieldInfoVO> fieldInfoVOs = dataSourceParams.getFieldInfoVOs();
        TypeInformation<Row> typeInformations = TypeInformationClass.getTypeInformationTypes(fieldInfoVOs);
        DataStream<Row> dataStream = env.addSource(new ElasticSearchSource(dataSourceParams), typeInformations);
        return dataStream;
    }
}
