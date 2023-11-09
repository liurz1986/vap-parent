package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新告警处置的VO
 * @author vrv
 *
 */
@Data
@ApiModel(value="更新告警处置对象")
public class UpdateAlarmDealVO {
	@ApiModelProperty(value="告警处置结果")
	private ResultData<Boolean> resultData;
	@ApiModelProperty(value="告警处置类型Id")
	private String alarmItemId;
	@ApiModelProperty(value="告警处置类型")
	private String itemType;
	@ApiModelProperty(value="告警人用户名称")
	private String userName;
	@ApiModelProperty(value="告警人用户Id")
	private String userId;
	
}
