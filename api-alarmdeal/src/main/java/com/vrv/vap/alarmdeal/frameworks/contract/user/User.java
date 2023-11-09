package com.vrv.vap.alarmdeal.frameworks.contract.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@ApiModel("用户信息")
@Data
public class User implements Serializable {
    
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8911309855041043180L;

	/**
     * 
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("用户ID，主键")
    private Integer id;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String name;

    /**
     * 用户用到登录的帐号名
     */
    @ApiModelProperty("用户用到登录的帐号名")
    private String account;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码",hidden = true)
    private String password;

    @Column(name = "role_id")
    @ApiModelProperty("角色ID")
    private String roleId;

    @ApiModelProperty("身份证号")
    private String idcard;

    @ApiModelProperty("电话号码 ")
    private String phone;

    @ApiModelProperty("email ")
    private String email;

    @ApiModelProperty("状态")
    private Byte status;

    @ApiModelProperty("机构编码")
    @Column(name = "org_code")
    private String orgCode;

    @ApiModelProperty("机构名称")
    @Column(name = "org_name")
    private String orgName;

    @ApiModelProperty("所在省")
    private String province;

    @ApiModelProperty("所在市")
    private String city;

    @Column(name = "is_leader")
    @ApiModelProperty("是否是领导")
    private Byte isLeader;

    @ApiModelProperty("安全域编码")
    private String domainCode;

    @ApiModelProperty("安全域名称")
    private String domainName;
    @ApiModelProperty("关联personid")
    private Integer personId;
    @Override
	public String toString()
    {
		return "User [id=" + id + ", name=" + name + ", account=" + account + ", password=" + password + ", roleId=" + roleId + ", idcard=" + idcard
				+ ", phone=" + phone + ", email=" + email + ", status=" + status + ",personId=" + personId + "]";
	}

    
}