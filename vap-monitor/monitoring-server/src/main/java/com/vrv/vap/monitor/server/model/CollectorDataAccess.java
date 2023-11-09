package com.vrv.vap.monitor.server.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2022/1/5
 * @description 采集器数据接入实体类
 */
@Table(name = "collector_data_access")
public class CollectorDataAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("端口号")
    private String port;

    @ApiModelProperty("发送端IP")
    private String srcIp;

    @ApiModelProperty("接入方式")
    private Integer type;

    @ApiModelProperty("编码格式")
    private String encoding;

    @ApiModelProperty("规则集ID")
    private Integer collectionId;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("采集器ID")
    @Column(name = "cid")
    private String cid;

    @ApiModelProperty("过滤规则")
    @Column(name = "rule_json")
    private String ruleJson;

    @Transient
    @ApiModelProperty("是否更新到最新")
    private Boolean updateNew = false;

    @ApiModelProperty("数据来源")
    @Column(name = "source_type")
    private Integer sourceType;

    @ApiModelProperty("导入模板id")
    @Column(name = "source_id")
    private Integer sourceId;

    @ApiModelProperty("模板类型")
    @Column(name = "template_type")
    private Integer templateType;

    @ApiModelProperty("启动内存")
    @Column(name = "init_memory")
    private Integer initMemory;

    @ApiModelProperty("内置类型")
    @Column(name = "build_type")
    private Integer buildType;

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getRuleJson() {
        return ruleJson;
    }

    public void setRuleJson(String ruleJson) {
        this.ruleJson = ruleJson;
    }

    public Boolean getUpdateNew() {
        return updateNew;
    }

    public void setUpdateNew(Boolean updateNew) {
        this.updateNew = updateNew;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getInitMemory() {
        return initMemory;
    }

    public void setInitMemory(Integer initMemory) {
        this.initMemory = initMemory;
    }

    public Integer getBuildType() {
        return buildType;
    }

    public void setBuildType(Integer buildType) {
        this.buildType = buildType;
    }
}
