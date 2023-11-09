package com.flink.demo.analysis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import com.flink.demo.vo.POJOTestVO;
import com.vrv.rule.ruleInfo.exchangeType.agg.AggOperatorUtil;
import com.vrv.rule.source.KafkaDataStreamSource;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 维表相关的测试
 * @author wd-pc
 *
 */
public class DimensionTableFunction  {

	public static void main(String[] args) throws Exception {
		LocalStreamEnvironment localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
		List<FieldInfoVO> inputList = POJOTestVO.getDimensionInputFieldInfoList();
		List<FieldInfoVO> outputList = POJOTestVO.getDimensionOutputFieldInfoList();
		DataStream<Row> dataStreamSource = KafkaDataStreamSource.getDataStreamSource(localEnvironment, inputList);
		
		String sql = "select guid,ip from asset where 1=1 and guid = '00025409a5394382aa423b63a4e7c028'";
		
		AssetGuidRiskByJdbc assetGuidRiskByJdbc = new AssetGuidRiskByJdbc(sql,inputList,outputList);
		
		
		//AssetGuidRiskByFilterJdbc assetGuidRiskByFilterJdbc = new AssetGuidRiskByFilterJdbc(sql, inputList, outputList, "collectorIp", "ip");
		
		DataStream<Row> unorderedWait = AsyncDataStream.unorderedWait(dataStreamSource, assetGuidRiskByJdbc, 10, TimeUnit.SECONDS, 1);
		unorderedWait.print();
//		unorderedWait.filter(new FilterFunction<Row>() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//			@Override
//			public boolean filter(Row value) throws Exception {
//				boolean judgeIsInitRow = AggOperatorUtil.judgeIsInitRow(value);
//				return judgeIsInitRow==false;
//			}
//		}).print();
		localEnvironment.execute();
		
	}
       
	
	
	
}
