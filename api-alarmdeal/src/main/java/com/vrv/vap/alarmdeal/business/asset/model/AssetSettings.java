package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name="asset_settings")
@Entity
@ApiModel(value="资产设置")
public class AssetSettings {
	@Id
	@Column(name="guid")
    @ApiModelProperty(value="主键id") 
	private String guid;  

	private String title;
	
	private String data;
}
