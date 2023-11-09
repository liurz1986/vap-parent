package com.vrv.rule.ruleInfo.exchangeType;

import java.util.ArrayList;
import java.util.List;

import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;

/**
 * 数据流转换执行者
 * @author wd-pc
 *
 */
public class ExchangeTypeExecutor {

	private ExchangeType exchangeType;
	private ExchangeVO exchangeVO;
	
	public ExchangeTypeExecutor(ExchangeType exchangeType,ExchangeVO exchangeVO) {
		this.exchangeType = exchangeType;
		this.exchangeVO = exchangeVO;
	}
	
	/**
	 * 执行Exchange转换
	 * @return
	 */
	public List<DataStreamSourceVO> executeExchangeType(){
		List<DataStreamSourceVO> list = new ArrayList<>();
		DataStreamSourceVO dataStreamSourceVO = exchangeType.exchangeType(exchangeVO);
		list.add(dataStreamSourceVO);
		return list;
	}
	
	

	
	
	
}
