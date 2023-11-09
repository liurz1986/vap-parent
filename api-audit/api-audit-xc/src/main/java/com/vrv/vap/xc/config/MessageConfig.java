package com.vrv.vap.xc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "message")
public class MessageConfig {
    private String vapSourceKey;

    private String vapBaseLineKey;

    private String baseLineTopic;

    private Map<String,Integer> sourceIdMap;

    public String getVapBaseLineKey() {
        return vapBaseLineKey;
    }

    public void setVapBaseLineKey(String vapBaseLineKey) {
        this.vapBaseLineKey = vapBaseLineKey;
    }

    public Map<String, Integer> getSourceIdMap() {
        return sourceIdMap;
    }

    public void setSourceIdMap(Map<String, Integer> sourceIdMap) {
        this.sourceIdMap = sourceIdMap;
    }

    public String getVapSourceKey() {
        return vapSourceKey;
    }

    public void setVapSourceKey(String vapSourceKey) {
        this.vapSourceKey = vapSourceKey;
    }

    public String getBaseLineTopic() {
        return baseLineTopic;
    }

    public void setBaseLineTopic(String baseLineTopic) {
        this.baseLineTopic = baseLineTopic;
    }
}
