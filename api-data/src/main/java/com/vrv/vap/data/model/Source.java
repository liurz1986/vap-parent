package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "data_source")
@ApiModel(value = "数据源")
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("索引/数据表/视图名")
    private String name;

    @ApiModelProperty("数据源标题")
    private String title;

    @ApiModelProperty("数据源Icon标识")
    private String icon;

    /**
     * 1： 本地ES ,2: 本地Mysql ,3: 远程Mysql, 4远程mysql
     */
    @ApiModelProperty("数据源类型")
    private Byte type;

    /**
     * ES 必选，MySql 可选
     */
    @ApiModelProperty("时间字段")
    @Column(name = "time_field")
    private String timeField;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("时间字段格式")
    @Column(name = "time_format")
    private String timeFormat;

    /**
     * 1：原始日志，2：基线数据
     */
    @ApiModelProperty("数据类型")
    @Column(name = "data_type")
    private Integer dataType;

    @ApiModelProperty("对应kafka主题")
    @Column(name = "topic_name")
    private String topicName;

    @ApiModelProperty("安全域字段")
    @Column(name = "domain_field")
    private String domainField;

    @ApiModelProperty("主题别名")
    @Column(name = "topic_alias")
    private String topicAlias;

    @ApiModelProperty("变更通知")
    @Column(name = "change_inform")
    private Integer changeInform;

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

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDomainField() {
        return domainField;
    }

    public void setDomainField(String domainField) {
        this.domainField = domainField;
    }

    public String getTopicAlias() {
        return topicAlias;
    }

    public void setTopicAlias(String topicAlias) {
        this.topicAlias = topicAlias;
    }

    public Integer getChangeInform() {
        return changeInform;
    }

    public void setChangeInform(Integer changeInform) {
        this.changeInform = changeInform;
    }
}