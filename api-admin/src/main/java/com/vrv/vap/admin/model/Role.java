package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

public class Role {
    /**
     * 角色ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    @Ignore
    private String name;
    /**
     * 角色代码
     */
    @ApiModelProperty(value = "角色编码")
    private String code = "";

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述")
    private String description;

    /**
     * 是否是系统内置角色
     */
    @Column(name = "built_in")
    @Ignore
    private String builtIn;

    /**
     * 可以进行管理的角色
     */
    private String control;

    @Column(name = "three_powers")
    @ApiModelProperty(value = "三权标识")
    @Ignore
    private Integer threePowers;

    @ApiModelProperty(value = "创建该角色的用户id")
    @Ignore
    private Integer creator;

    private String guid;

    private Integer status;

    private String orgId;

    public Role() {
    }

    public Role(String code) {
        this.code = code;
    }

    /**
     * 获取角色ID
     *
     * @return id - 角色ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置角色ID
     *
     * @param id 角色ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取角色名称
     *
     * @return name - 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(String builtIn) {
        this.builtIn = builtIn;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}