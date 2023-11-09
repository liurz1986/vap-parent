package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

@Data
public class LogTableNameVO {

	private String name;
	private String label;
	private String id;
	private String type; //jdbc or logvo or editor
	private String analysisId;

    	
	
}
