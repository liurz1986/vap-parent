package com.vrv.vap.alarmdeal.business.asset.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@Entity
@Table(name="asset_type_template")
@ApiModel(value = "资产类型模板")
public class AssetTypeTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="asset_type_guid")
	private String guid;
	
	@Column(name="form_data")
	private String formdata;
	
	@Column(name="key_data")
	private String keyData;//  title  code 
	
	@Column(name="name")
	private String name;
	
	@Column(name="delete_flag")
	private boolean deleteFlag;
	
}
