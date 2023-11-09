package com.vrv.vap.netflow.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author sj
 * @version 1.0
 * @date 2023/10/7 17:20
 */

@Table(name = "network_monitor_current_status")
@Data
public class NetworkMonitorCurrentStatus {

    @Id
    @Column(name = "device_id")
    private String deviceId;

    //ip
    @Column(name = "ip")
    private String ip;

    //部署地址
    @Column(name = "device_location")
    private String deviceLocation;

    //设备状态
    @Column(name = "device_belong")
    private String deviceBelong;
    //设备软件本本
    @Column(name = "device_soft_version")
    private String deviceSoftVersion;
    //cpu使用率
    @Column(name = "device_cpu_uasge")
    private Double deviceCpuUsage;

    //内存使用率
    @Column(name = "device_mem_uasge")
    private Double deviceMemUsage;

    //磁盘使用率
    @Column(name = "device_disk_uasge")
    private Double deviceDiskUsage;

    @Column(name = "device_status")
    private Integer deviceStatus;

    @Column(name = "device_status_description")
    private String deviceStatusDescription;

    /**
     * 操作时间
     */
    @Column(name = "update_time")
    private Date update_time;
}
