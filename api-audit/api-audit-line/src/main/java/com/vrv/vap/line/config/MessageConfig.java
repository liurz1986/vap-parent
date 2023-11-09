package com.vrv.vap.line.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "message")
public class MessageConfig {
    private String vapSourceKey;

    private String vapBaseLineKey;

    private String baseLineTopic;

    private Map<String,Integer> sourceIdMap;

    public Map<String, Integer> getSourceIdMap() {
        return sourceIdMap == null ? new HashMap<>() : sourceIdMap;
    }

    public void setSourceIdMap(Map<String, Integer> sourceIdMap) {
        this.sourceIdMap = sourceIdMap;
    }

    public String getVapSourceKey() {
        return StringUtils.isNotEmpty(vapSourceKey) ? vapSourceKey : "vap_source_message";
    }

    public String getVapBaseLineKey() {
        return StringUtils.isNotEmpty(vapBaseLineKey) ? vapBaseLineKey : "vap_baseline_message";
    }

    public void setVapBaseLineKey(String vapBaseLineKey) {
        this.vapBaseLineKey = vapBaseLineKey;
    }

    public void setVapSourceKey(String vapSourceKey) {
        this.vapSourceKey = vapSourceKey;
    }

    public String getBaseLineTopic() {
        return StringUtils.isNotEmpty(baseLineTopic) ? baseLineTopic : "vap_event_change_message";
    }

    public void setBaseLineTopic(String baseLineTopic) {
        this.baseLineTopic = baseLineTopic;
    }
}
