package com.vrv.rule.ruleInfo.exchangeType;

import java.util.List;

import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;

/**
 * 交换数据类型接口
 * @author wd-pc
 *
 */
public interface ExchangeType {

	/**
	 * exchange转换接口
	 * @param exchangeVO
	 * @return
	 */
	public DataStreamSourceVO exchangeType(ExchangeVO exchangeVO);
	
	
}
