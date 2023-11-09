package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class LocalSystemInfo {
    private Integer id;
    private Double cpuRate;
    private Double ramRate;
    private Double diskRate;
    private Date createTime;
}