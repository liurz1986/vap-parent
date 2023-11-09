package com.vrv.vap.monitor.server.config;

import com.vrv.vap.monitor.common.model.MonitorConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "vap.common.monitor")
@Data
public class MonitorProperties {
    private String localIp;
    private Boolean open;
    private Integer reportInterval;
    private String backupFilePath;
    private String logFilePath;
    private Integer offlineInterval;
    private Integer maxRestartWait;
    private Map<String, MonitorConfig> components;
}
