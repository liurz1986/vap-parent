package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2021/3/25
 * @description
 */
@Table(name = "cascade_strategy")
public class CascadeStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("唯一ID")
    private String uid;

    @ApiModelProperty("策略名称")
    private String name;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("启用（禁用）时间")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("执行对象id")
    @Column(name = "platform_id")
    private String platformId;

    @ApiModelProperty("kafka连接信息")
    private String kafka;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getKafka() {
        return kafka;
    }

    public void setKafka(String kafka) {
        this.kafka = kafka;
    }
}
