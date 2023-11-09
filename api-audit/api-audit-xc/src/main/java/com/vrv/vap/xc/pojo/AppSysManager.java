package com.vrv.vap.xc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class AppSysManager {
    private Integer id;
    @ApiModelProperty(value = "应用编号")
    private String appNo;
    @ApiModelProperty(value = "应用系统名称")
    private String appName;
    @ApiModelProperty(value = "单位名称")
    private String departmentName;
    @ApiModelProperty(value = "单位GUID")
    private String departmentGuid;
    @ApiModelProperty(value = "域名")
    private String domainName;
    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;
    @ApiModelProperty(value = "涉密厂商")
    private String secretCompany;
    @ApiModelProperty(value = "服务器ID")
    private String serviceId;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty(value = "数据来源类型：1、手动录入；2 数据同步；3资产发现")
    private int dataSourceType;
    @ApiModelProperty(value = "外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs")
    private String syncSource;
    @ApiModelProperty(value = "外部来源主键ID")
    private String syncUid;
    @ApiModelProperty(value = "业务入口url")
    private String appUrl;
    @ApiModelProperty(value = "管理入口url")
    private String operationUrl;
}
