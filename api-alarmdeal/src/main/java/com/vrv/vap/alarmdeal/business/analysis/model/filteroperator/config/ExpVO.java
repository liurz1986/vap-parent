package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExpVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name; //中文名
	private String field; //英文名a.x
	private String fieldType; //字段类型
	private String operator; //操作符
	private String value; //阈值b.y
	private String value1;
	private String valueType; //constant or attribute
	private String resourceType; //资源类型
	private String resguid; //资源标识
	

	
	
	
	
}
