package com.vrv.rule.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class MapOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tableName;
	private Integer order;
	

}
