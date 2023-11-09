package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "visual_report_java")
public class VisualReportJava {
    /**
     * 报告ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    @Column(name = "subTitle")
    private String subtitle;

    /**
     * 时间范围，如7d、6m、3y
     */
    @Column(name = "time_range")
    private String timeRange;

    @ApiModelProperty("类别ID")
    @Column(name = "catalog_id")
    private Integer catalogId;

    /**
     * 模板组
     */
    private String models;

    /**
     * 获取报告ID
     *
     * @return id - 报告ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置报告ID
     *
     * @param id 报告ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取标题
     *
     * @return title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取副标题
     *
     * @return subTitle - 副标题
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * 设置副标题
     *
     * @param subtitle 副标题
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * 获取时间范围，如7d、6m、3y
     *
     * @return time_range - 时间范围，如7d、6m、3y
     */
    public String getTimeRange() {
        return timeRange;
    }

    /**
     * 设置时间范围，如7d、6m、3y
     *
     * @param timeRange 时间范围，如7d、6m、3y
     */
    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    /**
     * 获取模板组
     *
     * @return models - 模板组
     */
    public String getModels() {
        return models;
    }

    /**
     * 设置模板组
     *
     * @param models 模板组
     */
    public void setModels(String models) {
        this.models = models;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }
}