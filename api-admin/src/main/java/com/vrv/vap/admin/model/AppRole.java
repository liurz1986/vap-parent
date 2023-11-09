package com.vrv.vap.admin.model;

import javax.persistence.*;

@Table(name = "app_role")
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_id")
    private Integer appid;

    @Column(name = "role_id")
    private Integer roleId;

    private Short sort;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return app_id
     */
    public Integer getAppid() {
        return appid;
    }

    /**
     * @param appId
     */
    public void setAppid(Integer appId) {
        this.appid = appId;
    }

    /**
     * @return role_id
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * @return sort
     */
    public Short getSort() {
        return sort;
    }

    /**
     * @param sort
     */
    public void setSort(Short sort) {
        this.sort = sort;
    }
}