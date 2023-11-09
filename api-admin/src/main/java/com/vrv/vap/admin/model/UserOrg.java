package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2021/8/23
 * @description
 */
@ApiModel("人员组织机构关联表")
@Table(name = "user_org")
public class UserOrg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "org_id")
    private Integer orgId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
}
