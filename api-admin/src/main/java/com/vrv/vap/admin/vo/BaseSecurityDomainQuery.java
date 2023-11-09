package com.vrv.vap.admin.vo;

public class BaseSecurityDomainQuery {
    Integer userId;
    Byte orgHierarchy;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getOrgHierarchy() {
        return orgHierarchy;
    }

    public void setOrgHierarchy(Byte orgHierarchy) {
        this.orgHierarchy = orgHierarchy;
    }
}
