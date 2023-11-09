package com.vrv.vap.alarmdeal.business.asset.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Table(name="asset_system_attribute_settings")
@Entity
@ApiModel(value="资产系统属性设置")
public class AssetSystemAttributeSettings  implements Serializable {
	@Id
	@Column(name="guid")
    @ApiModelProperty(value="主键id") 
	private String guid;  

	private String name;
	private String type;
	private Boolean visible;

	@Column(name="system_settings")
	private String systemSettings;

	@Column(name="custom_settings")
	private String customSettings;

	@Column(name="asset_settings_guid")
	private String assetSettingsGuid;
	
	private String panel;
	
}
