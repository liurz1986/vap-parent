package com.vrv.vap.alarmdeal.business.appsys.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lps 2021/8/10
 */

@Data
@ApiModel(value = "应用账户管理Vo")
public class AppAccountManageVo extends PageReqVap {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("账户名称", "用户编号", "姓名","角色名称", "创建时间", "注销时间","登录IP","应用系统id"));
    public static final String[] KEYS= new String[]{"accountName","personNo","name","appRoleName","createTime","cancelTime","ip","appId"};

    public static final String  APP_ACCOUNT_MANAGE ="应用账号";

    private String guid;

    @ApiModelProperty(value = "账户名")
    private String accountName;

    @ApiModelProperty(value = "角色名称")
    private String appRoleName;

    @ApiModelProperty(value = "角色ID")
    private String appRoleId;

    @ApiModelProperty(value = "员工编号")
    private String personNo;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "注销时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    @ApiModelProperty(value = "应用ID")
    private Integer appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    // 非必填，填什么入什么     2022-04-25
    @ApiModelProperty(value = "登录ip")
    private String ip;



}
