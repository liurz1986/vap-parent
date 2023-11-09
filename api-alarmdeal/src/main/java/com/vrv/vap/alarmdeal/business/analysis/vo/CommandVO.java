package com.vrv.vap.alarmdeal.business.analysis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 告警类型命令传参
 * @author vrv
 *
 */
@Data
@ApiModel(value="告警类型命令传参")
public class CommandVO {
	@ApiModelProperty(value="用户名称")
	private String userName;
	@ApiModelProperty(value="用户Id")
	private String userId;
	@ApiModelProperty(value="告警处理类型Id")
	private String alarmItemId;
	@ApiModelProperty(value="告警处理信息")
	private String jsonInfo;
}
