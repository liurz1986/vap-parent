package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("SecurityModel")
public class SecurityModel extends PageModel{

    @ApiModelProperty("设备ip 必传")
    private String devIp;
    @ApiModelProperty("设备一级类型 0终端、1服务器、2bm安全产品、3应用、4网络设备、5其它设备（通用办公设备）")
    private String devTypeGroup;
    @ApiModelProperty("时间跨度 1小时 2天 3月")
    private String interval = "2";
    @ApiModelProperty("账户名")
    private String userAccount;
    @ApiModelProperty("源ip")
    private String sip;
    @ApiModelProperty("目的ip")
    private String dip;
    @ApiModelProperty("运维协议")
    private String protocol;
    @ApiModelProperty("运维端口")
    private String port;
    @ApiModelProperty("URL")
    private String url;
    @ApiModelProperty("操作记录")
    private String record;
    @ApiModelProperty("总字节数：0-100")
    private String bytes;
    @ApiModelProperty("总字包数：0-100")
    private String pcks;
    @ApiModelProperty("文件传输方向 上传/发送 1 ,下载/接收 2")
    private String fileDir;
    @ApiModelProperty("文件信息")
    private String fileInfo;
    @ApiModelProperty("文件密级")
    private String fileLevel;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("链接主机协议")
    private String connType;

    @ApiModelProperty("服务端口")
    private String dport;

    public String getDport() {
        return dport;
    }

    public void setDport(String dport) {
        this.dport = dport;
    }

    @ApiModelProperty("操作指令")
    private String order;

    @Override
    public String getOrder() {
        return order;
    }

    @Override
    public void setOrder(String order) {
        this.order = order;
    }

    public String getConnType() {
        return connType;
    }

    public void setConnType(String connType) {
        this.connType = connType;
    }

    public String getFileLevel() {
        return fileLevel;
    }

    public void setFileLevel(String fileLevel) {
        this.fileLevel = fileLevel;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public String getPcks() {
        return pcks;
    }

    public void setPcks(String pcks) {
        this.pcks = pcks;
    }

    public String getDip() {
        return dip;
    }

    public void setDip(String dip) {
        this.dip = dip;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getSip() {
        return sip;
    }

    public void setSip(String sip) {
        this.sip = sip;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getDevTypeGroup() {
        return devTypeGroup;
    }

    public void setDevTypeGroup(String devTypeGroup) {
        this.devTypeGroup = devTypeGroup;
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }


    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
