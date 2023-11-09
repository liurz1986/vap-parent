package com.vrv.vap.alarmdeal.business.asset.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 资产
 * @author wd-pc
 * 资产模块用到---20210924
 */
@Data
@Table(name="asset")
@Entity
@ApiModel(value = "资产主表")
public class Asset implements Serializable {


	private static final long serialVersionUID = -4352105771239783453L;
	@Id
	@Column(name="Guid")
	private String guid;
	@Column(name="Type_Sno_Guid")
	private String assetTypeSnoGuid;
	@Column(name="Type_Guid")
	private String assetType;
	@Column(name="Name")
	private String name;
	@Column(name="Name_en")
	private String nameEn;
	@Column(name="ip")
	private String ip;

	@Column(name="securityGuid")
	private String securityGuid; // 安全域guid

	@Column(name="ipNum")
	private Long ipNum;
	@Column(name="Version_info")
	private String versionInfo;
	@Column(name="CreateTime")
	private Date createTime;
	// 目前存在的一级资产类型treeCode 2023-09-19
	@Column(name="Tags")
	private String tags;
	@Column(name="mac")
	private String mac;
	@Column(name="employee_Code1")
	private String employeeCode1;
	@Column(name="employee_Code2")
	private String employeeCode2;
	@Column(name="monitor")
	private String monitor;
	@Column(name="special")
	private String special;
	@Column(name="canMonitor")
	private String canMonitor;
	@Column(name="canRCtrl")
	private String canRCtrl;
	@Column(name="assetNum")
	private String assetNum; // 资产编号
	@Column(name="assetUse")
	private String assetUse;// 资产用途
	@Column(name="location")
	private String location;// 物理位置
	@Column(name="AssetDescribe")
	private String assetDescribe;// 备注
	@Column(name="lng")
	private BigDecimal lng;// 经度
	@Column(name="lat")
	private BigDecimal lat;// 纬度

	/**
	 * 所属机柜guid
	 */
	@Column(name="cabinetGuid")
	private String cabinetGuid;

	/**
	 * 距离底部高度
	 */
	@Column(name="marginBottom")
	private int marginBottom;

	/**
	 * 占U口个数
	 */
	@Column(name="height")
	private Integer height;
	@Column(name="protocol")
	private String protocol;
	@Column(name="worth")
	private String worth;
	@Column(name="typeUnicode")
	private String typeUnicode;
	@Column(name="snoUnicode")
	private String snoUnicode;
	/*	private Date lastThreatenTime; */

	/*	private Integer assetRuleNum;*/


	//TODO 接入网关控制属性
	@Column(name="gatewayName")
	private String gatewayName; //网关名称
	@Column(name="gatewayNum")
	private String gatewayNum;//序列号
	@Column(name="gatewayUser")
	private String gatewayUser; //主管人员
	@Column(name="gatewayDepartment")
	private String gatewayDepartment;//主管部门
	@Column(name="phoneNum")
	private String phoneNum;//联系电话
	@Column(name="remarkInfo")
	private String remarkInfo;//备注

	@Column(name="org")
	private String org;// 机构


	@Column(name="core")
	private Boolean core;// 核心资产


	@Column(name="app_id")
	private String appId;//
	@Column(name="app_name")
	private String appName;//
	@Column(name="employee_guid")
	private String employeeGuid;//

	@Column(name="domain_sub_code")
	private String domainSubCode;//

	@Column(name="labels")
	private String labels;//  资产状态标签

	@Column(name = "equipment_intensive")
	private String equipmentIntensive;//  涉密等级 绝密0，机密1，秘密2，内部3，非密4
	@Column(name = "serial_number")
	private String serialNumber; // 序列号

	@Column(name = "org_name")
	private String orgName; // 组织机构名称(单位、部门) 2021-08-20

	@Column(name = "org_code")
	private String orgCode; // 组织机构code 2021-08-20

	@Column(name = "responsible_name")
	private String responsibleName; // 责任人名称:普通用户、管理员 2021-08-20 对应用户的userName

	@Column(name = "responsible_code")
	private String responsibleCode; // 责任人名称:普通用户、管理员 2021-08-20 对应用户的userNo

	@Column(name = "term_type")
	private String termType;// 国产与非国产 2021-08-20  1：表示国产 2：非国产

	@Column(name = "ismonitor_agent")
	private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装

	@Column(name = "os_setup_time")
	@JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date osSetuptime;// 操作系统安装时间

	@Column(name = "os_list")
	private String osList;// 安装操作系统

	@Column(name = "terminal_Type")
	private String terminalType;// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
	
	@Column(name = "self_secret_product_num")
	private int safeSecretProduceNum;   //安全产品保密数


   // -----2022-4-14NTDS数据更新资产表数据---//
	//1,登陆域名称
	@Column(name = "domain_name")
	private String domainName;
	//2,杀毒软件安装情况
	@Column(name = "install_anti_virus_status")
	private Integer installAntiVirusStatus;
	//3,主审客户端在线状态
	@Column(name = "client_status")
	private Integer clientStatus;
	//4,设备在线情况
	@Column(name = "device_status")
	private Integer deviceStatus;
	//5,主审客户端最近一次上报时间
	@Column(name = "client_up_last_time")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date clientUpLastTime;
	//6.设备Id
	@Column(name="device_id")
	private String deviceId;
	@Column(name="update_time")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;   //更新时间
	@Column(name="clinet_time_difference")
	private Integer clinetTimeDifference;   //当前时间与主审客户端最近一次上报时间(clientUpLastTime)的差值，分钟表示

	@Column(name="data_source_type")
	private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现;4探针发现
	@Column(name="sync_source")
	private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
	@Column(name="sync_uid")
	private String syncUid;   //外部来源主键ID

	// 资产价格涉及的五权
	@Column(name="secrecy")
	private String secrecy; // 资产保密性权值
	@Column(name="integrity")
	private String integrity;  // 资产完整性权值
	@Column(name="availability")
	private String availability; // 资产可用性权值
	// 新增的
	@Column(name="importance")
	private String importance; // 业务重要性
	@Column(name="loadBear")
	private String loadBear; // 系统资产业务承载性
	// 新增PID(pid)和VID(vid)
	@Column(name="vid")
	private String vid; // VID
	@Column(name="pid")
	private String pid; // PID
    // 增加管理入口URL 202309019
	@Column(name="operation_url ")
	private String operationUrl; // 管理入口url
}
