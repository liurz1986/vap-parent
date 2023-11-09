package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2022/10/9
 * @description
 */
@Table(name = "resource_api")
public class ResourceApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "resource_id")
    @ApiModelProperty(value = "资源ID")
    private Integer resourceId;

    @Column(name = "api_id")
    @ApiModelProperty(value = "接口ID")
    private Integer apiId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }
}
