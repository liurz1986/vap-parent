package com.vrv.rule.ruleInfo.exchangeType.agg;

import java.util.List;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.types.Row;

import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 聚合类方法函数
 * @author wd-pc
 *
 */
public class AcculateAggregteFuntion implements AggregateFunction<Row, Row, Row> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FieldInfoVO> outputFieldInfos;
	private List<FieldInfoVO> inputFieldInfos;
	private String roomType; //盒子类型
	
	
	
	public AcculateAggregteFuntion(List<FieldInfoVO> outputFieldInfos,List<FieldInfoVO> inputFieldInfos,String roomType){
		this.outputFieldInfos = outputFieldInfos;
		this.inputFieldInfos = inputFieldInfos;
		this.roomType = roomType;
		
	}
	
	

	@Override
	public Row createAccumulator() {     // the methods for creating an initial accumulator
		Row row = new Row(outputFieldInfos.size());
		return row;
	}

	@Override
	public Row add(Row inputRow, Row accumulator) {
		if(AggOperatorUtil.judgeIsInitRow(accumulator)){ //accumulator is initRow
			AggOperatorUtil.setSameFieldRow(inputFieldInfos, outputFieldInfos, inputRow, accumulator);
			AggOperatorUtil.getAggregateOperator(accumulator, outputFieldInfos,inputRow,inputFieldInfos);
			return accumulator;
		}else{
			AggOperatorUtil.setSameFieldRowWithoutGuid(inputFieldInfos, outputFieldInfos, inputRow, accumulator);
			AggOperatorUtil.getAggregateOperator(accumulator, outputFieldInfos,inputRow,inputFieldInfos);
			accumulator = ExchangeUtil.setRoomInfoByType(inputRow, accumulator, roomType, outputFieldInfos, inputFieldInfos);
			return accumulator;
		}
		
	}

	
	
	
	@Override
	public Row getResult(Row accumulator) {
		return accumulator;
	}

	@Override
	public Row merge(Row a, Row b) {
		return null;
	}

}
