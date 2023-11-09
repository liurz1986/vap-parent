package com.vrv.vap.alarmdeal.business.asset.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 资产类型
 * @author wd-pc
 * 资产模块用到---20210924
 */
@Data
@Table(name="asset_type")
@Entity
public class AssetType implements Serializable,Comparable<AssetType> {
	
	private static final long serialVersionUID = -3562609335013647488L;
	@Id
    @Column(name="Guid")
    private String guid;
    @Column(name="TreeCode")
    private String treeCode;
    @Column(name="uniqueCode")
    private String uniqueCode;
    @Column(name="Name")
    private String name;
    @Column(name="Name_en")
    private String nameEn;
    @Column(name="Icon")
    private String icon;
    @Column(name="monitorProtocols")
    private String monitorProtocols;
    //@OneToMany(cascade={CascadeType.ALL}, mappedBy = "assetType")
    //private Set<Asset> assets;
    @Column(name="status")
    private Integer status;
    @Column(name="orderNum")
    private Integer orderNum;
    @Column(name="predefine")  //是否是内置数据  默认值 false
    private Boolean predefine;

    @Override
    public int compareTo(AssetType o) {
		return this.getName().compareTo(o.getName());
	}

}
