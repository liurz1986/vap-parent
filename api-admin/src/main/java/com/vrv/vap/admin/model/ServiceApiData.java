package com.vrv.vap.admin.model;

public class ServiceApiData {
    private Integer id;
    private String path;
    private String method;
    private String prefix;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "ServiceApiData{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
