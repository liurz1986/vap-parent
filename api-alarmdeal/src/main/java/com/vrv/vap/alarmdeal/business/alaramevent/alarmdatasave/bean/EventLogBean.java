package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 日志字段对应
 *
 * @author 梁国露
 * @date 2021年11月02日 9:24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventLogBean {
    /**
     * IP
     */
    // @SerializedName(value = "std_dev_ip",alternate = {"src_std_dev_ip","sip"})
    private String stdDevIp;

    /**
     * MAC
     */
    // @SerializedName(value = "std_dev_mac",alternate = {"src_std_dev_mac"})
    private String stdDevMac;

    /**
     * 硬盘序列号
     */
    // @SerializedName(value = "std_dev_hardware_identification",alternate = {"src_std_dev_hardware_identification"})
    private String stdDevHardwareIdentification;

    /**
     * 设备品牌型号
     */
    // @SerializedName(value = "std_dev_brand_model",alternate = {"src_std_dev_brand_model"})
    private String stdDevBrandModel;

    /**
     * 硬件设备型号
     */
    // @SerializedName(value = "std_dev_hardware_model",alternate = {"src_std_dev_hardware_model"})
    private String stdDevHardwareModel;

    /**
     * 设备名称
     */
    // @SerializedName(value = "std_dev_name",alternate = {"src_std_dev_name"})
    private String stdDevName;

    /**
     * 设备入网时间
     */
    // @SerializedName(value = "std_dev_net_time",alternate = {"src_std_dev_net_time"})
    private String stdDevNetTime;

    /**
     * 操作系统类型
     */
    // @SerializedName(value = "std_dev_os_type",alternate = {"src_std_dev_os_type"})
    private String stdDevOsType;

    /**
     * 所属安全域
     */
    // @SerializedName(value = "std_dev_safety_marign",alternate = {"src_std_dev_safety_marign"})
    private String stdDevSafetyMarign;

    /**
     * 源设备安全域名称
     */
    // @SerializedName(value = "src_std_dev_safety_marign_name",alternate = {"std_dev_safety_marign_name"})
    private String srcStdDevSafetyMarignName;

    /**
     * 软件系统版本号
     */
    // @SerializedName(value = "std_dev_software_version",alternate = {"src_std_dev_software_version"})
    private String stdDevSoftwareVersion;

    /**
     * 设备密级
     */
    // @SerializedName(value = "std_dev_level",alternate = {"src_std_dev_level"})
    private String stdDevLevel;

    /**
     * 设备二级类型
     */
    // @SerializedName(value = "std_dev_type",alternate = {"src_std_dev_type"})
    private String stdDevType;

    /**
     * 设备ID
     */
    // @SerializedName(value = "std_dev_id",alternate = {"src_std_dev_id"})
    private String stdDevId;

    /**
     * 责任人编号
     */
    // @SerializedName(value = "std_user_no",alternate = {"src_std_user_no"})
    private String stdUserNo;

    /**
     * 责任人姓名
     */
    // @SerializedName(value = "std_username",alternate = {"src_std_username"})
    private String stdUserName;

    /**
     * 责任人部门
     */
    // @SerializedName(value = "std_user_department",alternate = {"src_std_user_department"})
    private String stdUserDepartment;

    /**
     * 责任人角色
     */
    // @SerializedName(value = "std_user_role",alternate = {"src_std_user_role"})
    private String stdUserRole;

    /**
     * 责任人密级
     */
    // @SerializedName(value = "std_user_level",alternate = {"src_std_user_level"})
    private String stdUserLevel;

    /**
     * 责任人类型
     */
    private String stdUserType;

    /**
     * 责任人岗位
     */
    // @SerializedName(value = "std_user_station",alternate = {"src_std_user_station"})
    private String stdUserstation;

    /**
     * 应用系统编号
     */
    // @SerializedName(value = "std_sys_id",alternate = {"src_std_sys_id"})
    private String stdSysId;

    /**
     * 所在服务器IP
     */
    // @SerializedName(value = "std_sys_ip",alternate = {"src_std_sys_ip"})
    private String stdSysIp;

    /**
     * 服务端口
     */
    // @SerializedName(value = "std_sys_port")
    private String stdSysPort;

    /**
     * 应用系统名称
     */
    // @SerializedName(value = "std_sys_name",alternate = {"src_std_sys_name"})
    private String stdSysName;

    /**
     * 通信参数
     */
    // @SerializedName(value = "std_sys_parameter")
    private String stdSysParameter;

    /**
     * 通讯协议名
     */
    // @SerializedName(value = "std_sys_protocal")
    private String stdSysProtocal;

    /**
     * 文件内容md5
     */
    @SerializedName(value = "md5", alternate = {"file_md5"})
    private String fileMd5;

    /**
     * 业务列表
     */
    @SerializedName(value = "business_list")
    private String businessList;

    /**
     * 源文件路径
     */
    @SerializedName(value = "src_file_dir", alternate = {"file_dir"})
    private String srcFileDir;

    /**
     * 源文件名称
     */
    @SerializedName(value = "src_file_name", alternate = {"file_name"})
    private String fileName;

    /**
     * 文件密级
     */
    @SerializedName(value = "file_level", alternate = {"classification_level"})
    private String fileLevel;

    /**
     * 类别
     */
    @SerializedName(value = "file_type")
    private String fileType;

    /**
     * 单位编码
     */
    // @SerializedName(value = "std_org_code",alternate = {"src_std_org_code"})
    private String stdOrgCode;

    /**
     * 单位名称
     */
    // @SerializedName(value = "std_org_name",alternate = {"src_std_org_name"})
    private String stdOrgName;

}
