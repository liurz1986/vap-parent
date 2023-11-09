package com.vrv.vap.monitor.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class AgentStateInfo {
    /**
     * IP
     */
    private String ip;

    /**
     * agent配置信息
     */
    private List<MonitorConfig> monitorConfigs;

    /**
     * agent组件状态信息
     */
    private Map<String,MetricInfo> metricInfoMap;

    /**
     * agent组件状态信息
     */
    private Map<String,RestartInfo> restartInfoMap;


    /**
     * 1正常，0下线
     */
    private Integer status;

    /**
     * 最新更新时间
     */
    private Date time;

    /**
     * 最后心跳
     */
    private BeatInfo beatInfo;

    /**
     * 请求Agent URL
     */
    private String url;
}
