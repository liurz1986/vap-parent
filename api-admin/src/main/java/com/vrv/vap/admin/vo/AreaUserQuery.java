package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel("查询区域下的用户")
public class AreaUserQuery extends Query {

    @ApiModelProperty("区域编码")
    @Column(name = "areaCode")
    private String code;

    @ApiModelProperty("角色ID")
    private int roleId;

    @ApiModelProperty("角色名称")
    private String roleCode;

    @ApiModelProperty("用户姓名")
    private String userName;


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
}
