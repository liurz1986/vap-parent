package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="告警分类查询实体")
public class EventTypeSearchVO {
 
	Integer weight;
	String  stime;
	String  etime;
	Boolean isApp=false;
	String  event_group;
	String statusEnum;
	String systemId;//应用系统id
}
