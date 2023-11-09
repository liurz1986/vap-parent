package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 数据同步查询资产信息对象
 * 2022-06-21
 */
@Data
public class AssetQueryVO {
    private String guid; // 资产guid
    private String name; //资产名称
    private String ip; //ip地址
    private String mac; //mac地址
    private String serialNumber; // 序列号
    private String typeTreeCode; //二级资产类型treeCode
    private String assetType; // 二级资产的guid
    private String orgName; // 组织机构名称(单位、部门)
    private String orgCode; // 组织机构code
    private String responsibleName; // 责任人名称
    private String responsibleCode; // 责任人名称
    private String securityGuid; // 安全域Code
    private String domainSubCode; //安全域subcode
    private String domainName; //安全域名称
    private String equipmentIntensive;//  涉密等级
    private Date createTime;  // 创建时间
    private Date osSetuptime; //系统安装时间
    private String osList;// 操作系统
    private String termType;// 国产与非国产   1：表示国产 2：非国产
    private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装
    private String terminalType;// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
    private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现
    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs


}
