package com.vrv.vap.admin.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2021/8/10
 * @description 监测器状态信息
 */
@Table(name = "network_monitor_status")
public class NetworkMonitorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_sys_version")
    private String deviceSysVersion;

    @Column(name = "device_soft_version")
    private String deviceSoftVersion;

    @Column(name = "device_bios_version")
    private String deviceBiosVerion;

    @Column(name = "device_cpu_core")
    private Integer deviceCpuCore;

    @Column(name = "device_cpu_usage")
    private Integer deviceCpuUsage;

    @Column(name = "device_mem_size")
    private Integer deviceMemSize;

    @Column(name = "device_hdisk_size")
    private Integer deviceHdiskSize;

    @Column(name = "device_hdisk_num")
    private String deviceHdiskNum;

    @Column(name = "device_mem_usage")
    private Integer deviceMemUsage;

    @Column(name = "device_hdisk_usage")
    private Integer deviceHdiskUsage;

    @Column(name = "report_time")
    private Date reportTime;

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

    public String getDeviceSysVersion() {
        return deviceSysVersion;
    }

    public void setDeviceSysVersion(String deviceSysVersion) {
        this.deviceSysVersion = deviceSysVersion;
    }

    public String getDeviceSoftVersion() {
        return deviceSoftVersion;
    }

    public void setDeviceSoftVersion(String deviceSoftVersion) {
        this.deviceSoftVersion = deviceSoftVersion;
    }

    public String getDeviceBiosVerion() {
        return deviceBiosVerion;
    }

    public void setDeviceBiosVerion(String deviceBiosVerion) {
        this.deviceBiosVerion = deviceBiosVerion;
    }

    public Integer getDeviceCpuCore() {
        return deviceCpuCore;
    }

    public void setDeviceCpuCore(Integer deviceCpuCore) {
        this.deviceCpuCore = deviceCpuCore;
    }

    public Integer getDeviceCpuUsage() {
        return deviceCpuUsage;
    }

    public void setDeviceCpuUsage(Integer deviceCpuUsage) {
        this.deviceCpuUsage = deviceCpuUsage;
    }

    public Integer getDeviceMemSize() {
        return deviceMemSize;
    }

    public void setDeviceMemSize(Integer deviceMemSize) {
        this.deviceMemSize = deviceMemSize;
    }

    public Integer getDeviceHdiskSize() {
        return deviceHdiskSize;
    }

    public void setDeviceHdiskSize(Integer deviceHdiskSize) {
        this.deviceHdiskSize = deviceHdiskSize;
    }

    public String getDeviceHdiskNum() {
        return deviceHdiskNum;
    }

    public void setDeviceHdiskNum(String deviceHdiskNum) {
        this.deviceHdiskNum = deviceHdiskNum;
    }

    public Integer getDeviceMemUsage() {
        return deviceMemUsage;
    }

    public void setDeviceMemUsage(Integer deviceMemUsage) {
        this.deviceMemUsage = deviceMemUsage;
    }

    public Integer getDeviceHdiskUsage() {
        return deviceHdiskUsage;
    }

    public void setDeviceHdiskUsage(Integer deviceHdiskUsage) {
        this.deviceHdiskUsage = deviceHdiskUsage;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }
}
