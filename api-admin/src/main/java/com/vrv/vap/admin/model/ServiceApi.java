package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "service_api")
public class ServiceApi {
    @Id
    private Integer id;
    @ApiModelProperty("服务ID")
    private Integer serviceId;
    @ApiModelProperty("操作键值")
    private String operateKey;
    @ApiModelProperty("路径")
    private String path;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("方法")
    private String method;
    @ApiModelProperty("标签")
    private String tags;
    @ApiModelProperty("标签描述")
    private String tagDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getOperateKey() {
        return operateKey;
    }

    public void setOperateKey(String operateKey) {
        this.operateKey = operateKey;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public void setTagDesc(String tagDesc) {
        this.tagDesc = tagDesc;
    }

    @Override
    public String toString() {
        return "ServiceApi{" +
                "id=" + id +
                ", serviceId=" + serviceId +
                ", operateKey='" + operateKey + '\'' +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", method='" + method + '\'' +
                ", tags='" + tags + '\'' +
                ", tagDesc='" + tagDesc + '\'' +
                '}';
    }
}
