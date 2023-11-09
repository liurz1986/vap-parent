package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventUrge;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryIdsVO  extends AlarmEventUrge {
	@ApiModelProperty(value="批量id")
	private List<String> ids; //批量id
	
	@ApiModelProperty(value="消息 冗余字段")
	String  msg;

	@JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	Date eventCreattime;
}
