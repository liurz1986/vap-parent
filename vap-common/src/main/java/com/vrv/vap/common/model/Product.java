package com.vrv.vap.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Product implements Serializable {
    private String id;

    private String name;

    private String authInfo;

    private Set<String> services;

    private Map<String,String> extendProperties;

    private Map<String,List<RedirectModel>> redirectMap;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }

    public Set<String> getServices() {
        return services;
    }

    public void setServices(Set<String> services) {
        this.services = services;
    }

    public Map<String, String> getExtendProperties() {
        return extendProperties;
    }

    public void setExtendProperties(Map<String, String> extendProperties) {
        this.extendProperties = extendProperties;
    }

    public Map<String, List<RedirectModel>> getRedirectMap() {
        return redirectMap;
    }

    public void setRedirectMap(Map<String, List<RedirectModel>> redirectMap) {
        this.redirectMap = redirectMap;
    }
}
