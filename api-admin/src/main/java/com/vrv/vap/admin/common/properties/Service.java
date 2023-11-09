package com.vrv.vap.admin.common.properties;

import java.util.ArrayList;
import java.util.List;

public class Service {

    private List<String> service = new ArrayList<>();

    private String name = "";

    private String version = "";

    public List<String> getService() {
        return service;
    }

    public void setService(List<String> service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
