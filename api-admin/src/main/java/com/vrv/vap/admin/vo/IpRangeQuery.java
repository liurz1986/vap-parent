package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="ip范围对象") 
public class IpRangeQuery extends Query {
	@ApiModelProperty(value="起始ip") 
	String beginIp;
	@ApiModelProperty(value="起始ip数值") 
	Long beginNum;
	
	@ApiModelProperty(value="结束ip") 
	String endIp;
	@ApiModelProperty(value="结束ip数值") 
	Long endNum;
}
