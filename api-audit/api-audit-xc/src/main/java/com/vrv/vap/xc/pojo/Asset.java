package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 资产
 */
@Data
@TableName("asset")
public class Asset implements Serializable {
	private static final long serialVersionUID = -4352105771239783453L;
	private String guid;

	private String assetTypeSnoGuid;

	private String assetType;

	private String name;

	private String nameEn;

	private String ip;

	private String securityGuid; // 安全域guid

	private Long ipNum;

	private String versionInfo;

	private Date createTime;

	private String tags;

	private String mac;

	private String employeeCode1;

	private String employeeCode2;

	private String monitor;

	private String special;

	private String canMonitor;
	private String assetNum; // 资产编号
	private String assetUse;// 资产用途
	private String location;// 物理位置
	private String assetDescribe;// 备注
	private BigDecimal lng;// 经度
	private BigDecimal lat;// 纬度

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
	private Integer height;
	private String protocol;
	private String worth;
	private String typeUnicode;
	private String snoUnicode;


	//TODO 接入网关控制属性
	private String gatewayName; //网关名称
	private String gatewayNum;//序列号
	private String gatewayUser; //主管人员
	private String gatewayDepartment;//主管部门
	private String phoneNum;//联系电话
	private String remarkInfo;//备注

	private String org;// 机构


	private Boolean core;// 核心资产


	private String appId;
	private String appName;
	private String employeeGuid;

	private String domainSubCode;

	private String labels;//  资产状态标签

	private String equipmentIntensive;//  涉密等级 绝密0，机密1，秘密2，内部3，非密4
	private String serialNumber; // 序列号

	private String orgName; // 组织机构名称(单位、部门) 2021-08-20

	private String orgCode; // 组织机构code 2021-08-20

	private String responsibleName; // 责任人名称:普通用户、管理员 2021-08-20 对应用户的userName

	private String responsibleCode; // 责任人名称:普通用户、管理员 2021-08-20 对应用户的userNo

	private String termType;// 国产与非国产 2021-08-20  1：表示国产 2：非国产

	private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装

	@JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date osSetuptime;// 操作系统安装时间

	private String osList;// 安装操作系统

	private String terminalType;// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
	
	private int safeSecretProduceNum;   //安全产品保密数


	//1,登陆域名称
	private String domainName;
	//2,杀毒软件安装情况
	private Integer installAntiVirusStatus;
	//3,主审客户端在线状态
	private Integer clientStatus;
	//4,设备在线情况
	private Integer deviceStatus;
	//5,主审客户端最近一次上报时间
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date clientUpLastTime;
	//6.设备Id
	private String deviceId;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;   //更新时间
	private Integer clinetTimeDifference;   //当前时间与主审客户端最近一次上报时间(clientUpLastTime)的差值，分钟表示

	private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现;4探针发现
	private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
	private String syncUid;   //外部来源主键ID

	// 资产价格涉及的五权
	private String secrecy; // 资产保密性权值
	private String integrity;  // 资产完整性权值
	private String availability; // 资产可用性权值
	private String importance; // 业务重要性
	private String loadBear; // 系统资产业务承载性
	// 新增PID(pid)和VID(vid)
	private String vid; // VID
	private String pid; // PID
    // 增加管理入口URL 202309019
	private String operationUrl; // 管理入口url
}
