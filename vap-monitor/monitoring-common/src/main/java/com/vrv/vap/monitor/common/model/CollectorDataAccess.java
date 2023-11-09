package com.vrv.vap.monitor.common.model;


public class CollectorDataAccess {

    private Integer id;

    private String name;


    private String port;

    private String srcIp;


    private Integer type;

    private String encoding;


    private Integer collectionId;


    private String version;


    private String cid;


    private String ruleJson;


    private Boolean updateNew = false;


    private Integer sourceType;


    private Integer sourceId;


    private Integer templateType;


    private Integer initMemory;


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
