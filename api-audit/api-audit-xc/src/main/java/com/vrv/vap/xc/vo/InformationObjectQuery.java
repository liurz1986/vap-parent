package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;

public class InformationObjectQuery extends Query {
    private String taskId;
    private String userName;
    private String idCard;
    private String orgId;
    private String organization;
    private String sysId;
    private String deviceId;
    private String objectCount;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(String objectCount) {
        this.objectCount = objectCount;
    }
}
