package com.vrv.vap.netflow.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "network_monitor_audited")
public class NetworkMonitorAudited {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备ID
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * 设备所属单位
     */
    @Column(name = "device_belong")
    private String deviceBelong;

    /**
     * 设备部署位置
     */
    @Column(name = "device_location")
    private String deviceLocation;

    /**
     * 设备软件版本号
     */
    @Column(name = "device_soft_version")
    private String deviceSoftVersion;



    /**
     * 上报时间
     */
    @Column(name = "report_time")
    private Date reportTime;

    /**
     * 设备系统版本号
     */
    @Column(name = "device_sys_version")
    private String deviceSysVersion;

    /**
     * 固件版本
     */
    @Column(name = "device_bios_version")
    private String deviceBiosVersion;




    /**
     * 网络状态 0 异常 1正常
     */
    @Column(name = "network_monitor_status")
    private Integer networkMonitorStatus;

    /**
     * 设备配置信息
     */
    @Column(name = "interface_info")
    private String interfaceInfo;

    /**
     * 内存总数
     */
    @Column(name = "mem_total")
    private Integer memTotal;

    /**
     * CPU信息
     */
    @Column(name = "cpu_info")
    private String cpuInfo;

    /**
     * 磁盘信息
     */
    @Column(name = "disk_info")
    private String diskInfo;

    /**
     * 行政区域编码
     */
    @Column(name = "address_code")
    private String addressCode;

    /**
     * 客户单位联系人信息
     */
    private String contact;

    /**
     * 备注信息
     */
    private String memo;

    /**
     * 注册类型：0：手工录入，1：在线注册
     */
    @Column(name = "reg_type")
    private Integer regType;

    public String getApiKey() {
        return apiKey;
    }

    @Column(name = "api_key")
    private String apiKey;

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取设备ID
     *
     * @return device_id - 设备ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备ID
     *
     * @param deviceId 设备ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取设备所属单位
     *
     * @return device_belong - 设备所属单位
     */
    public String getDeviceBelong() {
        return deviceBelong;
    }

    /**
     * 设置设备所属单位
     *
     * @param deviceBelong 设备所属单位
     */
    public void setDeviceBelong(String deviceBelong) {
        this.deviceBelong = deviceBelong;
    }

    /**
     * 获取设备部署位置
     *
     * @return device_location - 设备部署位置
     */
    public String getDeviceLocation() {
        return deviceLocation;
    }

    /**
     * 设置设备部署位置
     *
     * @param deviceLocation 设备部署位置
     */
    public void setDeviceLocation(String deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    /**
     * 获取设备软件版本号
     *
     * @return device_soft_version - 设备软件版本号
     */
    public String getDeviceSoftVersion() {
        return deviceSoftVersion;
    }

    /**
     * 设置设备软件版本号
     *
     * @param deviceSoftVersion 设备软件版本号
     */
    public void setDeviceSoftVersion(String deviceSoftVersion) {
        this.deviceSoftVersion = deviceSoftVersion;
    }



    /**
     * 获取上报时间
     *
     * @return report_time - 上报时间
     */
    public Date getReportTime() {
        return reportTime;
    }

    /**
     * 设置上报时间
     *
     * @param reportTime 上报时间
     */
    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    /**
     * 获取设备系统版本号
     *
     * @return device_sys_version - 设备系统版本号
     */
    public String getDeviceSysVersion() {
        return deviceSysVersion;
    }

    /**
     * 设置设备系统版本号
     *
     * @param deviceSysVersion 设备系统版本号
     */
    public void setDeviceSysVersion(String deviceSysVersion) {
        this.deviceSysVersion = deviceSysVersion;
    }

    /**
     * 获取固件版本
     *
     * @return device_bios_version - 固件版本
     */
    public String getDeviceBiosVersion() {
        return deviceBiosVersion;
    }

    /**
     * 设置固件版本
     *
     * @param deviceBiosVersion 固件版本
     */
    public void setDeviceBiosVersion(String deviceBiosVersion) {
        this.deviceBiosVersion = deviceBiosVersion;
    }


    /**
     * 获取网络状态 0 异常 1正常
     *
     * @return network_monitor_status - 网络状态 0 异常 1正常
     */
    public Integer getNetworkMonitorStatus() {
        return networkMonitorStatus;
    }

    /**
     * 设置网络状态 0 异常 1正常
     *
     * @param networkMonitorStatus 网络状态 0 异常 1正常
     */
    public void setNetworkMonitorStatus(Integer networkMonitorStatus) {
        this.networkMonitorStatus = networkMonitorStatus;
    }

    public String getInterfaceInfo() {
        return interfaceInfo;
    }

    public void setInterfaceInfo(String interfaceInfo) {
        this.interfaceInfo = interfaceInfo;
    }

    public Integer getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(Integer memTotal) {
        this.memTotal = memTotal;
    }

    public String getCpuInfo() {
        return cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public String getDiskInfo() {
        return diskInfo;
    }

    public void setDiskInfo(String diskInfo) {
        this.diskInfo = diskInfo;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getRegType() {
        return regType;
    }

    public void setRegType(Integer regType) {
        this.regType = regType;
    }

}