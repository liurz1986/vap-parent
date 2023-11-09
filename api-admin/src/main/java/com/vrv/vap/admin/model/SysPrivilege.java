package com.vrv.vap.admin.model;

import javax.persistence.*;

@Table(name = "sys_privilege")
public class SysPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 样式
     */
    private String icon;

    /**
     * URL
     */
    private String url;

    private String method;

    /**
     * 权限类型 ,0-菜单权限 ,1-API权限
     */
    private Boolean type;

    /**
     * 上级权限id
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 是否可用 ,1-可用,0-不可用
     */
    private Boolean enabled;

    /**
     * 是否默认
     * */
    @Column(name = "def_allow")
    private Byte defAllow;
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
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取样式
     *
     * @return icon - 样式
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置样式
     *
     * @param icon 样式
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取URL
     *
     * @return url - URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置URL
     *
     * @param url URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取权限类型 ,0-菜单权限 ,1-API权限
     *
     * @return type - 权限类型 ,0-菜单权限 ,1-API权限
     */
    public Boolean getType() {
        return type;
    }

    /**
     * 设置权限类型 ,0-菜单权限 ,1-API权限
     *
     * @param type 权限类型 ,0-菜单权限 ,1-API权限
     */
    public void setType(Boolean type) {
        this.type = type;
    }

    /**
     * 获取上级权限id
     *
     * @return parent_id - 上级权限id
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置上级权限id
     *
     * @param parentId 上级权限id
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取是否可用 ,1-可用,0-不可用
     *
     * @return enabled - 是否可用 ,1-可用,0-不可用
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 设置是否可用 ,1-可用,0-不可用
     *
     * @param enabled 是否可用 ,1-可用,0-不可用
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Byte getDefAllow() {
        return defAllow;
    }

    public void setDefAllow(Byte defAllow) {
        this.defAllow = defAllow;
    }
}