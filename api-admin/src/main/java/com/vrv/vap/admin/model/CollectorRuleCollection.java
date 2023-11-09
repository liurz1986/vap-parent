package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2022/1/4
 * @description 采集器规则集
 */
@Table(name = "collector_rule_collection")
public class CollectorRuleCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @Ignore
    @ApiModelProperty("类型")
    private Integer type;

    @Ignore
    @ApiModelProperty("版本号")
    private String version;

    @Column(name = "access_type")
    @ApiModelProperty("接入方式")
    private Integer accessType;

    @ApiModelProperty("字符编码")
    private String encoding;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getAccessType() {
        return accessType;
    }

    public void setAccessType(Integer accessType) {
        this.accessType = accessType;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
