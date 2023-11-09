package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("printDetailModel")
public class PrintDetailModel extends PageModel{
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("文件密级")
    private String fileLevel;
    @ApiModelProperty("文件类型：pdf word...")
    private String fileType;
    @ApiModelProperty("文件大小：最小值,最大值")
    private String fileSzie;
    @ApiModelProperty("业务类别")
    private String business;
    @ApiModelProperty("打印数量: 最小值,最大值")
    private String fileNum;
    @ApiModelProperty("设备标识")
    private String terminalType;
    @ApiModelProperty("用户编码")
    private String userNo;
    @ApiModelProperty("部门")
    private String orgCode;
    @ApiModelProperty("操作结果 0成功 1失败")
    private String opResult;
    @ApiModelProperty("设备名称")
    private String devName;
    @ApiModelProperty("设备ip")
    private String devIp;
    @ApiModelProperty("用户名")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }

    public String getFileLevel() {
        return fileLevel;
    }

    public void setFileLevel(String fileLevel) {
        this.fileLevel = fileLevel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSzie() {
        return fileSzie;
    }

    public void setFileSzie(String fileSzie) {
        this.fileSzie = fileSzie;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOpResult() {
        return opResult;
    }

    public void setOpResult(String opResult) {
        this.opResult = opResult;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }
}