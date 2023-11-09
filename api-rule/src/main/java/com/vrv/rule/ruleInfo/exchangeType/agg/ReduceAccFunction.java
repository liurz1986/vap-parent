package com.vrv.rule.ruleInfo.exchangeType.agg;

import java.util.List;
import java.util.UUID;

import org.apache.flink.api.common.functions.RichReduceFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * flink1.12通过reduce完成
 * @author Administrator
 *
 */
public class ReduceAccFunction extends RichReduceFunction<Row> {

	
	private static Logger logger = LoggerFactory.getLogger(ReduceAccFunction.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
     
	
	private transient ValueState<Row> rowStatus;   //row对应的状态
	
	
	
	private List<FieldInfoVO> outputFieldInfos;  //输出字段类型
	private List<FieldInfoVO> inputFieldInfos;   //输入字段类型
	private String roomType; //盒子类型
	//private Row initRow;
	private Boolean initResult; //初始值
	
	public ReduceAccFunction(List<FieldInfoVO> outputFieldInfos,List<FieldInfoVO> inputFieldInfos,Boolean initResult,String roomType) {
		this.initResult = initResult;
		this.outputFieldInfos = outputFieldInfos;
		this.inputFieldInfos = inputFieldInfos;
		this.roomType = roomType;
	}
	
	@Override
	public Row reduce(Row acc, Row inputRow) throws Exception {
		  Row initRow = rowStatus.value();
		if(AggOperatorUtil.judgeIsInitRow(initRow)) {  //如果initRow是空
			// 初始化配置acc Row
			Row outputRows = acc;
			AggOperatorUtil.getAggregateOperator(outputRows, outputFieldInfos,acc,inputFieldInfos);
			AggOperatorUtil.getAggregateOperator(outputRows, outputFieldInfos,inputRow,inputFieldInfos);
			rowStatus.update(outputRows);
			return outputRows;
		}else {
			AggOperatorUtil.getAggregateOperator(acc, outputFieldInfos,inputRow,inputFieldInfos); //进行聚合操作
			acc = ExchangeUtil.setRoomInfoByTypeAndReduce(inputRow, acc, roomType, outputFieldInfos); //进行
			rowStatus.update(acc);
			return acc;
		}
	}
	
	@Override
    @SuppressWarnings("deprecation")
    public void open(Configuration config) {
    	String uuid = UUID.randomUUID().toString();
    	Row outputRow = new Row(outputFieldInfos.size());
		ValueStateDescriptor<Row> descriptor =new ValueStateDescriptor<>(
                        "reduce"+uuid, // the state name
                        TypeInformation.of(Row.class), // type information
                        outputRow); // default value of the state, if nothing was set Row.of(0,0)
		rowStatus = getRuntimeContext().getState(descriptor);
    }
	
	
	

}
