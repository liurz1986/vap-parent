package com.vrv.vap.syslog.service;

import com.vrv.vap.syslog.model.SystemLog;

import java.util.Map;

/**
 * 策略模式下， 操作日志对象构建MAP类型
 *
 * @author wh1107066
 * @date 2021/7/2 14:47
 */
public interface BuildStrategy {
    /**
     * 以不同的策略模式进行map的赋值操作
     * @param systemLog
     * @param resResult
     * @return
     */
    Map<String, Object> buildSyslogVO(SystemLog systemLog, String resResult);
}
