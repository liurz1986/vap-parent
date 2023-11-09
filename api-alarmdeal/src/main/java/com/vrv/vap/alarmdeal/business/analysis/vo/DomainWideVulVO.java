package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.WideVulVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="全网风险查询接口child",parent= WideVulVO.class)
public class DomainWideVulVO extends WideVulVO {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5023699884993808326L;
	@ApiModelProperty(value="安全域code")
	private String domainCode;
	
}
