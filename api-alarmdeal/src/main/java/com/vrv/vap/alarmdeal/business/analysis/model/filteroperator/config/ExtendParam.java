package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import java.io.Serializable;

import lombok.Data;

/**
 *额外属性字段类型
 */
@Data
public class ExtendParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String label;
	private String fieldName;
}
