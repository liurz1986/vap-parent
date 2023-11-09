package com.vrv.vap.syslog.config;

import com.vrv.vap.common.utils.StringUtils;
import com.vrv.vap.syslog.common.enums.SinkType;
import com.vrv.vap.syslog.common.properties.SyslogSenderProperties;
import com.vrv.vap.syslog.service.SyslogSender;
import com.vrv.vap.syslog.service.impl.FlumeSyslogSender;
import com.vrv.vap.syslog.service.impl.KafkaSyslogSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 根据不通的配置生成不同的发送策略，支持kafka的配置发送， 支持flume的配置发送。 默认flume
 * @author wh1107066
 * @date 2021/7/2 10:47
 */
@Configuration
@EnableConfigurationProperties(SyslogSenderProperties.class)
public class SyslogSenderConfig {

    @Autowired
    private SyslogSenderProperties syslogSenderProperties;

    /**
     * @return syslogSender
     * @ConditionalOnProperty(prefix="vap.flume" , name="enable", havingValue = "true", matchIfMissing = true)
     */
    @Bean(name = "flumeSyslogSender")
    @ConditionalOnProperty(prefix = "vap.syslog", name = "sinkType", havingValue = "flume", matchIfMissing = true)
    public SyslogSender flumeSyslogSender() {
        return new FlumeSyslogSender();
    }

    @Bean(name = "kafkaSyslogSender")
    @ConditionalOnProperty(prefix = "vap.syslog", name = "sinkType", havingValue = "kafka")
    public SyslogSender kafkaSyslogSender() {
        if (StringUtils.equalsIgnoreCase(syslogSenderProperties.getSinkType(), SinkType.KAFKA.name())) {
            return new KafkaSyslogSender();
        } else {
            throw new RuntimeException("发送flume逻辑错误，抛出异常!");
        }
    }
}
