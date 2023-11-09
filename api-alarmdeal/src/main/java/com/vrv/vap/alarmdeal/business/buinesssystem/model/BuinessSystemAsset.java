package com.vrv.vap.alarmdeal.business.buinesssystem.model;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 业务系统与资产关联表
 *  2022-11-16
 */
@Data
@Entity
@Table(name="busisystem_asset")
public class BuinessSystemAsset {

    @Id
    @Column(name="guid")
    private String guid;

    @Column(name="sysdomain_guid")
    private String sysdomainGuid; // 业务资产id

    @Column(name="asset_guid")
    private String assetGuid; // 资产id

    @Column(name="asset_order")
    private int assetOrder; // 排序
}
