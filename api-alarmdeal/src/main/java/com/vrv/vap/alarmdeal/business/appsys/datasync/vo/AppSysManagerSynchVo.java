package com.vrv.vap.alarmdeal.business.appsys.datasync.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用系统同步数据实体
 */

@Data
public class AppSysManagerSynchVo {

    private Integer id;

    @ApiModelProperty(value = "应用编号")
    private String appNo;

    /**
     * 应用系统名称
     */
    @ApiModelProperty(value = "应用系统名称")
    private String appName;

    /**
     * 单位名称(组织名称)
     */
    @ApiModelProperty(value = "单位名称")
    private String departmentName;

    /**
     * 单位Code(组织code)
     */
    @ApiModelProperty(value = "单位Code")
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
     * 涉密厂商
     */
    @ApiModelProperty(value = "涉密厂商")
    private String secretCompany;


    private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现

    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs

    private String syncUid;   //外部来源主键ID


}
