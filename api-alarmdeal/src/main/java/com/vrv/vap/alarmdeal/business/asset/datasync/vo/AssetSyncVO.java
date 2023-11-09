package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 获取kafka资产数据  2022-05-10
 *
 */
@Data
public class AssetSyncVO implements Serializable {

	private static final long serialVersionUID = -4352105771239783453L;
	private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现
	private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
	private String syncUid;   //外部来源主键ID
	private String name; //资产名称
	private String ip; //ip地址
	private String mac; //mac地址
	private String typeGuid; // 二级资产类型Guid，对应AssetVerify和Asset中的是assetType
	private String typeUnicode; //二级资产类型UniqueCode
	private String orgName; // 组织机构名称(单位、部门)
	private String orgCode; // 组织机构code
	private String responsibleName; // 责任人名称
	private String responsibleCode; // 责任人名称
	private String securityGuid; // 安全域Code
	private String domainSubCode; //安全域subcode
	private String domainName; //安全域名称
	private String equipmentIntensive;//  涉密等级
	private String serialNumber; // 序列号
	private String termType;// 国产与非国产
	private String terminalType;// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
	private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date osSetuptime;// 操作系统安装时间
	private String osList;// 安装操作系统
	private Integer installAntiVirusStatus; // 杀毒软件安装情况
	private Integer clientStatus; // 主审客户端在线状态
	private Integer deviceStatus; //设备在线情况
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date clientUpLastTime; // 主审客户端最近一次上报时间
	private String deviceId; // 设备Id
	private Integer clinetTimeDifference;   //当前时间与主审客户端最近一次上报时间(clientUpLastTime)的差值，分钟表示
	private String  extendInfos;// 扩展信息 json格式
	// 新增的字段 2023-4//
	private String location;// 位置
	private String remarkInfo;// 备注
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date registerTime;// 启用时间
	private String  extendDiskNumber; //磁盘序列号
	private String  typeSnoGuid;// 品牌型号
	private String deviceArch;// 架构
	private String  deviceDesc;// 设备类型(小类)
	private String assetNum; // 设置编号
	// 2023-4-20
	private String batchNo;// 同步的批次，每批的值一样

	//--------------非kafka同步的数据-------------//
	private String guid; // 资产guid
	private AssetType assetType;// 该字段主要数据校验，不用于接受kafka 数据
	private boolean isUsb;// 该字段主要数据校验，不用于接受kafka 数据

}
