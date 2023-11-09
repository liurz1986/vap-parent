package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2020/7/23
 * @description 报告实体类
 */
@Table(name = "visual_report")
@ApiModel("报告实体类")
public class Report {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("副标题")
    @Column(name = "subTitle")
    private String subTitle;

    @ApiModelProperty("主题风格")
    @Column(name = "theme_id")
    private Integer themeId;

    @ApiModelProperty("数据模板")
    @Column(name = "ui_state")
    private String uiState;

    @ApiModelProperty("参数")
    private String param;

    @ApiModelProperty("默认时间")
    @Column(name = "time_restore")
    private String timeRestore;

    @ApiModelProperty("全局数据集")
    @Column(name = "dataset")
    private String dataset;

    @ApiModelProperty("类别ID")
    @Column(name = "catalog_id")
    private Integer catalogId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public String getUiState() {
        return uiState;
    }

    public void setUiState(String uiState) {
        this.uiState = uiState;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getTimeRestore() {
        return timeRestore;
    }

    public void setTimeRestore(String timeRestore) {
        this.timeRestore = timeRestore;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }
}
