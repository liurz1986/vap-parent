package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * asset表比对要用的数据
 * 2023-4
 */
@Data
public class AssetComparisonVO {
    private String guid;
    private String name; //资产名称
    private String ip; //ip地址
    private String mac; //mac地址
    private String typeGuid; // 二级资产类型Guid，对应AssetVerify和Asset中的是assetType
    private String orgName; // 组织机构名称(单位、部门)
    private String orgCode; // 组织机构code(单位、部门)
    private String responsibleName; // 责任人名称
    private String responsibleCode; // 责任人code
    private String  typeSnoGuid;// 品牌型号
    private String equipmentIntensive;//  涉密等级
    private String serialNumber; // 序列号
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date osSetuptime;// 操作系统安装时间
    private String osList;// 安装操作系统
    private String location;// 位置
    private String remarkInfo;// 备注
    private String  assetNum;// 设备编号
    private String  extendDiskNumber; //磁盘序列号


}
