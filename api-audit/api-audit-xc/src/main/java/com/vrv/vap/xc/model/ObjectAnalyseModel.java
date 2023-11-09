package com.vrv.vap.xc.model;

import com.vrv.vap.toolkit.annotations.LogDict;

public class ObjectAnalyseModel extends PageModel {

    //用户账号
    private String userAcount;

    //目标单位是否本单位 0-不是 1-是
    private String isLocalOrg;

    //用户、管理员、后台服务（应用系统内部IP）、应用系统（其它应用系统IP）
    @LogDict("f5a4ae5b-3cee-a84f-7471-8f23ezjg0200")
    private String userType;

    //终端、服务器、网络设备、保密设备、刻录设备、打印设备、应用服务器（通过设备IP标识）
    //设备类型：0终端、1服务器、2bm安全产品、3应用、4网络设备、5网络
    @LogDict("467d3000-3bc8-9129-9356-3e9fb82ab6c5")
    private String deviceType;

    //1-上传；2-下载 68ccdf6f-89ef-4528-a973-3ca4fb4509c3
    @LogDict("68ccdf6f-89ef-4528-a973-3ca4fb4509c3")
    private Integer fileDir;

    //0-打印、1-刻录 199424e5-0631-c06e-89c9-c1f33aa7a510
    @LogDict("199424e5-0631-c06e-89c9-c1f33aa7a510")
    private Integer printType;

    //1-终端 2-服务器 3-应用系统 4-安全保密设备 5-网络设备
    @LogDict("467d3000-3bc8-9129-9356-3e9fb82ab6c5")
    private Integer operationType;

    public String getUserAcount() {
        return userAcount;
    }

    public void setUserAcount(String userAcount) {
        this.userAcount = userAcount;
    }

    public String getIsLocalOrg() {
        return isLocalOrg;
    }

    public void setIsLocalOrg(String isLocalOrg) {
        this.isLocalOrg = isLocalOrg;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getFileDir() {
        return fileDir;
    }

    public void setFileDir(Integer fileDir) {
        this.fileDir = fileDir;
    }

    public Integer getPrintType() {
        return printType;
    }

    public void setPrintType(Integer printType) {
        this.printType = printType;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }
}
