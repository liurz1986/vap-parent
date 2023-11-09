package com.vrv.vap.syslog.service.impl;

import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.AbstractSyslogSender;
import com.vrv.vap.syslog.service.SyslogSender;

import java.util.Map;

/**
 * @author wh1107066
 * @date 2022年8月15日11:04:13
 */
public class KafkaSyslogSender extends AbstractSyslogSender<SystemLog> implements SyslogSender<SystemLog> {

    @Override
    public void messageFlush(Map<String, Object> systemLogMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("调试代码发送操作日志信息:" + gson.toJson(systemLogMap));
        }
        try {
            //TODO 未实现的kafka的发送逻辑操作
            logger.info("未实现的kafka的发送逻辑操作,待完善...");
        } catch (Exception ex) {
            logger.error("发送出现异常!!!", ex);
        }
    }


}
