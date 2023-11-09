package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *告警状态改变
 */
@Data
@ApiModel(value="告警状态修改对象")
public class AnalysisStatusVO {
	@ApiModelProperty(value="告警状态id")
	private String id;
	@ApiModelProperty(value="告警状态")
	private String status;
	@ApiModelProperty(value="告警处置人")
	private String dealPerson;
	
}
