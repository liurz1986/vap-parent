package com.vrv.vap.alarmdeal.business.appsys.vo;

/**
 * @author lps 2021/8/10
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "应用角色管理Vo")
public class AppRoleManageVo {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("角色名称", "角色描述", "创建时间", "注销时间","应用系统id"));
    public static final String[] KEYS= new String[]{"roleName","appRoleDesc","createTime","cancelTime","appId"};
    public static final String APP_ROLE_MANAGE="应用角色";

    private String guid;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "应用角色描述")
    private String appRoleDesc;

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
}
