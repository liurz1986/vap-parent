package com.vrv.vap.xc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资产VO
 */
@Data
@ApiModel(value = "资产实体VO")
public class AssetVO {
    @ApiModelProperty("资产guid")
    private String guid;
    @ApiModelProperty("资产名称")
    private String name;
    @ApiModelProperty("资产英文名称")
    private String nameEn;
    @ApiModelProperty("资产IP")
    private String ip;
    @ApiModelProperty("安全域Guid")
    private String securityGuid;
    @ApiModelProperty("安全域名称")
    private String securityName;
    @ApiModelProperty("资产版本信息")
    private String versionInfo;
    @ApiModelProperty("资产创建时间")
    private String createTime;
    @ApiModelProperty("资产品牌型号guid")
    private String assetTypeSnoGuid;
    @ApiModelProperty("资产类型guid")
    private String assetTypeGuid;
    @ApiModelProperty("资产类型guid")
    private String assetType; //assetTypeGuid
    @ApiModelProperty("资产类型")
    private String typeName;
    @ApiModelProperty("资产类型guid")
    private String typeGuid;
    @ApiModelProperty("资产品牌名称")
    private String typeSnoName;
    @ApiModelProperty("资产标签名称")
    private String tags;
    @ApiModelProperty("查询类型")
    private String type;
    @ApiModelProperty("资产mac地址")
    private String mac;
    @ApiModelProperty("资产第一责任人")
    private String employeeCode1;
    @ApiModelProperty("资产第二责任人")
    private String employeeCode2;
    @ApiModelProperty("资产监控")
    private String monitor;
    @ApiModelProperty("资产种类")
    private String special;
    @ApiModelProperty("资产是否可以监控")
    private String canMonitor;
    @ApiModelProperty("资产是否可以远程监控")
    private String canRCtrl;
    @ApiModelProperty("资产监控协议")
    private String protocol;

    @ApiModelProperty("楚天云创建时间")
    private String ctyTime;

    @ApiModelProperty("机构")
    private String org;// 机构
    @ApiModelProperty("核心资产")
    private Boolean core;// 核心资产
    @ApiModelProperty("资产价值")
    private String worth;
    @ApiModelProperty("资产安全性")
    private String secrecy;
    @ApiModelProperty("资产可靠性")
    private String integrity;
    @ApiModelProperty("资产可用性")
    private String availability;
    @ApiModelProperty("资产类型编号")
    private String typeUnicode;
    @ApiModelProperty("资产品牌编号")
    private String snoUnicode;
    @ApiModelProperty("弱点值")
    private String weakNessWorth;
    @ApiModelProperty("威胁值")
    private String threatenfreqWorth;
    @ApiModelProperty("风险值")
    private String riskWorth;
    @ApiModelProperty("风险事件值")
    private String riskEventWorth;
    @ApiModelProperty("最后发生风险时间")
    private String lastThreatenTime;
    @ApiModelProperty("资产责任人")
    private String employeeNames;
    @ApiModelProperty("资产告警数")
    private int alarmCount;
    @ApiModelProperty("运行状态")
    private String runStatus;
    @ApiModelProperty("资产编号")
    private String assetNum; //资产编号
    @ApiModelProperty("资产用途")
    private String assetUse;//资产用途
    @ApiModelProperty("物理位置")
    private String location;//物理位置
    @ApiModelProperty("备注")
    private String assetDescribe;// 备注
    @ApiModelProperty("监控状态")
    private Boolean onMonitor;
    @ApiModelProperty("配置监控")
    private Boolean configMonitor;
    @ApiModelProperty("是否确认规则")
    private Boolean configAlertRule = false;
    @ApiModelProperty("经度")
    private BigDecimal lng;// 经度
    @ApiModelProperty(" 纬度")
    private BigDecimal lat;// 纬度
    @ApiModelProperty("网关快速登录状态 0 快速链接正常； 1 快速链接未配置;2.快速链接配置无法ping通")
    private String quickLoginStatus; //网关快速登录状态 0 快速链接正常； 1 快速链接未配置;2.快速链接配置无法ping通
    @ApiModelProperty("快速链接具体信息")
    private String quickExtendInfo; //快速链接具体信息
    @ApiModelProperty("资产类型图片")
    private String image; //资产类型图片
    @ApiModelProperty("应用系统名称")
    private String appName;
    @ApiModelProperty("应用系统id")
    private String appId;
    @ApiModelProperty("资产第一责任人guid")
    private String employeeGuid;
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssetVO other = (AssetVO) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        return result;
    }


    /**
     * 所属机柜guid
     */
    private String cabinetGuid;

    /**
     * 距离底部高度
     */
    private int marginBottom;

    /**
     * 占U口个数
     */
    private int height;

    private String assetExtendInfo;

    //TODO 准入网关信息添加
    private String gatewayName; //网关名称
    private String gatewayNum;//序列号
    private String gatewayUser; //主管人员
    private String gatewayDepartment;//主管部门
    private String phoneNum;//联系电话
    private String remarkInfo;//备注

    private String equipmentIntensive;//  绝密5，机密4，秘密3，内部2，非密1 2021-08-13

    private int safeSecretProduceNum;//  保密产品数量 2021-08-13

    private String serialNumber; // 序列号

    private String orgName; // 组织机构名称 2021-08-20

    private String orgCode; // 组织机构code 2021-08-20

    private String responsibleName; //  2021-08-20 对应用户的userName

    private String responsibleCode; //  2021-08-20 对应用户的userNo
    private String userName; //  2023-07-12 对应用户的name
    private String account; //  2023-07-12 对应用户的account
    private int eventNumber; //  2023-07-12 对应事件数量
    private Integer assetStealLeakValue; //  2023-07-12 对应窃泄密值
    private String termType;// 国产与非国产 2021-08-20

    private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装

    private String osSetuptime;// 操作系统安装时间

    private String osList;//  安装操作系统
    private String terminalType;//  终端类型 ：1.运维终端 2. 用户终端

    private String order_;    // 排序字段
    private String by_;   // 排序顺序
    private Integer start_;//起始页
    private Integer count_; //总数

    private String importance; // 业务重要性

    private String loadBear; // 系统资产业务承载性
    // 新增字段
    private String vid; // VID

    private String pid; // PID

    private boolean focusAssets; //是否为关注资产

    private String operationUrl; // 管理入口url

}
