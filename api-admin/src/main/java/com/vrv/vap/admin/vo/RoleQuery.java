package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryIn;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

public class RoleQuery extends Query {

    @QueryIn
    private String id;
    @ApiModelProperty("角色名称")
    @QueryLike
    private String name;
    private String code;
    @ApiModelProperty("角色描述")
    @QueryLike
    private String description;

    @ApiModelProperty("角色id")
    private String roleId;
    @ApiModelProperty("角色名称")
    @Column(name="name")
    private String fullname;

    private Integer threePowers;

    private Integer creator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getThreePowers() {
        return threePowers;
    }

    public void setThreePowers(Integer threePowers) {
        this.threePowers = threePowers;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
