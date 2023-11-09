package com.vrv.vap.alarmdeal.business.asset.vo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

/**
 * 主审资产同步传输对象
 * 2022-4-14
 */
@Data
public class AuditAssetVO {
    //*****asset表新增字段******//
    // 1.登陆域名称
    @SerializedName(value = "domainName",alternate = {"DomainName"})
    private String domainName;
    //2,杀毒软件安装情况 需要进行计算(1表示安装: 0表示未安装)
    private Integer installAntiVirusStatus;
    //3,主审客户端在线状态 需要进行计算
    private Integer clientStatus;
    //4,设备在线情况
    @SerializedName(value = "deviceStatus",alternate = {"RunStatus"})
    private Integer deviceStatus;
    //5,主审查客户端注册状态（对应asset表ismonitor_agent）
    @SerializedName(value = "clientRegister",alternate = {"Registered"})
    private Integer clientRegister;
    //6,主审客户端最近一次上报时间
    @SerializedName(value = "clientUpLastTime",alternate = {"LastTime"})
    private Date clientUpLastTime;
    //7.设备id
    @SerializedName(value = "deviceId",alternate = {"DeviceID"})
    private String deviceId;
    //*****对asset表现有字段*****//
    //计算机名称(对应asset表中name)
    @SerializedName(value ="name",alternate = {"DeviceName"})
    private String name;
    //ip地址(对应asset表ip地址)---备注ipAddress多个用分号;分割
    @SerializedName(value = "ipAddress",alternate = {"IPAddres"})
    private String ipAddress;
    // 设备密级(对应asset表equipment_intensive)
    @SerializedName(value = "equipmentIntensive",alternate = {"RunLevel"})
    private String equipmentIntensive;
    // mac地址(对应asset表mac)
    @SerializedName(value = "mac",alternate = {"MacAddress"})
    private String mac;
    // 序列号(对应asset表serial_number)
    @SerializedName(value = "serialNumber",alternate = {"DiskSerial"})
    private String serialNumber;
    // 终端类型操作系统安装时间(对应asset表os_setup_time)
    @SerializedName(value = "ossetuptime",alternate = {"SetupTmos"})
    private Date ossetuptime;
    // 终端类型安装操作系统(对应asset表os_list)
    @SerializedName(value = "osList",alternate = {"OSType"})
    private String osList;
}
