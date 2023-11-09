package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lps 2021/8/9
 */

@Data
@Entity
@Table(name = "app_account_manage")
@ApiModel(value = "账户管理")
public class AppAccountManage {

    @Id
    private String guid;

    @ApiModelProperty(value = "账户名")
    @Column(name = "account_name")
    private String accountName;

    @ApiModelProperty(value = "角色名称")
    @Column(name = "app_role_name")
    private String appRoleName;

    @ApiModelProperty(value = "角色ID")
    @Column(name = "app_role_id")
    private String appRoleId;

    @ApiModelProperty(value = "员工编号")
    @Column(name = "person_no")
    private String personNo;

    @ApiModelProperty(value = "姓名")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "注销时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "cancel_time")
    private Date cancelTime;

    @ApiModelProperty(value = "应用ID")
    @Column(name = "app_id")
    private Integer appId;

    @ApiModelProperty(value = "应用名称")
    @Column(name = "app_name")
    private String appName;

    @ApiModelProperty(value = "登录ip")
    @Column(name = "ip")
    private String ip;
    @Transient
    @ApiModelProperty(value = "部门")
    private String orgName;





}
