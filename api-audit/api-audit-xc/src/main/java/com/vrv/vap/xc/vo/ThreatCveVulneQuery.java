package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table threat_cve_vulne
 *
 * @mbg.generated do_not_delete_during_merge 2018-12-03 15:16:30
 */
@ApiModel
@SuppressWarnings("unused")
public class ThreatCveVulneQuery extends Query {
    /**
     * 情报创建者
     */
    @ApiModelProperty("情报创建者")
    private String createdBy;

    /**
     * 情报创建时间
     */
    @ApiModelProperty("情报创建时间")
    private String createdTime;

    /**
     * 信息来源
     */
    @ApiModelProperty("信息来源")
    private String dataSource;

    /**
     * 漏洞编码_中国标准
     */
    @ApiModelProperty("漏洞编码_中国标准")
    private String holeCodeCn;

    /**
     * 漏洞编码_国际标准
     */
    @ApiModelProperty("漏洞编码_国际标准")
    private String holeCodeEn;

    /**
     * 漏洞描述
     */
    @ApiModelProperty("漏洞描述")
    private String holeInfo;

    /**
     * 漏洞名称
     */
    @ApiModelProperty("漏洞名称")
    private String holeName;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @NotNull
    private String id;

    /**
     * 情报修改者
     */
    @ApiModelProperty("情报修改者")
    private String modifiedBy;

    /**
     * 情报修改时间
     */
    @ApiModelProperty("情报修改时间")
    private String modifiedTime;

    /**
     * 应对方式（ID）
     */
    @ApiModelProperty("应对方式（ID）")
    private String potentialCoa;

    /**
     * 攻击目标
     */
    @ApiModelProperty("攻击目标")
    private String target;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getHoleCodeCn() {
        return holeCodeCn;
    }

    public void setHoleCodeCn(String holeCodeCn) {
        this.holeCodeCn = holeCodeCn;
    }

    public String getHoleCodeEn() {
        return holeCodeEn;
    }

    public void setHoleCodeEn(String holeCodeEn) {
        this.holeCodeEn = holeCodeEn;
    }

    public String getHoleInfo() {
        return holeInfo;
    }

    public void setHoleInfo(String holeInfo) {
        this.holeInfo = holeInfo;
    }

    public String getHoleName() {
        return holeName;
    }

    public void setHoleName(String holeName) {
        this.holeName = holeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getPotentialCoa() {
        return potentialCoa;
    }

    public void setPotentialCoa(String potentialCoa) {
        this.potentialCoa = potentialCoa;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}