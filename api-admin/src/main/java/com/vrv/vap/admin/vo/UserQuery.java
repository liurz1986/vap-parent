package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryIn;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.plugin.annotaction.QueryNotEmpty;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import java.util.List;

@ApiModel("查询用户列表的参数")
public class UserQuery extends Query {


    @QueryLike
    @ApiModelProperty("名称")
    private String name;

    @QueryLike
    @ApiModelProperty("帐号")
    private String account;

    @QueryIn
    @ApiModelProperty("角色ID")
    private String roleId;

    @QueryLike
    @ApiModelProperty("身份证号")
    private String idcard;

    @QueryLike
    @ApiModelProperty("邮箱")
    private String email;

    @QueryLike
    @ApiModelProperty("电话号码")
    private String phone;

    @Column(name="email")
    @QueryNotEmpty
    @ApiModelProperty("如果传1，则只返回有Email的记录")
    private String hasEmail;

    @Column(name="phone")
    @QueryNotEmpty
    @ApiModelProperty("如果传1，则只返回有手机号的记录")
    private String hasPhone;

    @ApiModelProperty("角色编号")
    private String roleCode;

    @ApiModelProperty("用户状态")
    private Byte status;

    @ApiModelProperty("机构编码")
    private String orgCode;

    @Column(name="orgCode")
    @ApiModelProperty("下级机构编码")
    private String orgSub;

    @ApiModelProperty("机构名称")
    @QueryLike
    private String orgName;

    @ApiModelProperty("所在省")
    private String province;

    @ApiModelProperty("所在市")
    private String city;

    @Column(name = "isLeader")
    private Byte isLeader;

    @ApiModelProperty("创建人")
    private Integer creator;


    @ApiModelProperty("安全域编码")
    private String domainCode;

    private String orderBy;

    private  String subCode;

    private List roleIds;

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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

    public Byte getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Byte isLeader) {
        this.isLeader = isLeader;
    }


    public String getOrgSub() {
        return orgSub;
    }

    public void setOrgSub(String orgSub) {
        this.orgSub = orgSub;
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public List getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List roleIds) {
        this.roleIds = roleIds;
    }
}
