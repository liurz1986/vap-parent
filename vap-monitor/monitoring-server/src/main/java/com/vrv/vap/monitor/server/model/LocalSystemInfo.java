package com.vrv.vap.monitor.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "local_system_info")
public class LocalSystemInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cpu_rate")
    private Double cpuRate;

    @Column(name = "ram_rate")
    private Double ramRate;

    @Column(name = "disk_rate")
    private Double diskRate;

    @Column(name = "create_time")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}