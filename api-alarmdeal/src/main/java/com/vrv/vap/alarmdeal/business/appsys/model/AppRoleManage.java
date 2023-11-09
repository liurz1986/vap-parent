package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lps 2021/8/9
 */

@Data
@Entity
@Table(name = "app_role_manage")
@ApiModel(value = "应用角色")
public class AppRoleManage {


    @Id
    private String guid;

    @ApiModelProperty(value = "角色名称")
    @Column(name = "app_role_name")
    private String roleName;

    @ApiModelProperty(value = "应用角色描述")
    @Column(name = "app_role_desc")
    private String appRoleDesc;

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


}
