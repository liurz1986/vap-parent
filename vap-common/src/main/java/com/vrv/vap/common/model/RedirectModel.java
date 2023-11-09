package com.vrv.vap.common.model;

import java.io.Serializable;
import java.util.Map;

public class RedirectModel implements Serializable {
    private String serviceId;

    private String originPath;

    private String redirectPath;

    private String methodType;

    private Map<String,String> extendProperties;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public Map<String, String> getExtendProperties() {
        return extendProperties;
    }

    public void setExtendProperties(Map<String, String> extendProperties) {
        this.extendProperties = extendProperties;
    }
}
