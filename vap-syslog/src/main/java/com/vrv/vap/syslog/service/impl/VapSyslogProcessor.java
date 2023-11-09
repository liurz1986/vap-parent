package com.vrv.vap.syslog.service.impl;

import com.vrv.vap.syslog.common.properties.SyslogSenderProperties;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.SyslogSender;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * vap的syslog的AOP的Processor处理器，提提供日志的解析及发送功能
 *
 * @author wh1107066
 * @date 2021/7/2 10:59
 */
@Component
public class VapSyslogProcessor extends AbstractSyslogProcessor<SystemLog> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SyslogSender syslogSender;

    @Autowired
    private SyslogSenderProperties syslogSenderProperties;

    /**
     * 手动方式与自动方式的区别：
     * 手动模式： 自定义注解@SysRequestLog 的 manually= true， 默认是true的情况。 手写 SyslogSenderUtils.sendSyslogManually(systemLog);
     * 自动模式： 不需要手动去写。controller中自动获取并写入，没有特别的详细信息。
     * @param syslog SystemLog对象
     */
    @Override
    void sending(SystemLog syslog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("打印日志[%s]",
                    ReflectionToStringBuilder.toString(syslogSenderProperties, ToStringStyle.MULTI_LINE_STYLE)));
        }
        if (syslog != null) {
            syslogSender.sendSysLog(syslog);
        } else {
            logger.warn("syslog的值为空，不进行记录日志！");
        }
    }
}
