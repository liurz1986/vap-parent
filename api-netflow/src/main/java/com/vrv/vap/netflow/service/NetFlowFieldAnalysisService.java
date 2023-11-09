package com.vrv.vap.netflow.service;

import java.util.Map;

/**
 * 解析流量日志中的字段类型及数据
 * @author wh1107066
 * @date 2023/8/18 11:19
 */
public interface NetFlowFieldAnalysisService {
    void handleSessionId(Map log);

    void handlerApp(Map log);

    void handlerPerson(Map log);

    void handlerDev(Map log);

    void handlerOrg(Map log);

    void swapFieldValues(Map log);

    /**
     * log_type标准化vrv的标准字段，， 流量探针的log_type  转化成  zjg 的标准 log_type 类型
     */
    void standMonitorLogTypeToFlumeLogType(Map<String, Object> logMap);
}
