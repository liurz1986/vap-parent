package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Method {
    private Property get;
    private Property post;
    private Property put;
    private Property delete;
    private Property patch;
    public Property getGet() {
        return get;
    }

    public void setGet(Property get) {
        this.get = get;
    }

    public Property getPost() {
        return post;
    }

    public void setPost(Property post) {
        this.post = post;
    }

    public Property getPut() {
        return put;
    }

    public void setPut(Property put) {
        this.put = put;
    }

    public Property getDelete() {
        return delete;
    }

    public void setDelete(Property delete) {
        this.delete = delete;
    }

    public Property getPatch() {
        return patch;
    }

    public void setPatch(Property patch) {
        this.patch = patch;
    }
}
