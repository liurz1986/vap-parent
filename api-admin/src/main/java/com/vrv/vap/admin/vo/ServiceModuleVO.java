package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ServiceModuleVO extends Query {
    private Integer id;
    @ApiModelProperty("服务名称")
    @QueryLike
    private String name;
    @ApiModelProperty("服务描述")
    @QueryLike
    private String serviceDesc;
    private Integer type;
    private String prefix;
    private String version;
    private Date syncTime;
    private String syncUrl;

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

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public String getSyncUrl() {
        return syncUrl;
    }

    public void setSyncUrl(String syncUrl) {
        this.syncUrl = syncUrl;
    }

    @Override
    public String toString() {
        return "ServiceModule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serviceDesc='" + serviceDesc + '\'' +
                ", type=" + type +
                ", prefix='" + prefix + '\'' +
                ", version='" + version + '\'' +
                ", syncTime=" + syncTime +
                ", syncUrl='" + syncUrl + '\'' +
                '}';
    }
}
