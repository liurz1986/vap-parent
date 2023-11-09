package com.vrv.vap.alarmdeal.business.flow.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value="工作流表单数据")
public class WorkDataVO {
	@ApiModelProperty(value="form表单信息")
	private Map<String, Object> forms;
	@ApiModelProperty(value="流程标识guid")
	private String processdefGuid;
	@ApiModelProperty(value="流程实例名称")
	private String name;
	@ApiModelProperty(value="流程实例code")
	private String code;
	@ApiModelProperty(value="当前登陆用户userId")
	private String userId;
	@ApiModelProperty(value="当前登陆用户userName")
	private String userName;
	@ApiModelProperty(value="逾期时间")
	private String deadlineDate ;

}
