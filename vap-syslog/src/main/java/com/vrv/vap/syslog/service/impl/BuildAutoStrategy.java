package com.vrv.vap.syslog.service.impl;

import com.vrv.vap.syslog.model.SystemLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 自动构建策略模式
 *
 * @author wh1107066
 * @date 2021/7/2 14:50
 */
public class BuildAutoStrategy extends AbstractBuildStrategy {
    @Override
    public Map<String, Object> buildSyslogVO(SystemLog systemLog, String resResult) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("request_ip", systemLog.getRequestIp());
        logMap.put("user_id", systemLog.getUserId());
        logMap.put("user_name", systemLog.getUserName());
        logMap.put("organization_name", systemLog.getOrganizationName());
        logMap.put("request_url", systemLog.getRequestUrl());
        logMap.put("request_method", systemLog.getRequestMethod());
        logMap.put("response_result", systemLog.getResponseResult());
        logMap.put("login_type", systemLog.getLoginType());
        logMap.put("role_name", systemLog.getRoleName());
        logMap.put("extend_fields", systemLog.getExtendFields());
        logMap.put("result", systemLog.getResponseResult());
        // 自动构建id唯一，由aop确定
        logMap.put("id", systemLog.getId());
        extraProperties(systemLog,logMap);
        return logMap;
    }
}
