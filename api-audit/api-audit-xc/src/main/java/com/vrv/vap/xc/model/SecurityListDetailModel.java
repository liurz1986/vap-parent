package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("SecurityListDetailModel")
public class SecurityListDetailModel extends PageModel{

    @ApiModelProperty("设备唯一标识")
    private String devId;
    @ApiModelProperty("设备ip 必传")
    private String devIp;
    @ApiModelProperty("客户端ip")
    private String sip;
    @ApiModelProperty("客户端port")
    private String sport;
    @ApiModelProperty("服务协议")
    private String protocol;
    @ApiModelProperty("服务端口")
    private String dport;
    @ApiModelProperty("设备一级类型 0终端、1服务器、2bm安全产品、3应用、4网络设备、5其它设备（通用办公设备）")
    private String devTypeGroup;
    @ApiModelProperty("会话id")
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDevTypeGroup() {
        return devTypeGroup;
    }

    public void setDevTypeGroup(String devTypeGroup) {
        this.devTypeGroup = devTypeGroup;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }

    public String getSip() {
        return sip;
    }

    public void setSip(String sip) {
        this.sip = sip;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDport() {
        return dport;
    }

    public void setDport(String dport) {
        this.dport = dport;
    }
}
