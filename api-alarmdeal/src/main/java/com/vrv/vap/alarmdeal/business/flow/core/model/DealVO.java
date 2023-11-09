package com.vrv.vap.alarmdeal.business.flow.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="处理对象")
public class DealVO {
	@ApiModelProperty(value="任务Id")
	private String taskId;   // businessTaskId
	@ApiModelProperty(value="流程实例Id")
	private String processInstanceId;   // businessTaskId
	@ApiModelProperty(value="流程流转操作")
	private String action;
	@ApiModelProperty(value="工单建议")
	private String advice;
	@ApiModelProperty(value="用户userId")
	private String userId;
	@ApiModelProperty(value="业务参数")
	private List<Map<String,Object>> params;
	@ApiModelProperty(value = "事件id")
	private String eventId;
}
