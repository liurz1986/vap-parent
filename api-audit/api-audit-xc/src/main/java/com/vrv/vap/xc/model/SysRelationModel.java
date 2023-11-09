package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("SysRelationModel")
public class SysRelationModel extends PageModel{

    @ApiModelProperty("设备唯一标识")
    private String devId;
    @ApiModelProperty("设备ip 必传")
    private String devIp;
    @ApiModelProperty("类型 0终端、1服务器、2bm安全产品、3应用、4网络设备、5其它设备（通用办公设备）")
    private String devTypeGroup;
    @ApiModelProperty("时间跨度 1小时 2天 3月")
    private String interval = "2";
    @ApiModelProperty("目的ip")
    private String dip;
    @ApiModelProperty("目的端口")
    private String dport;
    @ApiModelProperty("协议")
    private String protoclol;


    public String getDip() {
        return dip;
    }

    public void setDip(String dip) {
        this.dip = dip;
    }

    public String getDport() {
        return dport;
    }

    public void setDport(String dport) {
        this.dport = dport;
    }

    public String getProtoclol() {
        return protoclol;
    }

    public void setProtoclol(String protoclol) {
        this.protoclol = protoclol;
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

    public String getDevTypeGroup() {
        return devTypeGroup;
    }

    public void setDevTypeGroup(String devTypeGroup) {
        this.devTypeGroup = devTypeGroup;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
