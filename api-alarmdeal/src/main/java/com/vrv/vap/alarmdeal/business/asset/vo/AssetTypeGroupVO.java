package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="资产类型对象")
public class AssetTypeGroupVO {
	@ApiModelProperty(value="一级资产类型guid")
	private String guid;
	@ApiModelProperty(value="一级资产类型treecode")
	private String treeCode;
	@ApiModelProperty(value="一级资产类型treecode头部")
	private String treeCodeHead;
	@ApiModelProperty(value="一级资产类型treecode尾部")
	private String treeCodeTail;
	@ApiModelProperty(value="一级资产类型名称")
	private String name;
	@ApiModelProperty(value="一级资产类型en名称")
	private String nameEn;
	@ApiModelProperty(value="一级资产类型标识")
	private String icon;
	@ApiModelProperty(value="一级资产类型唯一标识")
	private String uniqueCode;
	@ApiModelProperty(value="一级资产类型状态")
	private Integer status;
	@ApiModelProperty(value="一级资产类型排序")
	private Integer orderNum;

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
		int idx = treeCode.lastIndexOf("-");
		this.treeCodeHead = treeCode.substring(0, idx + 1);
		this.treeCodeTail = treeCode.substring(idx + 1, treeCode.length());

	}
}
