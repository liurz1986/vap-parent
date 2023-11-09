package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class IdsVO {
	@ApiModelProperty("id集合，逗号分割")
	private String ids;
}
