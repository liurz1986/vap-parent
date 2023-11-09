package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GuidNameVO {
	private String guid ;  
	private String name ;  
	
	public GuidNameVO(String guid_,String name_)
	{
		this.guid=guid_;
		this.name=name_;
	}
}
