package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 *
 * 资产审核表VO
 * @data 2022-05-27
 *
 */
@Data
public class AssetVerifyVO  {

	private String guid; // 资产guid

	private String assetType; // 二级资产类型guid

	private String type ; // 类型   一级资产类型名称-二级资产类型名称

	private String name; // 资产名称

	private String ip; // ip地址

	private String securityGuid; // 安全域guid

	private String domainName;  // 安全域名称

	private String domainSubCode;// 安全域subcode
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;  // 创建时间

	private String mac; // mac地址

	private String equipmentIntensive;//  涉密等级

	private String serialNumber; // 序列号

	private String orgName; // 组织机构名称

	private String orgCode; // 组织机构code

	private String responsibleName; // 责任人名称

	private String responsibleCode; // 责任人名称

	private String termType;// 国产与非国产   1：表示国产 2：非国产

	private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	private Date osSetuptime;// 操作系统安装时间

	private String osList;// 安装操作系统

	private String terminalType;// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
     /***************/
	private Integer installAntiVirusStatus; // 杀毒软件安装情况

	private Integer clientStatus; // 主审客户端在线状态

	private Integer deviceStatus; //设备在线情况
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date clientUpLastTime; // 主审客户端最近一次上报时间

	private String deviceId; // 设备Id

	private Integer clinetTimeDifference;   //当前时间与主审客户端最近一次上报时间(clientUpLastTime)的差值，分钟表示
	/***************/
	private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现

	private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs

	private String syncUid;   //外部来源主键ID

	private int syncStatus; // 状态：1、待编辑；2、待入库、3、入库失败 ;4、已入库(入库成功);5、已忽略
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date syncTime; // 同步时间

	private String assetId; // 关联正式库资产ID

	private String syncMessage; // 同步错误信息

	private String  extendInfos;// 扩展信息 json格式
}
