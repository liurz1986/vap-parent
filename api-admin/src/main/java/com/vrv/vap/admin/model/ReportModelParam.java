package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

public class ReportModelParam {
    @ApiModelProperty("主键")
    private String modelId;

    @ApiModelProperty(value = "查询语句")
    private String sql;

    @ApiModelProperty(value = "类型,1：饼图，2：折线图，3：柱状图，4：表格, 5:段落 ,6:引用，7:列表")
    private String type;

    @ApiModelProperty("参数")
    private String p;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty(value = "模型内容")
    private String content;

    @ApiModelProperty(value = "数据源id：-1 mysql, -2 es")
    private Integer dataSourceId;

    @ApiModelProperty("数据类型是否是指标")
    private String isInterface;

    @ApiModelProperty("指标编号")
    private String interfaceId;

    @ApiModelProperty("")
    private String config;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getIsInterface() {
        return isInterface;
    }

    public void setIsInterface(String isInterface) {
        this.isInterface = isInterface;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
