package com.vrv.vap.alarmdeal.business.appsys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author lps 2021/8/9
 */

@Data
@ApiModel(value = "应用系统管理VO")
public class AppSysManagerVo {

    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("应用系统id","应用编号", "应用名称", "单位名称", "涉密等级","涉密厂商","域名","业务入口","管理入口"));
    public static final String[] KEYS= new String[]{"id","appNo","appName","departmentName","secretLevel","secretCompany","domainName","appUrl","operationUrl"};
    public static final String  APP_SYS_MANAGER="应用系统信息";

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
     * 服务器ID
     */
    @ApiModelProperty(value = "服务器ID")
    private String serviceId;

    /**
     * 服务器关联资产ips
     */
    @ApiModelProperty(value = "服务器关联资产ips")
    private String ips;
    /**
     * 账号数量
     */
    @ApiModelProperty(value = "账号数量")
    private Integer accountCount=0;


    @ApiModelProperty(value = "涉密厂商")
    private String secretCompany;

    /**
     * 角色数量
     */
    @ApiModelProperty(value = "角色数量")
    private Integer roleCount=0;

    /**
     * 资源数量
     */
    @ApiModelProperty(value = "资源数量")
    private Integer resourceCount=0;

    /**
     * 服务器数量
     */
    @ApiModelProperty(value = "服务器数量")
    private Integer serverCount=0;

    /**
     * 责任人
     */
    @ApiModelProperty(value = "责任人")
    private String personName;

    /**
     * 事件数量
     */
    @ApiModelProperty(value = "事件数量")
    private Integer countEvent;
    /**
     * 泄密
     */
    @ApiModelProperty(value = "泄密")
    private Integer stealLeakValue;

    @ApiModelProperty(value = "业务入口")
    private String appUrl;   //业务入口
    @ApiModelProperty(value = "管理入口")
    private String operationUrl;   //管理入口
    @ApiModelProperty(value = "仅看关注")
    Boolean isJustAssetOfConcern=false;
}
