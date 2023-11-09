package com.vrv.vap.syslog.service.impl;

import com.vrv.vap.flumeavrostarter.sender.FlumeDataSender;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.AbstractSyslogSender;
import com.vrv.vap.syslog.service.SyslogSender;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author wh1107066
 * @date 2021/7/2 10:44
 */
public class FlumeSyslogSender extends AbstractSyslogSender<SystemLog> implements SyslogSender<SystemLog> {

    @Resource
    private FlumeDataSender flumeDataSender;

    /**
     * 操作审计发送到flume组件， 这个通过AOP切面给予resResult的值，所以不需要自动给
     */
    @Override
    public void messageFlush(Map<String, Object> systemLogMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("调试代码发送操作日志信息:" + gson.toJson(systemLogMap));
        }
        try {
            boolean sendStatus = flumeDataSender.send(systemLogMap);
            if (!sendStatus) {
                logger.error("syslog messageFlush error！" + gson.toJson(systemLogMap));
            }
        } catch (Exception ex) {
            logger.error("发送出现异常!!!", ex);
        }
    }

}
