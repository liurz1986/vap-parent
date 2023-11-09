package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.Column;

@ApiModel("查询机构下的用户")
public class OrgUserQuery extends Query {

    @ApiModelProperty("机构编码")
    @Column(name = "departmentCode")
    private String code;

    @ApiModelProperty("角色ID")
    @Ignore
    private int roleId;

    @ApiModelProperty("角色名称")
    private String roleCode;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("起始IP值")
    private String startIp;

    @ApiModelProperty("结束IP地址段")
    private String endIp;

    @ApiModelProperty("组织机构名称")
    private String orgName;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
