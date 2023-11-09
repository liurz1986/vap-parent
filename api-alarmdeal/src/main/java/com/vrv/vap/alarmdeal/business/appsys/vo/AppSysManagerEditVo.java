package com.vrv.vap.alarmdeal.business.appsys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author lps 2021/9/7
 */

@Data
public class AppSysManagerEditVo {

    private Integer id;

    @ApiModelProperty(value = "应用编号")
    private String appNo;

    /**
     * 应用系统名称
     */
    @ApiModelProperty(value = "应用系统名称")
    private String appName;

    /**
     * 单位名称
     */
    @ApiModelProperty(value = "单位名称")
    private String departmentName;

    /**
     * 单位GUID
     */
    @ApiModelProperty(value = "单位GUID")
    private String departmentGuid;

    /**
     * 域名
     */
    @ApiModelProperty(value = "域名")
    private String domainName;

    /**
     * 涉密等级
     */
    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;


    /**
     * 账号数量
     */
    @ApiModelProperty(value = "账号数量")
    private Integer accountCount=0;


    @ApiModelProperty(value = "涉密厂商")
    private String secretCompany;


    private String appUrl;   //业务入口
    private String operationUrl;   //管理入口

}
