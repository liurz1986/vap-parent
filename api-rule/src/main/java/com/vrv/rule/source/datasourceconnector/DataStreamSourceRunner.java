package com.vrv.rule.source.datasourceconnector;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.source.datasourceparam.DataStreamRunnerParamsAbs;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

/**
 * 数据源启动类
 */
public interface DataStreamSourceRunner<T extends DataStreamRunnerParamsAbs> {


    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, T params);


}
