package com.vrv.rule.source.datasourceconnector.mysql;

import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.source.datasourceconnector.DataSourceConnector;
import com.vrv.rule.source.datasourceparam.impl.MysqlDatasourceParam;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * mysql数据源连接器
 */
public class MysqlDataSourceConnector implements DataSourceConnector<MysqlDatasourceParam> {
    @Override
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, MysqlDatasourceParam dataSourceParams) {
        List<FieldInfoVO> fieldInfoVOs = dataSourceParams.getFieldInfoVOs();
        TypeInformation<Row> typeInformations = TypeInformationClass.getTypeInformationTypes(fieldInfoVOs);
        DataStream<Row> dataSource = env.addSource(new MysqlSource(dataSourceParams),typeInformations);
        return dataSource;
    }
}
