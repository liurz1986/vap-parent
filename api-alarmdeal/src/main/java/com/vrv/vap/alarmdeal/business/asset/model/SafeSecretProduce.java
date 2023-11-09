package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 编排记录表
 *
 * @author vrv
 * @date 2021-08-12
 */
@Data
@Table(name="safe_secret_produce")
@Entity
@ApiModel(value = "编排记录表")
public class SafeSecretProduce implements Serializable {

    @Id
    @Column(name="guid")
    private String guid;

    // 产品名称
    @Column(name="name")
    private String name;

    // 生产厂商
    @Column(name="manufacturer")
    private String manufacturer;

    // 版本号
    @Column(name="version")
    private String version;

    // 关联asset表guid
    @Column(name="asset_guid")
    private String assetGuid;

}
