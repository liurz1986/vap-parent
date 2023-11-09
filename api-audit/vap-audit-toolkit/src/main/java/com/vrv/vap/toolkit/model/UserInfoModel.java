package com.vrv.vap.toolkit.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 登录用户信息
 *
 * @author xw
 * @date 2018年5月3日
 */
public class UserInfoModel implements Serializable {

    /**
     * 用户表id
     */
    private Integer id;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 角色id   数据为数组类型String[] 上云不同步所以先用object
     */
    private Object roleId;

    /**
     * 身份证号
     */
    private String idcard;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 角色编号
     */
    private List<String> roleCode;

    /**
     * 角色名称
     */
    private List<String> roleName;

    /**
     * 机构编号
     */
    private String orgCode;

    /**
     * 系统机构编号（用于级联查询）
     */
    private String orgSubCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 省编码
     */
    private String province;

    /**
     * 市编码
     */
    private String city;

    /**
     * 登录类型
     */
    private Integer loginType;

    public UserInfoModel() {
    }

    /**User bean*/
    @SuppressWarnings("unchecked")
    public UserInfoModel(Map<String, Object> map) {
        Map<String, Object> data = (Map<String, Object>) map.get("data");
        this.id = (data.get("id")) == null ? null : ((Double) data.get("id")).intValue();
        this.name = (String) data.get("name");
        this.account = (String) data.get("account");
        this.roleId = data.get("roleId");
        this.idcard = (String) data.get("idcard");
        this.status = ((Double) data.get("status")).intValue();
        this.roleCode = (List<String>) data.get("roleCode");
        this.roleName = (List<String>) data.get("roleName");
        this.orgCode = (String) data.get("orgCode");
        this.orgName = (String) data.get("orgName");
        this.province = (String) data.get("province");
        this.city = (String) data.get("city");
        this.loginType = ((Double) data.get("loginType")).intValue();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Object getRoleId() {
        return roleId;
    }

    public void setRoleId(Object roleId) {
        this.roleId = roleId;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(List<String> roleCode) {
        this.roleCode = roleCode;
    }

    public List<String> getRoleName() {
        return roleName;
    }

    public void setRoleName(List<String> roleName) {
        this.roleName = roleName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getOrgSubCode() {
        return orgSubCode;
    }

    public void setOrgSubCode(String orgSubCode) {
        this.orgSubCode = orgSubCode;
    }
}
