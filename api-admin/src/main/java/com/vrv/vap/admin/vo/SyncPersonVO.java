package com.vrv.vap.admin.vo;

/**
 * @author lilang
 * @date 2022/5/16
 * @description
 */
public class SyncPersonVO {

    private String syncUid;

    private String syncSource;

    private Integer dataSourceType;

    private String userNo;

    private String userName;

    private String userIdnEx;

    private String personType;

    private String personRank;

    private String secretLevel;

    private String orgCode;

    private String orgName;

    private String originAccount;

    public String getSyncUid() {
        return syncUid;
    }

    public void setSyncUid(String syncUid) {
        this.syncUid = syncUid;
    }

    public String getSyncSource() {
        return syncSource;
    }

    public void setSyncSource(String syncSource) {
        this.syncSource = syncSource;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIdnEx() {
        return userIdnEx;
    }

    public void setUserIdnEx(String userIdnEx) {
        this.userIdnEx = userIdnEx;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getPersonRank() {
        return personRank;
    }

    public void setPersonRank(String personRank) {
        this.personRank = personRank;
    }

    public String getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(String originAccount) {
        this.originAccount = originAccount;
    }
}
