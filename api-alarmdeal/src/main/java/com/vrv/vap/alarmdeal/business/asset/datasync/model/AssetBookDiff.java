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
 * 差异记录表
 * 2023-4
 */
@Data
@Table(name="asset_book_diff")
@Entity
public class AssetBookDiff {
    @Id
    @Column(name="guid")
    private String guid;
    //---18个比对字段//
    @Column(name="type_guid")
    private String typeGuid; // 二级资产类型Guid，对应AssetVerify和Asset中的是assetType
    @Column(name="device_desc")
    private String  deviceDesc;// 设备类型(小类)
    @Column(name="asset_num")
    private String  assetNum;// 设备编号
    @Column(name="name")
    private String name; //资产名称
    @Column(name="responsible_name")
    private String responsibleName; // 责任人名称
    @Column(name="ip")
    private String ip; //ip地址
    @Column(name="mac")
    private String mac; //mac地址
    @Column(name="org_name")
    private String orgName; // 组织机构名称(单位、部门)
    @Column(name="register_time")
    private String registerTime;// 启用时间
    @Column(name="serial_number")
    private String serialNumber; // 序列号
    @Column(name="equipment_intensive")
    private String equipmentIntensive;//  涉密等级
    @Column(name="location")
    private String location;// 位置
    @Column(name="extend_disk_number")
    private String  extendDiskNumber; //磁盘序列号
    @Column(name="os_list")
    private String osList;// 安装操作系统
    @Column(name="os_setup_time")
    private String osSetupTime;// 操作系统安装时间
    @Column(name="type_Sno_Guid")
    private String  typeSnoGuid;// 品牌型号
    @Column(name="remark_info")
    private String remarkInfo;// 备注
    @Column(name="device_arch")
    private String  deviceArch;// 架构
    //---18个比对字段//
    @Column(name="handle_status")
    private String handleStatus;// 处理状态 "1"标识已经处理
    @Column(name="ref_detail_guid")
    private String refDetailGuid;// 关联详情的guid
    @Column(name="ref_asset_guid")
    private String refAsetGuid;// 关联正式库的guid
    @Column(name="create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;   //记录创建时间
}
