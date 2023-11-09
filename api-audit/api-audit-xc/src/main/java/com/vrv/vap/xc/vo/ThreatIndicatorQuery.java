package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="ThreatIndicator对象", description="")
public class ThreatIndicatorQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "indicator Id")
    private String typeId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "数据类型")
    private String type;

    @ApiModelProperty(value = "攻击模式")
    private String pattern;

    @ApiModelProperty(value = "情报修改者")
    private String modifiedBy;

    @ApiModelProperty(value = "情报修改时间")
    private String modifiedTime;

    @ApiModelProperty(value = "情报创建者")
    private String createdBy;

    @ApiModelProperty(value = "情报创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "情报开始时间")
    private String reportStartTime;

    @ApiModelProperty(value = "情报结束时间")
    private String reportEndTime;

    @ApiModelProperty(value = "IOC类型(domain,url,ipv4,email,md5,sha1,sha256,other)")
    private String labelType;

    @ApiModelProperty(value = "值")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String value;

    @ApiModelProperty(value = "情报创建时间")
    private String labelCreatedTime;

    @ApiModelProperty(value = "情报来源")
    private String sourceRed;

    @ApiModelProperty(value = "动作")
    private String action;

    @ApiModelProperty(value = "最后更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "数据来源 1系统同步 2手工维护")
    private Integer sourceType;

    @ApiModelProperty(value = "描述")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
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
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
    public String getReportStartTime() {
        return reportStartTime;
    }

    public void setReportStartTime(String reportStartTime) {
        this.reportStartTime = reportStartTime;
    }
    public String getReportEndTime() {
        return reportEndTime;
    }

    public void setReportEndTime(String reportEndTime) {
        this.reportEndTime = reportEndTime;
    }
    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getLabelCreatedTime() {
        return labelCreatedTime;
    }

    public void setLabelCreatedTime(String labelCreatedTime) {
        this.labelCreatedTime = labelCreatedTime;
    }
    public String getSourceRed() {
        return sourceRed;
    }

    public void setSourceRed(String sourceRed) {
        this.sourceRed = sourceRed;
    }
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ThreatIndicatorQuery{" +
            "id=" + id +
            ", typeId=" + typeId +
            ", title=" + title +
            ", type=" + type +
            ", pattern=" + pattern +
            ", modifiedBy=" + modifiedBy +
            ", modifiedTime=" + modifiedTime +
            ", createdBy=" + createdBy +
            ", createdTime=" + createdTime +
            ", reportStartTime=" + reportStartTime +
            ", reportEndTime=" + reportEndTime +
            ", labelType=" + labelType +
            ", value=" + value +
            ", labelCreatedTime=" + labelCreatedTime +
            ", sourceRed=" + sourceRed +
            ", action=" + action +
            ", updateTime=" + updateTime +
            ", sourceType=" + sourceType +
            ", description=" + description +
        "}";
    }
}
