package com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EventAlarmTaskNodeMsg {

	String eventId;
	String taskName;//流程节点名称
	 //候选人的角色和用户id
	@ApiModelProperty(value = "可以操作的用户")
	public List<String>  canOperateUser;
	
	@ApiModelProperty(value = "可以操作的角色")
	public List<String>  canOperateRole;
	
	Integer dealedPersonId; //发生处置操作人员id
	String  dealedPersonName;//发生处置操作人员名称
	List<String> dealedRole;// 发生处置操作角色id
	Integer eventAlarmStatus;  //告警事件状态
	String causeAnalysis;  //成因分析
	
}
