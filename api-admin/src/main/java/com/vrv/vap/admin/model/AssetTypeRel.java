package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2022/6/2
 * @description
 */
@Table(name = "asset_type_rel")
public class AssetTypeRel {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "audit_type")
    @ApiModelProperty("主审类型")
    private String auditType;

    @Column(name = "asset_type_name")
    @ApiModelProperty("资产类型名称")
    private String assetTypeName;

    @Column(name = "asset_type_guid")
    @ApiModelProperty("资产类型名称")
    private String assetTypeGuid;
    @ApiModelProperty("类别")
    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public String getAssetTypeGuid() {
        return assetTypeGuid;
    }

    public void setAssetTypeGuid(String assetTypeGuid) {
        this.assetTypeGuid = assetTypeGuid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
