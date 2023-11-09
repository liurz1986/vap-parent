package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2022/6/6
 * @description
 */
public class AssetTypeRelQuery extends Query {
    @ApiModelProperty("审计类型")
    @QueryLike
    private String auditType;
    @ApiModelProperty("资产类型名称")
    @QueryLike
    private String assetTypeName;
    /**
     *  1：主审，2：准入，3：运管
     */
    @ApiModelProperty("类别")
    private Integer type;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
