package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.Column;

public class BaseSecurityDomainIpSegmentVO extends Query {

    @ApiModelProperty("安全域编码")
    @Column(name = "code")
    private String code;

    @Column(name = "start_ip")
    @ApiModelProperty("开始IP地址段")
    private String startIp;

    @Column(name = "end_ip")
    @ApiModelProperty("结束IP地址段")
    private String endIp;

    @Column(name = "start_ip_num")
    @ApiModelProperty("结束IP数值，计算公式：ip[0]*256*256*256 + ip[1]*256*256 + ip[2]*256 + ip[3]")
    @Ignore
    private Long startIpNum;

    @Column(name = "end_ip_num")
    @ApiModelProperty("开始IP数值，计算公式：ip[0]*256*256*256 + ip[1]*256*256 + ip[2]*256 + ip[3]")
    @Ignore
    private Long endIpNum;

    @ApiModelProperty("安全域名称")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
