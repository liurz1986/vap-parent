package com.vrv.vap.alarmdeal.business.asset.datasync.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 资产台账明细表
 * 2023-4
 */
@Data
@Table(name="asset_book_detail")
@Entity
public class AssetBookDetail {
    @Id
    @Column(name="guid")
    private String guid;
    @Column(name="name")
    private String name; //资产名称
    @Column(name="ip")
    private String ip; //ip地址
    @Column(name="mac")
    private String mac; //mac地址
    @Column(name="type_guid")
    private String typeGuid; // 二级资产类型Guid，对应AssetVerify和Asset中的是assetType
    @Column(name="type_unicode")
    private String typeUnicode;
    @Column(name="org_name")
    private String orgName; // 组织机构名称(单位、部门)
    @Column(name="org_code")
    private String orgCode; // 组织机构code(单位、部门)
    @Column(name="responsible_name")
    private String responsibleName; // 责任人名称
    @Column(name="responsible_code")
    private String responsibleCode; // 责任人code
    @Column(name="security_guid")
    private String securityGuid; // 安全域code
    @Column(name = "domain_name")
    private String domainName; // 安全域名称
    @Column(name="domain_sub_code")
    private String domainSubCode;//安全域SubCode
    @Column(name="type_Sno_Guid")
    private String  typeSnoGuid;// 品牌型号
    @Column(name="extend_disk_number")
    private String  extendDiskNumber; //磁盘序列号
    @Column(name="equipment_intensive")
    private String equipmentIntensive;//  涉密等级
    @Column(name="serial_number")
    private String serialNumber; // 序列号
    @Column(name = "term_type")
    private String termType;// 国产与非国产 2021-08-20  1：表示国产 2：非国产
    @Column(name = "terminal_Type")
    private String terminalType;// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
    @Column(name = "ismonitor_agent")
    private String isMonitorAgent;// 是否安装终端客户端 1.已安装；2.未安装
    @Column(name="os_setup_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date osSetuptime;// 操作系统安装时间
    @Column(name="os_list")
    private String osList;// 安装操作系统
    //杀毒软件安装情况
    @Column(name = "install_anti_virus_status")
    private Integer installAntiVirusStatus;
    //主审客户端在线状态
    @Column(name = "client_status")
    private Integer clientStatus;
    //设备在线情况
    @Column(name = "device_status")
    private Integer deviceStatus;
    //主审客户端最近一次上报时间
    @Column(name = "client_up_last_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date clientUpLastTime;
    //设备Id
    @Column(name="device_id")
    private String deviceId;
    //当前时间与主审客户端最近一次上报时间(clientUpLastTime)的差值，分钟表示
    @Column(name="clinet_time_difference")
    private Integer clinetTimeDifference;
    //数据来源类型：1、手动录入；2 数据同步；3资产发现
    @Column(name="data_source_type")
    private int dataSourceType;
    //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
    @Column(name="sync_source")
    private String syncSource;
    //外部来源主键ID
    @Column(name="sync_uid")
    private String syncUid;
    @Column(name="location")
    private String location;// 位置
    @Column(name="remark_info")
    private String remarkInfo;// 备注
    @Column(name="asset_num")
    private String  assetNum;// 设备编号
    @Column(name="device_arch")
    private String  deviceArch;// 架构
    @Column(name="device_desc")
    private String  deviceDesc;// 设备类型(小类)
    @Column(name="register_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;// 启用时间
    // 扩展字段
    @Column(name="extend_infos")
    private String  extendInfos;

    @Column(name="create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;   //记录创建时间
    // 2023-4-20
    @Column(name="batch_no")
    private String batchNo;// 同步的批次，每批的值一样
}
