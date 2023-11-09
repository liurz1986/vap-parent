package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
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
@ApiModel(value="ThreatReport对象", description="")
public class ThreatReportQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "情报id")
    private String reportId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "情报修改者")
    private String modifiedBy;

    @ApiModelProperty(value = "情报修改时间")
    private String modifiedTime;

    @ApiModelProperty(value = "情报创建者")
    private String createdBy;

    @ApiModelProperty(value = "情报创建时间")
    private String createdTime;

    @ApiModelProperty(value = "情报开始时间")
    private String startTime;

    @ApiModelProperty(value = "情报结束时间")
    private String endTime;

    @ApiModelProperty(value = "情报格式版本")
    private String version;

    @ApiModelProperty(value = "情报observations id")
    private String observations;

    @ApiModelProperty(value = "情报indicators id")
    private String indicators;

    @ApiModelProperty(value = "情报ttp id")
    private String ttp;

    @ApiModelProperty(value = "情报campaign id")
    private String campaign;

    @ApiModelProperty(value = "情报exploit_taget id")
    private String exploitTaget;

    @ApiModelProperty(value = "情报incidents id")
    private String incidents;

    @ApiModelProperty(value = "情报threat_actors id")
    private String threatActors;

    @ApiModelProperty(value = "情报Courses_of_action id")
    private String coursesOfAction;

    @ApiModelProperty(value = "TLP分级(red, yellow,green)")
    private String tlp;

    @ApiModelProperty(value = "是否公开(public,private)")
    private String status;

    @ApiModelProperty(value = "威胁分类标签")
    private String threatType;

    @ApiModelProperty(value = "行业标签")
    private String industry;

    @ApiModelProperty(value = "最后更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "数据来源 1系统同步 2手工维护")
    private Integer sourceType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
    public String getIndicators() {
        return indicators;
    }

    public void setIndicators(String indicators) {
        this.indicators = indicators;
    }
    public String getTtp() {
        return ttp;
    }

    public void setTtp(String ttp) {
        this.ttp = ttp;
    }
    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }
    public String getExploitTaget() {
        return exploitTaget;
    }

    public void setExploitTaget(String exploitTaget) {
        this.exploitTaget = exploitTaget;
    }
    public String getIncidents() {
        return incidents;
    }

    public void setIncidents(String incidents) {
        this.incidents = incidents;
    }
    public String getThreatActors() {
        return threatActors;
    }

    public void setThreatActors(String threatActors) {
        this.threatActors = threatActors;
    }
    public String getCoursesOfAction() {
        return coursesOfAction;
    }

    public void setCoursesOfAction(String coursesOfAction) {
        this.coursesOfAction = coursesOfAction;
    }
    public String getTlp() {
        return tlp;
    }

    public void setTlp(String tlp) {
        this.tlp = tlp;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getThreatType() {
        return threatType;
    }

    public void setThreatType(String threatType) {
        this.threatType = threatType;
    }
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
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

    @Override
    public String toString() {
        return "ThreatReportQuery{" +
            "id=" + id +
            ", reportId=" + reportId +
            ", title=" + title +
            ", modifiedBy=" + modifiedBy +
            ", modifiedTime=" + modifiedTime +
            ", createdBy=" + createdBy +
            ", createdTime=" + createdTime +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", version=" + version +
            ", observations=" + observations +
            ", indicators=" + indicators +
            ", ttp=" + ttp +
            ", campaign=" + campaign +
            ", exploitTaget=" + exploitTaget +
            ", incidents=" + incidents +
            ", threatActors=" + threatActors +
            ", coursesOfAction=" + coursesOfAction +
            ", tlp=" + tlp +
            ", status=" + status +
            ", threatType=" + threatType +
            ", industry=" + industry +
            ", updateTime=" + updateTime +
            ", sourceType=" + sourceType +
        "}";
    }
}
