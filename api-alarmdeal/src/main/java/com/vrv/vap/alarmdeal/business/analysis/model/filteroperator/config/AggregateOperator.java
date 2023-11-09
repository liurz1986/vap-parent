package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import java.io.Serializable;

import lombok.Data;

/**
 * 合并运算符
 * @author wd-pc
 *
 */
@Data
public class AggregateOperator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String operator;   //运算符 count,sum,avg,max,min
	private String sourceTable; //sourceTableId名称
	private String field;   //字段名称
	private LogicOperator loginExp;  //逻辑表达式
	
}
