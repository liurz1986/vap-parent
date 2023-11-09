package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 流量日志目的设备对应字段
 *
 * @author 梁国露
 * @date 2021年11月02日 11:44
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventLogDstBean extends EventLogBean {
    /**
     * IP
     */
    // @SerializedName(value = "dst_std_dev_ip",alternate = {"dip"})
    private String dstStdDevIp;

    /**
     * MAC
     */
    // @SerializedName(value = "dst_std_dev_mac")
    private String dstStdDevMac;

    /**
     * 硬盘序列号
     */
    // @SerializedName(value = "dst_std_dev_hardware_identification")
    private String dstStdDevHardwareIdentification;

    /**
     * 设备品牌型号
     */
    // @SerializedName(value = "dst_std_dev_brand_model")
    private String dstStdDevBrandModel;

    /**
     * 硬件设备型号
     */
    // @SerializedName(value = "dst_std_dev_hardware_model")
    private String dstStdDevHardwareModel;

    /**
     * 设备名称
     */
    // @SerializedName(value = "dst_std_dev_name")
    private String dstStdDevName;

    /**
     * 设备入网时间
     */
    // @SerializedName(value = "dst_std_dev_net_time")
    private String dstStdDevNetTime;

    /**
     * 操作系统类型
     */
    // @SerializedName(value = "dst_std_dev_os_type")
    private String dstStdDevOsType;

    /**
     * 所属安全域
     */
    // @SerializedName(value = "dst_std_dev_safety_marign")
    private String dstStdDevSafetyMarign;

    /**
     * 目标设备安全域名称
     */
    // @SerializedName(value = "dst_std_dev_safety_marign_name")
    private String dstStdDevSafetyMarignName;

    /**
     * 软件系统版本号
     */
    // @SerializedName(value = "dst_std_dev_software_version")
    private String dstStdDevSoftwareVersion;

    /**
     * 设备密级
     */
    // @SerializedName(value = "dst_std_dev_level")
    private String dstStdDevLevel;

    /**
     * 设备二级类型
     */
    // @SerializedName(value = "dst_std_dev_type")
    private String dstStdDevType;

    /**
     * 目标设备ID
     */
    // @SerializedName(value = "device_id")
    private String dstStdDevId;

    /**
     * 目标设备责任人编号
     */
    // @SerializedName(value = "dst_std_user_no")
    private String dstStdUserNo;
    /**
     * 目标设备责任人类型
     */
    private String dstPersonType;

    /**
     * 目标设备责任人姓名
     */
    // @SerializedName(value = "dst_std_username")
    private String dstStdUserName;

    /**
     * 目标设备责任人部门
     */
    // @SerializedName(value = "dst_std_user_department")
    private String dstStdUserDepartment;

    /**
     * 目标设备责任人角色
     */
    // @SerializedName(value = "dst_std_user_role")
    private String dstStdUserRole;

    /**
     * 目标设备责任人密级
     */
    // @SerializedName(value = "dst_std_user_level")
    private String dstStdUserLevel;

    /**
     * 目标设备责任人岗位
     */
    // @SerializedName(value = "dst_std_user_station")
    private String dstStdUserStation;

    /**
     * 应用系统编号
     */
    // @SerializedName(value = "dst_std_sys_id")
    private String dstStdSysId;

    /**
     * 所在服务器IP
     */
    // @SerializedName(value = "dst_std_sys_ip")
    private String dstStdSysIp;

    /**
     * 应用系统名称
     */
    // @SerializedName(value = "dst_std_sys_name")
    private String dstStdSysName;

    /**
     * 单位编码
     */
    // @SerializedName(value = "dst_std_org_code")
    private String dstStdOrgCode;

    /**
     * 单位名称
     */
    // @SerializedName(value = "dst_std_org_name")
    private String dstStdOrgName;

    /**
     * 单位类别
     */
    // @SerializedName(value = "dst_std_org_type")
    private String dstStdOrgType;

    /**
     * 目标设备文件目录
     */
    @SerializedName(value = "dst_file_dir")
    private String dstFileDir;
}
