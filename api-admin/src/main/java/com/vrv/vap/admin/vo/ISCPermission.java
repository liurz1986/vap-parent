package com.vrv.vap.admin.vo;

import java.util.List;

public class ISCPermission {

    private String systemId;

    private List<ISCPermissionItem> permission;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public List<ISCPermissionItem> getPermission() {
        return permission;
    }

    public void setPermission(List<ISCPermissionItem> permission) {
        this.permission = permission;
    }
}
