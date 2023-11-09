package com.vrv.vap.xc.model;

import lombok.Data;

import java.util.List;

@Data
public class NetWorkCountModel {
    private String department;
    private List<NetWorkModel> networks;

    public NetWorkCountModel() {
    }

    public NetWorkCountModel(String department, List<NetWorkModel> networks) {
        this.department = department;
        this.networks = networks;
    }
}
