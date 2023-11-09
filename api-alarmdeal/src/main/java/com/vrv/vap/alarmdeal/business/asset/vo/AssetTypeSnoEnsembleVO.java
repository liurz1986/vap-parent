package com.vrv.vap.alarmdeal.business.asset.vo;


import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel(value="资产类型品牌集合体")
public class AssetTypeSnoEnsembleVO {
    @ApiModelProperty(value="品牌实体")
	private AssetTypeSno assetTypeSno;
    @ApiModelProperty(value="品牌模板实体")
	private AssetTypeTemplate AssetTypeTemplate;
    
    //@ApiModelProperty(value="品牌监控实体")
}
