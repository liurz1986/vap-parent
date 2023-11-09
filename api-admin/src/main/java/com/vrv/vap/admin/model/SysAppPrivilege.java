package com.vrv.vap.admin.model;

import javax.persistence.*;

@Table(name = "sys_app_privilege")
public class SysAppPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "privilege_id")
    private Integer privilegeId;

    @Column(name = "app_id")
    private Integer appId;

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
     * @return privilege_id
     */
    public Integer getPrivilegeId() {
        return privilegeId;
    }

    /**
     * @param privilegeId
     */
    public void setPrivilegeId(Integer privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * @return app_id
     */
    public Integer getAppId() {
        return appId;
    }

    /**
     * @param appId
     */
    public void setAppId(Integer appId) {
        this.appId = appId;
    }
}