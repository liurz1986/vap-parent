package com.vrv.rule.ruleInfo.exchangeType.join;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.types.Row;

/**
 * Join的KeySelector查询
 * @author wd-pc
 *
 */
public class KeySelectorFunction implements KeySelector<Row, String> {

	private Integer order;
	
	public KeySelectorFunction(Integer order) {
		this.order = order;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getKey(Row row) throws Exception {
		String value = row.getField(order).toString();
		return value;
	}

	

}
