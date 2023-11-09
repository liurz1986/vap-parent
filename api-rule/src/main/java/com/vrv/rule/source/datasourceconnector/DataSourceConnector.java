package com.vrv.rule.source.datasourceconnector;

import com.vrv.rule.source.datasourceparam.DataSourceParamsAbs;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

/**
 * datasource连接器
 */
public interface DataSourceConnector<T extends DataSourceParamsAbs> {

    /**
     * 获得datastreamsource
     * @param env
     * @param dataSourceParams
     * @return
     */
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, T dataSourceParams);

}
