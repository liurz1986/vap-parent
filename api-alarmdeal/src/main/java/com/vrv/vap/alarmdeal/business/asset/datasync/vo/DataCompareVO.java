package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 比对结果
 *
 */
@Data
public class DataCompareVO {
    // 17个比对字段//
    private List<String> name; //资产名称
    private List<String> ip; //ip地址
    private List<String> mac; //mac地址
    private List<String> typeGuid; // 二级资产类型Guid，对应AssetVerify和Asset中的是assetType
    private List<String> orgName; // 组织机构名称(单位、部门)
    private List<String> responsibleName; // 责任人名称
    private List<String>  typeSnoGuid;// 品牌型号
    private List<String> equipmentIntensive;//  涉密等级
    private List<String> serialNumber; // 序列号
    private List<String> osSetuptime;// 操作系统安装时间
    private List<String> osList;// 安装操作系统
    private List<String> location;// 位置
    private List<String> remarkInfo;// 备注
    private List<String>  assetNum;// 设备编号
    private List<String>  extendDiskNumber; //磁盘序列号
    private List<String> registerTime;// 启用时间
    private List<String> deviceDesc;// 小类
    private List<String> deviceArch;// 架构
    // 比对结果
    private boolean result = true;
    // 关联明细表guid
    private List<String> refGuid;

    public DataCompareVO(){
        name = new ArrayList<>();
        ip = new ArrayList<>();
        mac = new ArrayList<>();
        typeGuid = new ArrayList<>();
        orgName = new ArrayList<>();
        responsibleName = new ArrayList<>();
        typeSnoGuid = new ArrayList<>();
        equipmentIntensive = new ArrayList<>();
        serialNumber = new ArrayList<>();
        osSetuptime = new ArrayList<>();
        osList = new ArrayList<>();
        location = new ArrayList<>();
        remarkInfo = new ArrayList<>();
        assetNum = new ArrayList<>();
        extendDiskNumber = new ArrayList<>();
        registerTime = new ArrayList<>();
        deviceDesc = new ArrayList<>();
        refGuid = new ArrayList<>();
        deviceArch = new ArrayList<>();
    }
}
