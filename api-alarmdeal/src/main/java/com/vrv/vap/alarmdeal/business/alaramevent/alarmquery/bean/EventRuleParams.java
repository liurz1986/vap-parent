package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ParamsContent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EventRuleParams {

	@ApiModelProperty(value = "时间规则id")
	String eventRuleId;

	@ApiModelProperty(value = "策略ID")
	String ruleCode;

	@ApiModelProperty(value = "规则ID")
	String filterCode;

	@ApiModelProperty(value = "参数类型 0表示没有参数  1表示有一个tab   2表示有多个")
	Integer paramsType;

	@ApiModelProperty(value = "参数正文")
	List<ParamsContent> paramsContents;
}
