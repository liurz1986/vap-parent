package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 逻辑运算符(逻辑树)
 * @author wd-pc
 *
 */
@Data
public class LogicOperator implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ExpVO exp;
	private List<LogicOperator> filters;
	private String key;
	private String parent;
	private String type; //AND,OR,filter
	




	
}
