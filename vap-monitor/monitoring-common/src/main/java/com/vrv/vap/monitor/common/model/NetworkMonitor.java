package com.vrv.vap.monitor.common.model;

import java.util.Date;


public class NetworkMonitor {

    private Integer id;

    private String deviceId;


    private String deviceBelong;


    private String deviceLocation;


    private String deviceSoftVersion;


    private Integer devicePortId;


    private String interfaceIcon;


    private String loginAddress;


    private String loginAccount;


    private String loginPassword;


    private Integer status;


    private Integer dataType;


    private Date reportTime;


    private String deviceSysVersion;


    private String deviceBiosVersion;


    private Integer deviceCpuCore;


    private Integer deviceCpuUsage;


    private Integer deviceMemSize;


    private Integer deviceHdiskSize;


    private String deviceHdiskNum;


    private Integer deviceMemUsage;


    private Integer deviceHdiskUsage;


    private Integer networkMonitorStatus;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getDeviceId() {
        return deviceId;
    }


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
     * 获取设备接口ID
     *
     * @return device_port_id - 设备接口ID
     */
    public Integer getDevicePortId() {
        return devicePortId;
    }

    /**
     * 设置设备接口ID
     *
     * @param devicePortId 设备接口ID
     */
    public void setDevicePortId(Integer devicePortId) {
        this.devicePortId = devicePortId;
    }

    /**
     * 获取接口标识
     *
     * @return interface_icon - 接口标识
     */
    public String getInterfaceIcon() {
        return interfaceIcon;
    }

    /**
     * 设置接口标识
     *
     * @param interfaceIcon 接口标识
     */
    public void setInterfaceIcon(String interfaceIcon) {
        this.interfaceIcon = interfaceIcon;
    }

    /**
     * 获取设备登录地址
     *
     * @return login_address - 设备登录地址
     */
    public String getLoginAddress() {
        return loginAddress;
    }

    /**
     * 设置设备登录地址
     *
     * @param loginAddress 设备登录地址
     */
    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
    }

    /**
     * 获取设备登录账户
     *
     * @return login_account - 设备登录账户
     */
    public String getLoginAccount() {
        return loginAccount;
    }

    /**
     * 设置设备登录账户
     *
     * @param loginAccount 设备登录账户
     */
    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    /**
     * 获取设备登录密码
     *
     * @return login_password - 设备登录密码
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * 设置设备登录密码
     *
     * @param loginPassword 设备登录密码
     */
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    /**
     * 获取状态
     *
     * @return status - 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取数据类型
     *
     * @return data_type - 数据类型
     */
    public Integer getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型
     *
     * @param dataType 数据类型
     */
    public void setDataType(Integer dataType) {
        this.dataType = dataType;
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
     * 获取设备cpu物理核数量
     *
     * @return device_cpu_core - 设备cpu物理核数量
     */
    public Integer getDeviceCpuCore() {
        return deviceCpuCore;
    }

    /**
     * 设置设备cpu物理核数量
     *
     * @param deviceCpuCore 设备cpu物理核数量
     */
    public void setDeviceCpuCore(Integer deviceCpuCore) {
        this.deviceCpuCore = deviceCpuCore;
    }

    /**
     * 获取CPU利用率
     *
     * @return device_cpu_usage - CPU利用率
     */
    public Integer getDeviceCpuUsage() {
        return deviceCpuUsage;
    }

    /**
     * 设置CPU利用率
     *
     * @param deviceCpuUsage CPU利用率
     */
    public void setDeviceCpuUsage(Integer deviceCpuUsage) {
        this.deviceCpuUsage = deviceCpuUsage;
    }

    /**
     * 获取内存大小
     *
     * @return device_mem_size - 内存大小
     */
    public Integer getDeviceMemSize() {
        return deviceMemSize;
    }

    /**
     * 设置内存大小
     *
     * @param deviceMemSize 内存大小
     */
    public void setDeviceMemSize(Integer deviceMemSize) {
        this.deviceMemSize = deviceMemSize;
    }

    /**
     * 获取硬盘大小
     *
     * @return device_hdisk_size - 硬盘大小
     */
    public Integer getDeviceHdiskSize() {
        return deviceHdiskSize;
    }

    /**
     * 设置硬盘大小
     *
     * @param deviceHdiskSize 硬盘大小
     */
    public void setDeviceHdiskSize(Integer deviceHdiskSize) {
        this.deviceHdiskSize = deviceHdiskSize;
    }

    /**
     * 获取硬盘序列号
     *
     * @return device_hdisk_num - 硬盘序列号
     */
    public String getDeviceHdiskNum() {
        return deviceHdiskNum;
    }

    /**
     * 设置硬盘序列号
     *
     * @param deviceHdiskNum 硬盘序列号
     */
    public void setDeviceHdiskNum(String deviceHdiskNum) {
        this.deviceHdiskNum = deviceHdiskNum;
    }

    /**
     * 获取内存利用率
     *
     * @return device_mem_usage - 内存利用率
     */
    public Integer getDeviceMemUsage() {
        return deviceMemUsage;
    }

    /**
     * 设置内存利用率
     *
     * @param deviceMemUsage 内存利用率
     */
    public void setDeviceMemUsage(Integer deviceMemUsage) {
        this.deviceMemUsage = deviceMemUsage;
    }

    /**
     * 获取硬盘使用率
     *
     * @return device_hdisk_usage - 硬盘使用率
     */
    public Integer getDeviceHdiskUsage() {
        return deviceHdiskUsage;
    }

    /**
     * 设置硬盘使用率
     *
     * @param deviceHdiskUsage 硬盘使用率
     */
    public void setDeviceHdiskUsage(Integer deviceHdiskUsage) {
        this.deviceHdiskUsage = deviceHdiskUsage;
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
}