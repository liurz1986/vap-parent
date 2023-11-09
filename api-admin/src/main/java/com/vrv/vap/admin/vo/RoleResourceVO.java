package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.RoleResource;

import java.util.List;

public class RoleResourceVO {

    private List<RoleResource> added;

    private List<RoleResource> deleted;

    public List<RoleResource> getAdded() {
        return added;
    }

    public void setAdded(List<RoleResource> added) {
        this.added = added;
    }

    public List<RoleResource> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<RoleResource> deleted) {
        this.deleted = deleted;
    }
}
