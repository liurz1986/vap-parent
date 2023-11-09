package com.vrv.vap.netflow.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;


@Table(name = "base_security_domain_ip_segment")
public class BaseSecurityDomainIpSegment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 关联使用guid
     */
    @Column(name = "code")
    private String code;

    /**
     * 开始IP地址段
     */
    @Column(name = "start_ip")
    @ApiModelProperty(value = "开始IP地址段")
    private String startIp;

    /**
     * 结束IP地址段
     */
    @Column(name = "end_ip")
    @ApiModelProperty(value = "结束IP地址段")
    private String endIp;

    /**
     * 开始IP地址段转换成整型
     */
    @Column(name = "start_ip_num")
    @ApiModelProperty(value = "开始IP地址段转换成整型")
    private Long startIpNum;

    /**
     * 结束IP地址段转换成整型
     */
    @Column(name = "end_ip_num")
    @ApiModelProperty(value = "结束IP地址段转换成整型")
    private Long endIpNum;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public Long getStartIpNum() {
        return startIpNum;
    }

    public void setStartIpNum(Long startIpNum) {
        this.startIpNum = startIpNum;
    }

    public Long getEndIpNum() {
        return endIpNum;
    }

    public void setEndIpNum(Long endIpNum) {
        this.endIpNum = endIpNum;
    }
}