package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 
 * @author wd-pc
 *
 */
@Table(name="asset_type_group")
@Entity
@Data
@ApiModel(value = "资产类型大类")
public class AssetTypeGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="Guid")
	private String guid;
	@Column(name="TreeCode")
	private String treeCode;
	@Column(name="Name")
	private String name;
	@Column(name="Name_en")
	private String nameEn;
	@Column(name="Icon")
	private String icon;
	@Column(name="uniqueCode")
	private String uniqueCode;
	@Column(name="status")
	private Integer status;
	@Column(name="orderNum")
	private Integer orderNum;
	
    @Column(name="predefine")  //是否是内置数据  默认值 false
    private Boolean predefine;
}
