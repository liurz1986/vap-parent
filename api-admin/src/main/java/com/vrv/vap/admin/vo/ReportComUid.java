package com.vrv.vap.admin.vo;

import java.util.List;

public class ReportComUid {
    private String uid;
    private List<ReportComUid> children;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<ReportComUid> getChildren() {
        return children;
    }

    public void setChildren(List<ReportComUid> children) {
        this.children = children;
    }
}
