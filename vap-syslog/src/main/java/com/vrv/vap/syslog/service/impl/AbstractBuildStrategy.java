package com.vrv.vap.syslog.service.impl;

import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.BuildStrategy;

import java.util.Map;

/**
 * @author wh1107066
 * @date 2021/7/2 15:29
 */
public abstract class AbstractBuildStrategy implements BuildStrategy {

    /**
     * 子类实现，构建发送数据格式
     *
     * @param systemLog 操作日志对象
     * @param resResult 结果
     * @return
     */
    @Override
    public abstract Map<String, Object> buildSyslogVO(SystemLog systemLog, String resResult);

    public void extraProperties(SystemLog systemLog, Map<String, Object> logMap) {
        String now = DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS);
        logMap.put("request_time", now);
        logMap.put("type", systemLog.getType());
        logMap.put("description", systemLog.getDescription());
        logMap.put("method_name", systemLog.getMethodName());
        logMap.put("bean_name", systemLog.getBeanName());
        logMap.put("params_value", systemLog.getParamsValue());
        logMap.put("extend_fields", systemLog.getExtendFields());
        logMap.put("operationObject", systemLog.getOperationObject());
        logMap.put("bz1", systemLog.getBz1());
        logMap.put("token", systemLog.getToken());
        logMap.put("taskCode", systemLog.getTaskCode());
        logMap.put("referer", systemLog.getReferer());
        logMap.put("request_page_uri", systemLog.getRequestPageUri());
        logMap.put("request_page_title", systemLog.getRequestPageTitle());
    }
}
