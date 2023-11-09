package com.vrv.rule.ruleInfo.exchangeType.agg;

import java.util.List;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import com.vrv.rule.model.WindowConfig;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 聚合操作器抽象类
 * @author wd-pc
 *
 */
public abstract class AggregationOperatorExecutor {

	public abstract DataStream<Row> aggByDataStream(DataStream<Row> dataStream, 
			List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> outputFieldInfos,WindowConfig windowConfig,String roomType,String... keys);
	
	
}
