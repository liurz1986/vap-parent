package com.vrv.vap.alarmdeal.business.asset.datasync.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 扩展信息数据
 */
@Data
@Table(name="asset_extend_verify")
@Entity
@ApiModel(value = "资产额外属性")
public class AssetExtendVerify implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="assetGuid")
	private String assetGuid; //资产待审库guid

	@Column(name="extendInfos")
	private String extendInfos; //资产额外属性

}
