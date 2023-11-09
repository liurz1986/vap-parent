package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class CtyBaseSysInfoVO extends PageModel {


    /**
     *   主键 主键/NOT NULL/自增长
     */
    @ApiModelProperty("主键 主键/NOT NULL/自增长")
    private Integer id;

    /**
     *   系统名称
     */
    @ApiModelProperty("系统名称")
    private String name;

    /**
     *   系统域名
     */
    @ApiModelProperty("系统域名")
    private String domain;

    /**
     *   所属部门
     */
    @ApiModelProperty("所属部门")
    private String orgCode;

    /**
     *   所属区域
     */
    @ApiModelProperty("所属区域")
    private String areaCode;

    /**
     *   网站站点类别
     */
    @ApiModelProperty("网站站点类别")
    private Integer siteTypeId;

    /**
     *   登陆安全级别
     */
    @ApiModelProperty("登陆安全级别")
    private Integer securityLevelId;

    /**
     *   业务分类
     */
    @ApiModelProperty("业务分类")
    private String appTypeId;

    /**
     *   网站负责人PKI证书号
     */
    @ApiModelProperty("网站负责人PKI证书号")
    private String certsn;

    /**
     *   是否启用分析
     */
    @ApiModelProperty("是否启用分析")
    private Integer isAnalysis;

    /**
     *   标签
     */
    @ApiModelProperty("标签")
    private String tags;

    /**
     *   重要等级
     */
    @ApiModelProperty("重要等级")
    private Integer importantTypeId;

    /**
     *   警种
     */
    @ApiModelProperty("警种")
    private String policeTypeId;

    /**
     *   科室名称
     */
    @ApiModelProperty("科室名称")
    private String office;

    /**
     *   网站负责人
     */
    @ApiModelProperty("网站负责人")
    private String userName;

    /**
     *   联系电话
     */
    @ApiModelProperty("联系电话")
    private String tel;

    /**
     *   邮箱地址
     */
    @ApiModelProperty("邮箱地址")
    private String email;

    /**
     *   接入方式为其他的信息
     */
    @ApiModelProperty("接入方式为其他的信息")
    private String other;

    /**
     *   边界设备IP
     */
    @ApiModelProperty("边界设备IP")
    private String boundaryIp;

    /**
     *   边界设备信息ID
     */
    @ApiModelProperty("边界设备信息ID")
    private Integer boundaryId;

    /**
     *   接入方式 0-其他 1-边界设备
     */
    @ApiModelProperty("接入方式 0-其他 1-边界设备")
    private Integer accessType;

    /**
     *   是否与其他网络交换数据 0-否  1-是
     */
    @ApiModelProperty("是否与其他网络交换数据 0-否  1-是")
    private Integer isOtherTrans;

    /**
     *   归类时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("归类时间")
    private Date addTime;

    /**
     *   修改时间 数据更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("修改时间 数据更新时间")
    private Date updateTime;

    /**
     *   系统编号 对应巨龙应用系统编号
     */
    @ApiModelProperty("系统编号 对应巨龙应用系统编号")
    private String systemId;

    /**
     *   系统等级
     */
    @ApiModelProperty("系统等级")
    private String sysLevel;

    /**
     *   系统状态
     */
    @ApiModelProperty("系统状态")
    private String sysState;

    /**
     *   系统类别
     */
    @ApiModelProperty("系统类别")
    private String sysType;

    /**
     *   信息应用域
     */
    @ApiModelProperty("信息应用域")
    private String infoScope;

    /**
     *   开发语言
     */
    @ApiModelProperty("开发语言")
    private String developLanguage;

    /**
     *   系统架构
     */
    @ApiModelProperty("系统架构")
    private String sysFramework;

    /**
     *   所使用的第三方插件
     */
    @ApiModelProperty("所使用的第三方插件")
    private String otherPlugin;

    /**
     *   所需要的外接设备
     */
    @ApiModelProperty("所需要的外接设备")
    private String externalDevice;

    /**
     *   外接设备型号（厂家、版本）
     */
    @ApiModelProperty("外接设备型号（厂家、版本）")
    private String extDeviceInfo;

    /**
     *   浏览器及版本号
     */
    @ApiModelProperty("浏览器及版本号")
    private String browserInfo;

    /**
     *   账户类型
     */
    @ApiModelProperty("账户类型")
    private String accountType;

    /**
     *   个人用户登录方式
     */
    @ApiModelProperty("个人用户登录方式")
    private String userLoginType;

    /**
     *   是否有日志审计模块
     */
    @ApiModelProperty("是否有日志审计模块")
    private String isAuditModel;

    /**
     *   是否已对接安审平台
     */
    @ApiModelProperty("是否已对接安审平台")
    private String isSecurityPlatform;

    /**
     *   ip地址
     */
    @ApiModelProperty("ip地址")
    private String ip;

    /**
     *   服务器类型
     */
    @ApiModelProperty("服务器类型")
    private String serverType;

    /**
     *   网络
     */
    @ApiModelProperty("网络")
    private String network;

    /**
     *   数据库类型
     */
    @ApiModelProperty("数据库类型")
    private String databaseType;

    /**
     *   数据库实例名
     */
    @ApiModelProperty("数据库实例名")
    private String databaseName;

    /**
     *   信息化室对口民警
     */
    @ApiModelProperty("信息化室对口民警")
    private String infoOfficePolice;

    /**
     *   承建公司
     */
    @ApiModelProperty("承建公司")
    private String buildCompany;

    /**
     *   承建公司联系人
     */
    @ApiModelProperty("承建公司联系人")
    private String buildContact;

    /**
     *   承建公司联系人电话
     */
    @ApiModelProperty("承建公司联系人电话")
    private String buildTel;

    /**
     *   数据库管理员
     */
    @ApiModelProperty("数据库管理员")
    private String dba;

    /**
     *   开发完成时间
     */
    @ApiModelProperty("开发完成时间")
    private String completeTime;

    /**
     *   是否使用文字处理软件
     */
    @ApiModelProperty("是否使用文字处理软件")
    private String isFontSoftware;

    /**
     *   与国产文字处理软件是否兼容
     */
    @ApiModelProperty("与国产文字处理软件是否兼容")
    private String isFontCompatible;

    /**
     *   上级系统ID
     */
    @ApiModelProperty("上级系统ID")
    private String upSysId;

    /**
     *   用户ID
     */
    @ApiModelProperty("用户ID")
    private String userId;

    /**
     *   应用系统等级: 1App,2模块
     */
    @ApiModelProperty("应用系统等级: 1App,2模块")
    private String isAppOrModel;

    /**
     *   建库模式
     */
    @ApiModelProperty("建库模式")
    private String createModel;

    /**
     *   是否有查询模块
     */
    @ApiModelProperty("是否有查询模块")
    private String isQueryModel;

    /**
     *   系统来源: 巨龙/第三方/移动警务等
     */
    @ApiModelProperty("系统来源: 巨龙/第三方/移动警务等")
    private Integer sourceType;

    /**
     *   系统标识:1.系统 2.网站
     */
    @ApiModelProperty("系统标识:1.系统 2.网站")
    private Integer webType;

    /**
     *   是否完成PK/PMI改造 1是,0否
     */
    @ApiModelProperty("是否完成PK/PMI改造 1是,0否")
    private String isOverPkiOrPmi;

    /**
     *   是否定级备案 1是,0否
     */
    @ApiModelProperty("是否定级备案 1是,0否")
    private String isRankRecord;

    /**
     *   定级情况 一级、二级、三级、四级
     */
    @ApiModelProperty("定级情况 一级、二级、三级、四级")
    private String rankLevel;

    /**
     *   地市区域代码
     */
    @ApiModelProperty("地市区域代码")
    private String cityAreaCode;

    @ApiModelProperty("地市区域名")
    private String cityAreaName;


    /**
     *楚天云扩展子段
     */

    private Info info;

    @Data
    public static  class Info{

        @ApiModelProperty("关联虚拟机数量")
        private Integer  vmCount;

        @ApiModelProperty("关联物理机数")
        private Integer  pmCount;

        @ApiModelProperty("关联托管主机数量")
        private Integer  tmCount;

        @ApiModelProperty("关中间件类型")
        private String  middlewareType;


        @ApiModelProperty("项目经理ID")
        private Integer  pmId;

        @ApiModelProperty("技术经理ID")
        private Integer  techId;

        @ApiModelProperty("业务敏感时间")
        private String  sensTime;

        @ApiModelProperty("组织名称")
        private  String organizationName;

        @ApiModelProperty("组织简称")
        private  String organizationSimpleName;

        @ApiModelProperty("作者")
        private  String remarks;

        @ApiModelProperty("创建者姓名")
        private  String creatorName;

        @ApiModelProperty("数据中心")
        private  String dataCenter;


        @ApiModelProperty("修改时间")
        private  Date updateTime;

        @ApiModelProperty("修改者ID")
        private  Integer updatorId;

        @ApiModelProperty("修改者姓名")
        private  String updatorName;

    }






}
