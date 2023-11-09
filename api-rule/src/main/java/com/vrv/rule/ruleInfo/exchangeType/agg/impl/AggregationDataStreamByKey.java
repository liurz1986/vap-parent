package com.vrv.rule.ruleInfo.exchangeType.agg.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import com.vrv.rule.model.WindowConfig;
import com.vrv.rule.ruleInfo.exchangeType.agg.AggregationOperatorExecutor;
import com.vrv.rule.ruleInfo.exchangeType.agg.ReduceAccFunction;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 根据key进行聚合操作
 * reduce只能够合并相同列数的属性，列不同无法进行对应的合并操作。（无法支持当前添加聚合函数的情况）
 * @author wd-pc
 *
 */
public class AggregationDataStreamByKey extends AggregationOperatorExecutor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	@Override
	public DataStream<Row> aggByDataStream(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			List<FieldInfoVO> outputFieldInfos,WindowConfig windowConfig,String roomType,String... keys) {
		dataStream = getMapRowData(dataStream, inputFieldInfos, outputFieldInfos);
		dataStream = dataStream.keyBy(keys).reduce(new ReduceAccFunction(outputFieldInfos, inputFieldInfos, false, roomType));
		return dataStream;
	
	}

	/**
	 * 获得Map关于Row的Data的数据（把输入的流数据换成和outputField个数相同的row）
	 * @param dataStream
	 * @param inputFieldInfos
	 * @param outputFieldInfos
	 * @return
	 */
	private DataStream<Row> getMapRowData(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			List<FieldInfoVO> outputFieldInfos) {
		TypeInformation<Row> outTypeInformation  = TypeInformationClass.getTypeInformationTypes(outputFieldInfos);
		dataStream = dataStream.map(new MapFunction<Row, Row>() {
			private static final long serialVersionUID = 1L;
            //TODO outputSize>inputsize
			//TODO 只考虑满数据情况，由于重新排序会将褶皱属性字段排在前面，所有将普通属性放置在后面(1,2,3,4,6)------->(null,null,null,1,2,3,4,6)
			@Override
			public Row map(Row row) throws Exception {
				int outputSize = outputFieldInfos.size();
				int inputsize = inputFieldInfos.size();
				Row outPutRow = new Row(outputSize);
				if(outputSize>inputsize) {
					int lastSize = outputSize-inputsize;
					for (int i = lastSize; i <outputSize; i++) {
						outPutRow.setField(i, row.getField(i-lastSize));
					}
				}else {  
					setOutRowNumltInRow(inputFieldInfos, outputFieldInfos, row, outPutRow);
				}
				return outPutRow;
			}
			
		}).returns(outTypeInformation);
		return dataStream;
	}
	
	/**
	 * 设置输出数据小于输入数据
	 * @param inputFieldInfos
	 * @param outputFieldInfos
	 * @param row
	 * @param outPutRow
	 */
	private void setOutRowNumltInRow(List<FieldInfoVO> inputFieldInfos, List<FieldInfoVO> outputFieldInfos,
			Row row, Row outPutRow) {
		//输出的Row的元素数小于输入的Row的元素数
		for (FieldInfoVO outPutFieldInfoVO : outputFieldInfos) {
			String outputFieldName = outPutFieldInfoVO.getFieldName();
			for (FieldInfoVO inputFieldInfoVO : inputFieldInfos) {
				String inputFieldName = inputFieldInfoVO.getFieldName();
				if(inputFieldName.equals(outputFieldName)) {
					Object inputField = row.getField(inputFieldInfoVO.getOrder());
					outPutRow.setField(outPutFieldInfoVO.getOrder(), inputField);
					break;
				}
			}
		}
	}
	
}
