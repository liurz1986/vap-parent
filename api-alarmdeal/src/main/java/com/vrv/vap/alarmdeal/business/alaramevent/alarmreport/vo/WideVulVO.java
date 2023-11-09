package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="全网风险查询接口")
public class WideVulVO implements Serializable {

	private static final long serialVersionUID = -7183519490033702610L;

	@ApiModelProperty(value="开始时间")
	private String startTime;
	
	@ApiModelProperty(value="结束时间")
	private String endTime;
}
