package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2022/1/5
 * @description 采集器规则
 */
@Table(name = "collector_rule")
public class CollectorRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("规则集ID")
    @Column(name = "collection_id")
    private Integer collectionId;

    @ApiModelProperty("优先级")
    private Integer priority;

    @ApiModelProperty("日志样例")
    @Column(name = "source")
    private String source;

    @ApiModelProperty("特征")
    private String charater;

    @ApiModelProperty("特征类型")
    @Column(name = "charater_type")
    private Integer charaterType;

    @ApiModelProperty("提取方式")
    private String handler;

    @ApiModelProperty("分隔符")
    private String split;

    @ApiModelProperty("关联索引")
    @Column(name = "relate_index")
    private String relateIndex;

    @ApiModelProperty("字段列表")
    private String fields;

    @ApiModelProperty("字段重命名")
    private String renames;

    @ApiModelProperty("正则表达式")
    private String regex;

    @ApiModelProperty("过滤规则JSON")
    @Column(name = "rule_json")
    @Ignore
    private String ruleJson;

    @ApiModelProperty("外部js")
    @Column(name = "js_content")
    private String jsContent;

    @ApiModelProperty("字段结构")
    private String body;

    @Transient
    @ApiModelProperty("规则集名称")
    private String collectionName;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getCharater() {
        return charater;
    }

    public void setCharater(String charater) {
        this.charater = charater;
    }

    public Integer getCharaterType() {
        return charaterType;
    }

    public void setCharaterType(Integer charaterType) {
        this.charaterType = charaterType;
    }


    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getRelateIndex() {
        return relateIndex;
    }

    public void setRelateIndex(String relateIndex) {
        this.relateIndex = relateIndex;
    }

    public String getRenames() {
        return renames;
    }

    public void setRenames(String renames) {
        this.renames = renames;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRuleJson() {
        return ruleJson;
    }

    public void setRuleJson(String ruleJson) {
        this.ruleJson = ruleJson;
    }

    public String getJsContent() {
        return jsContent;
    }

    public void setJsContent(String jsContent) {
        this.jsContent = jsContent;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public String toString() {
        return "CollectorRule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", collectionId=" + collectionId +
                ", priority=" + priority +
                ", source='" + source + '\'' +
                ", charater='" + charater + '\'' +
                ", charaterType=" + charaterType +
                ", handler='" + handler + '\'' +
                ", split='" + split + '\'' +
                ", relateIndex='" + relateIndex + '\'' +
                ", fields='" + fields + '\'' +
                ", renames='" + renames + '\'' +
                ", regex='" + regex + '\'' +
                ", ruleJson='" + ruleJson + '\'' +
                ", jsContent='" + jsContent + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
