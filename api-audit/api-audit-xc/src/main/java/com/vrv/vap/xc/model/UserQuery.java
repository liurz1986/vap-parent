package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModelProperty;

public class UserQuery extends BaseModel {
    @ApiModelProperty("用户名称，支持模糊匹配")
    private String name;

    @ApiModelProperty("用户帐号，支持模糊匹配")
    private String account;

    @ApiModelProperty("角色ID，支持多个id，用逗号分开")
    private String roleId;

    @ApiModelProperty("身份证号，支持模糊匹配")
    private String idcard;

    @ApiModelProperty("email，支持模糊匹配")
    private String email;

    @ApiModelProperty("电话号码，支持模糊匹配")
    private String phone;

    @ApiModelProperty("如果传1，则只返回有Email的记录")
    private String hasEmail;

    @ApiModelProperty("如果传1，则只返回有手机号的记录")
    private String hasPhone;

    @ApiModelProperty("角色编号，传此参数会导致 roleId 无效")
    private String roleCode;

    @ApiModelProperty("用户状态")
    private Byte status;

    @ApiModelProperty("机构编码")
    private String orgCode;

    @ApiModelProperty("下级机构编码")
    private String orgSub;

    @ApiModelProperty("机构名称")
    private String orgName;

    @ApiModelProperty("所在省")
    private String province;

    @ApiModelProperty("所在市")
    private String city;

    private Byte isLeader;

    @ApiModelProperty("创建人")
    private Integer creator;
    @ApiModelProperty("安全域编码")
    private String domainCode;

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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(String hasEmail) {
        this.hasEmail = hasEmail;
    }

    public String getHasPhone() {
        return hasPhone;
    }

    public void setHasPhone(String hasPhone) {
        this.hasPhone = hasPhone;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgSub() {
        return orgSub;
    }

    public void setOrgSub(String orgSub) {
        this.orgSub = orgSub;
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

    public Byte getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Byte isLeader) {
        this.isLeader = isLeader;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }
}
