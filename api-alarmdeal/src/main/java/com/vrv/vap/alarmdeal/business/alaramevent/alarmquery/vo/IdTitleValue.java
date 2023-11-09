package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

@Data
public class IdTitleValue {
	private String id ;  
	private String title ;  
	private String value ;  
	
 
	public IdTitleValue(String id_, String title_, String value_)
	{
		this.id=id_;
		this.title=title_;
		this.value=value_;
	}
}
