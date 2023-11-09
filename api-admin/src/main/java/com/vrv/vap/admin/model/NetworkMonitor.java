package com.vrv.vap.admin.model;

import com.vrv.vap.common.annotation.LogColumn;
import com.vrv.vap.syslog.common.annotation.LogField;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.Date;
import javax.persistence.*;

@Table(name = "network_monitor")
public class NetworkMonitor {
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
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 设备所属单位
     */
    @Column(name = "device_belong")
    @ApiModelProperty("设备所属单位")
    private String deviceBelong;

    /**
     * 设备部署位置
     */
    @Column(name = "device_location")
    @ApiModelProperty("设备部署位置")
    private String deviceLocation;

    /**
     * 设备软件版本号
     */
    @Column(name = "device_soft_version")
    @ApiModelProperty("设备软件版本号")
    private String deviceSoftVersion;

    /**
     * 设备接口ID
     */
    @Column(name = "device_port_id")
    @ApiModelProperty("设备接口ID")
    private Integer devicePortId;

    /**
     * 接口标识
     */
    @Column(name = "interface_icon")
    @ApiModelProperty("接口标识")
    private String interfaceIcon;

    /**
     * 设备登录地址
     */
    @Column(name = "login_address")
    @ApiModelProperty("设备登录地址")
    private String loginAddress;

    /**
     * 设备登录账户
     */
    @Column(name = "login_account")
    @ApiModelProperty("设备登录账户")
    private String loginAccount;

    /**
     * 设备登录密码
     */
    @Column(name = "login_password")
    @ApiModelProperty("设备登录密码")
    @LogField(name = "loginPassword", description = "设备登录密码", desensitization = true)
    private String loginPassword;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @Ignore
    private Integer status;

    /**
     * 数据类型
     */
    @Column(name = "data_type")
    @ApiModelProperty("数据类型")
    private Integer dataType;

    /**
     * 上报时间
     */
    @Column(name = "report_time")
    @ApiModelProperty("上报时间")
    private Date reportTime;

    /**
     * 设备系统版本号
     */
    @Column(name = "device_sys_version")
    @ApiModelProperty("设备系统版本号")
    private String deviceSysVersion;

    /**
     * 固件版本
     */
    @Column(name = "device_bios_version")
    @ApiModelProperty("固件版本")
    private String deviceBiosVersion;

    /**
     * 设备cpu物理核数量
     */
    @Column(name = "device_cpu_core")
    @ApiModelProperty("设备cpu物理核数量")
    private Integer deviceCpuCore;

    /**
     * CPU利用率
     */
    @Column(name = "device_cpu_usage")
    @ApiModelProperty("CPU利用率")
    private Integer deviceCpuUsage;

    /**
     * 内存大小
     */
    @Column(name = "device_mem_size")
    @ApiModelProperty("内存大小")
    private Integer deviceMemSize;

    /**
     * 硬盘大小
     */
    @Column(name = "device_hdisk_size")
    @ApiModelProperty("硬盘大小")
    private Integer deviceHdiskSize;

    /**
     * 硬盘序列号
     */
    @Column(name = "device_hdisk_num")
    @ApiModelProperty("硬盘序列号")
    private String deviceHdiskNum;

    /**
     * 内存利用率
     */
    @Column(name = "device_mem_usage")
    @ApiModelProperty("内存利用率")
    private Integer deviceMemUsage;

    /**
     * 硬盘使用率
     */
    @Column(name = "device_hdisk_usage")
    @ApiModelProperty("硬盘使用率")
    private Integer deviceHdiskUsage;

    /**
     * 网络状态 0 异常 1正常
     */
    @Column(name = "network_monitor_status")
    @ApiModelProperty("网络状态")
    @LogColumn(mapping = "{\"0\":\"异常\",\"1\":\"正常\"}")
    private Integer networkMonitorStatus;

    /**
     * 设备配置信息
     */
    @Column(name = "interface_info")
    @ApiModelProperty("设备配置信息")
    private String interfaceInfo;

    /**
     * 内存总数
     */
    @Column(name = "mem_total")
    @ApiModelProperty("内存总数")
    private Integer memTotal;

    /**
     * CPU信息
     */
    @Column(name = "cpu_info")
    @ApiModelProperty("CPU信息")
    private String cpuInfo;

    /**
     * 磁盘信息
     */
    @Column(name = "disk_info")
    @ApiModelProperty("磁盘信息")
    private String diskInfo;

    /**
     * 行政区域编码
     */
    @Column(name = "address_code")
    @ApiModelProperty("行政区域编码")
    private String addressCode;

    /**
     * 客户单位联系人信息
     */
    @ApiModelProperty("客户单位联系人信息")
    private String contact;

    /**
     * 备注信息
     */
    @ApiModelProperty("备注信息")
    private String memo;

    /**
     * 注册类型：0：手工录入，1：在线注册
     */
    @Column(name = "reg_type")
    @ApiModelProperty("注册类型")
    @LogColumn(mapping = "{\"0\":\"手工录入\",\"1\":\"在线注册\"}")
    private Integer regType;


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