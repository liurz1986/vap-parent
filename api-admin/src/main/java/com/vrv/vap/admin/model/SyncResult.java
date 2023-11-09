package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncResult {
    private String swagger;
    private String host;
    private String basePath;
    private List<Tags> tags;
    private Map<String, Method> paths;

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Map<String, Method> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Method> paths) {
        this.paths = paths;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Result{" +
                "swagger='" + swagger + '\'' +
                ", host='" + host + '\'' +
                ", basePath='" + basePath + '\'' +
                ", tags=" + tags +
                ", paths='" + paths + '\'' +
                '}';
    }
}
