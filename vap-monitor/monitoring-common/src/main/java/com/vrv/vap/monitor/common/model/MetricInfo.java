package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class MetricInfo {
    /**
     * 机器IP
     */
    private String ip;
    /**
     * 组件名称
     */
    private String monitorName;
    /**
     * 时间
     */
    private Date time;
    /**
     * 状态1 正常 0异常 2 部分正常  3 重启中
     */
    private Integer status;

    /**
     *  是否可重启
     */
    private Boolean restart;

    /**
     *  日志是否可下载
     */
    private Boolean log;

    /**
     *  分组
     */
    private String group;

    private String extendContent;
}
