package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;

public class AnomalyAccessQuery extends Query {
    public String id;
    public String resultGuid;
    public String relatedIps;
    public String userName;
    public String userId;
    public String areaCode;
    public String ruleCode;
    public String logsInfo;
    public String triggerTime;
    public String repeatCount;
    public String sysId;
    public String policeType;
    public String organization;
    public String operateCondition;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultGuid() {
        return resultGuid;
    }

    public void setResultGuid(String resultGuid) {
        this.resultGuid = resultGuid;
    }

    public String getRelatedIps() {
        return relatedIps;
    }

    public void setRelatedIps(String relatedIps) {
        this.relatedIps = relatedIps;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getLogsInfo() {
        return logsInfo;
    }

    public void setLogsInfo(String logsInfo) {
        this.logsInfo = logsInfo;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(String repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
}
