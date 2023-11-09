package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 资产类型品牌型号
 * 资产模块用到---20210924
 */
@Data
@Entity
@Table(name="asset_type_sno")
@ApiModel(value = "资产类型品牌型号")
public class AssetTypeSno implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "Guid")
	private String guid;
	@Column(name = "TreeCode")
	private String treeCode;
	@Column(name = "uniqueCode")
	private String uniqueCode;
	@Column(name = "Name")
	private String name;
	@Column(name = "Name_en")
	private String nameEn;
	@Column(name = "Icon")
	private String icon;
	@Column(name = "canSyslog")
	private String canSyslog;
	@Column(name = "canMonitor")
	private String canMonitor;
	@Column(name = "canRCtrl")
	private String canRCtrl;
	@Column(name = "status")
	private Integer status;
	@Column(name = "orderNum")
	private Integer orderNum;

	@Column(name = "predefine")  //是否是内置数据  默认值 false
	private Boolean predefine;
}