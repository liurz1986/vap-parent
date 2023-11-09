package com.vrv.vap.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "当前登录用户信息")
public class User implements Serializable{

    @ApiModelProperty(value = "用户ID")
    private int id;
    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "用户账号")
    private String account;
    @ApiModelProperty(value = "用户角色ID")
    private List<Integer> roleIds;
    @ApiModelProperty(value = "用户身份证号")
    private String idcard;
    @ApiModelProperty(value = "用户状态",allowableValues="0,1")
    private int status;
    @ApiModelProperty(value = "用户色色CODE")
    private List<String> roleCode;
    @ApiModelProperty(value = "用户色色名称")
    private List<String> roleName;
    @ApiModelProperty(value = "用户所在机构编码")
    private String orgCode;
    @ApiModelProperty(value = "用户所在机构名称")
    private String orgName;
    @ApiModelProperty(value = "用户登录类型 0：普通登录 1：证书登录 2：虹膜登录,3:人脸登录",allowableValues="0,1,2,3")
    private int loginType;
    @ApiModelProperty(value = "用户省编码")
    private String province;
    @ApiModelProperty(value = "用户市编码")
    private String city;


    public int getId() {
        return id;
    }

    public void setId(int id) {
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


    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
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

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
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
}
