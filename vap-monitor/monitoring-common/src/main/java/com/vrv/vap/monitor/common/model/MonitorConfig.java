package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MonitorConfig {
    /**
     * 组件名称
     */
    private String name;
    /**
     * cron表达式
     */
    private String cron;

    /**
     * 分组信息
     */
    private String module;

    /**
     * 监控类型
     */
    private String monitorType;
    /**
     * 开关
     */
    private Boolean metric;

    /**
     * 告警开关
     */
    private Boolean alarm;

    /**
     * 处理开关
     */
    private Boolean handler;

    /**
     * 是否支持重启
     */
    private Boolean restart;

    /**
     * 是否支持下载日志
     */
    private Boolean log;

    /**
     * 分组（展示）
     */
    private String group;


    /**
     * 监控列表
     */
    private List<String> nodes;

    /**
     * 监控连接信息
     */
    private Map<String, Object> connectConfig;

    /**
     * 组件日志路径
     */
    private String logAddress;

}
