package com.vrv.vap.admin.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2021/8/10
 * @description 监测器上报基础信息
 */
@Table(name = "network_monitor_report")
public class NetworkMonitorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_belong")
    private String deviceBelong;

    @Column(name = "device_location")
    private String deviceLocation;

    @Column(name = "device_soft_version")
    private String deviceSoftVersion;

    @Column(name = "device_port_id")
    private Integer devicePortId;

    @Column(name = "interface_icon")
    private String interfaceIcon;

    @Column(name = "data_type")
    private Integer dataType;

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

    public String getDeviceBelong() {
        return deviceBelong;
    }

    public void setDeviceBelong(String deviceBelong) {
        this.deviceBelong = deviceBelong;
    }

    public String getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(String deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    public String getDeviceSoftVersion() {
        return deviceSoftVersion;
    }

    public void setDeviceSoftVersion(String deviceSoftVersion) {
        this.deviceSoftVersion = deviceSoftVersion;
    }

    public Integer getDevicePortId() {
        return devicePortId;
    }

    public void setDevicePortId(Integer devicePortId) {
        this.devicePortId = devicePortId;
    }

    public String getInterfaceIcon() {
        return interfaceIcon;
    }

    public void setInterfaceIcon(String interfaceIcon) {
        this.interfaceIcon = interfaceIcon;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }
}
