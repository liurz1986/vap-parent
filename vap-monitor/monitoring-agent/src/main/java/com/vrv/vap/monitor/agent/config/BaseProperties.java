package com.vrv.vap.monitor.agent.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "base")
@Data
@Builder
public class BaseProperties {
    private String url;
    private String sendFileUrl;
    private Long sendFileMaxSize=104857600L;
    private String localIp;
    private String token;
    private Integer beatInterval;
    private String collect;
}
