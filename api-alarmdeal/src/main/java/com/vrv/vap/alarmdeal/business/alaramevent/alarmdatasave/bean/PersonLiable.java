package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PersonLiable {
	
	@EsField("personLiableName")
	@ApiModelProperty(value = "责任人名字")
	String personLiableName;
	
	@EsField("personLiableCode")
	@ApiModelProperty(value = "责任人code")
	String personLiableCode;

	@EsField("personLiableRole")
	@ApiModelProperty(value = "责任人角色")
	String personLiableRole;
	
	
	@EsField("personLiableOrg")
	@ApiModelProperty(value = "责任人单位")
	String personLiableOrg;
}
