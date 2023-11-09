package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="资产类型对象")
public class AssetTypeVO implements Serializable, Comparable<AssetTypeVO>{

	private static final long serialVersionUID = -6029806290343244650L;
	
	@ApiModelProperty(value="资产类型guid")
	private String guid;
	@ApiModelProperty(value="资产类型编码")
	private String treeCode;
	@ApiModelProperty(value="资产类型唯一标识")
	private String uniqueCode;
	@ApiModelProperty(value="资产类型名称")
	private String name;
	@ApiModelProperty(value="资产类型英文名称")
	private String nameEn;
	@ApiModelProperty(value="资产类型图标")
	private String icon;
	@ApiModelProperty(value="资产类型监控方式")
	private String monitorProtocols;
	@ApiModelProperty(value="资产类型树编码头部")
	private String treeCodeHead;
	@ApiModelProperty(value="资产类型树编码尾部")
	private String treeCodeTail;
	@ApiModelProperty(value="一级资产类型guid")
	private String assetTypeGroupGuid;
	@ApiModelProperty(value="资产类型状态")
	private Integer status;
	@ApiModelProperty(value="资产类型顺序编号")
	private Integer orderNum;
	
	
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
		int idx = treeCode.lastIndexOf("-");
		this.treeCodeHead = treeCode.substring(0, idx + 1);
		this.treeCodeTail = treeCode.substring(idx + 1, treeCode.length());

	}
	
	@Override
	public int compareTo(AssetTypeVO o) {
		return this.getNameEn().compareTo(o.getNameEn());
	}
	
	
}
