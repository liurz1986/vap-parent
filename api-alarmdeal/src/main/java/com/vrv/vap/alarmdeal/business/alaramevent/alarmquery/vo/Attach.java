package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

@Data
public class Attach {
   
	private String id; //对应的主键
	private String type; //key or window
	private String options;// value of key or value of window
}
