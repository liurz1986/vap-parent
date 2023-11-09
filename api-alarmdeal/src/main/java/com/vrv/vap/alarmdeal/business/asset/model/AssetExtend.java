package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 资产额外属性
 * @author wd-pc
 * 资产模块用到---20210924
 */
@Data
@Table(name="asset_extend")
@Entity
@ApiModel(value = "资产额外属性")
public class AssetExtend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="assetGuid")
	private String assetGuid; //资产guid
	@Column(name="extendInfos")
	private String extendInfos; //资产额外属性

}
