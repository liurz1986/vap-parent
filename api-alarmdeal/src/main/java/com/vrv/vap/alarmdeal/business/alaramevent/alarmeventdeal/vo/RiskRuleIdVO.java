package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 规则VO
 * @author wd-pc
 *
 */
@Data
@ApiModel(value="告警规则对象")
public class RiskRuleIdVO {
     
	@ApiModelProperty(value="批量id")
	private List<String> ids; //批量id
	@ApiModelProperty(value="单个id")
	private String id; //单个id
	@ApiModelProperty(value="状态结果")
	private Boolean result; //状态结果
	
}
