package com.vrv.vap.alarmdeal.frameworks.contract.user;


public class Role {
    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色名称
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 是否是系统内置角色
     */
    private String builtIn;

    /**
     * 可以进行管理的角色
     */
    private String control;

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
}