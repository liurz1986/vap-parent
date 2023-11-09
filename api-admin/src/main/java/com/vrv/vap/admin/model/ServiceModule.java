package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "service_module")
public class ServiceModule {
    @Id
    private Integer id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("服务描述")
    private String serviceDesc;
    @ApiModelProperty("类型")
    private Integer type;
    @ApiModelProperty("前缀")
    private String prefix;
    @ApiModelProperty("版本号")
    private String version;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    @ApiModelProperty("同步时间")
    @Ignore
    private Date syncTime;
    @ApiModelProperty("同步地址")
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
