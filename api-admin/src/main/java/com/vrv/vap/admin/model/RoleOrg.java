package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2021/8/23
 * @description
 */
@ApiModel("角色组织机构关联表")
@Table(name = "role_org")
public class RoleOrg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "org_id")
    private Integer orgId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
}
