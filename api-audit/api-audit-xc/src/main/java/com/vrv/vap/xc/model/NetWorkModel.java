package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class NetWorkModel {
    private String appName;
    private int count;

    public NetWorkModel() {
    }

    public NetWorkModel(String appName, int count) {
        this.appName = appName;
        this.count = count;
    }
}
