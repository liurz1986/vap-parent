package com.vrv.vap.admin.vo;

public class ISCPermissionItem {

    private String spRoleId;
    private String orgRoleId;
    private  boolean isdeflaultRole = false;
    private String orgId;

    public String getSpRoleId() {
        return spRoleId;
    }

    public void setSpRoleId(String spRoleId) {
        this.spRoleId = spRoleId;
    }

    public String getOrgRoleId() {
        return orgRoleId;
    }

    public void setOrgRoleId(String orgRoleId) {
        this.orgRoleId = orgRoleId;
    }

    public boolean isIsdeflaultRole() {
        return isdeflaultRole;
    }

    public void setIsdeflaultRole(boolean isdeflaultRole) {
        this.isdeflaultRole = isdeflaultRole;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
