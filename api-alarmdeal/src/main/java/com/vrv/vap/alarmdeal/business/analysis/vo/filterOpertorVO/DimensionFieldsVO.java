package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DimensionFieldsVO {

	@ApiModelProperty(value="主键") 
	private String guid;
	@ApiModelProperty(value="字段名称")
	private String fieldName;
	@ApiModelProperty(value="字段长度")
	private int fieldLength;
	@ApiModelProperty(value="字段类型")
	private String fieldType;
	@ApiModelProperty(value="字段描述")
	private String fieldDesc;
	@ApiModelProperty(value="关联维表guid")
	private String tableGuid;
	
	private   String  enumType;
	private   String  formatType;
	@ApiModelProperty(value="别名")
	private   String  aliasName;//别名
	@ApiModelProperty(value="排序字段") 
	private String order_; 
	@ApiModelProperty(value="排序顺序") 
	private String by_; 
	@ApiModelProperty(value="起始行") 
	private Integer start_;
	@ApiModelProperty(value="页个数")
	private Integer count_;
}
