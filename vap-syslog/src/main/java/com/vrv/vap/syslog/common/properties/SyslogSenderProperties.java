package com.vrv.vap.syslog.common.properties;

import com.vrv.vap.syslog.common.enums.SinkType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wh1107066
 * @date 2021/7/13 19:14
 */
@ConfigurationProperties(prefix = "vap.syslog")
public class SyslogSenderProperties {

    /**
     * sinkType类型： flume, kafka
     * 方式1：默认以flume的方式进行发送（默认）
     * 方式2：以kafka的方式进行发送
     */
    private String sinkType = SinkType.FLUME.name();


    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

}
