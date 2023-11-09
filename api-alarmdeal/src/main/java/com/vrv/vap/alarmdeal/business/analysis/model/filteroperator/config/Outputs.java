package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import java.io.Serializable;

import lombok.Data;

@Data
public class Outputs implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String type;  //alarmdeal or kafka
	private Configs config;
	
	

	
	
	
}
