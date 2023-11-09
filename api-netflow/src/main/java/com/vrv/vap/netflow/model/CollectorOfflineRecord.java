package com.vrv.vap.netflow.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2022/3/28
 * @description
 */
@Table(name = "collector_offline_record")
public class CollectorOfflineRecord {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("创建人")
    private String creator;
    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("模板ID")
    @Column(name = "template_id")
    private Integer templateId;
    @ApiModelProperty("模板名称")
    @Column(name = "template_name")
    private String templateName;
    @Ignore
    @Column(name = "collector_id")
    private Integer collectorId;
    @ApiModelProperty("接入任务名称")
    @Column(name = "collector_name")
    private String collectorName;
    @Ignore
    @Column(name = "error_file")
    private String errorFile;
    @Ignore
    @Column(name = "total_count")
    private Integer totalCount;
    @Ignore
    @Column(name = "success_count")
    private Integer successCount;
    @Ignore
    @Column(name = "error_count")
    private Integer errorCount;
    @ApiModelProperty("类型")
    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Integer collectorId) {
        this.collectorId = collectorId;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getErrorFile() {
        return errorFile;
    }

    public void setErrorFile(String errorFile) {
        this.errorFile = errorFile;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
