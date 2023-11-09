package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {
    private List<String> tags;
    private String summary;
    private String operationId;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public String toString() {
        return "Property{" +
                "tags=" + tags +
                ", summary='" + summary + '\'' +
                ", operationId='" + operationId + '\'' +
                '}';
    }
}
