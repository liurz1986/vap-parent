package com.vrv.vap.alarmdeal.business.asset.vo;


import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AssetDetailVO {
	@ApiModelProperty(value="资产信息")
	private Asset asset;
	@ApiModelProperty(value="资产扩展信息")
	private AssetExtend assetExtend;
	@ApiModelProperty(value="资产安全域信息")
	private BaseSecurityDomain baseSecurityDomain;
	@ApiModelProperty(value="资产品牌型号")
	private AssetTypeSno assetTypeSno;
	
	@ApiModelProperty(value="资产型号")
	private AssetType assetType;
}
