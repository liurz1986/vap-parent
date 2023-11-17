package com.vrv.vap.monitor.agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "base")
public class BaseProperties {
    private String url;
    private String sendFileUrl;
    private Long sendFileMaxSize=104857600L;
    private String localIp;
    private String token;
    private Integer beatInterval;
    private String collect;

    public BaseProperties() {
    }

    public BaseProperties(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSendFileUrl() {
        return sendFileUrl;
    }

    public void setSendFileUrl(String sendFileUrl) {
        this.sendFileUrl = sendFileUrl;
    }

    public Long getSendFileMaxSize() {
        return sendFileMaxSize;
    }

    public void setSendFileMaxSize(Long sendFileMaxSize) {
        this.sendFileMaxSize = sendFileMaxSize;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getBeatInterval() {
        return beatInterval;
    }

    public void setBeatInterval(Integer beatInterval) {
        this.beatInterval = beatInterval;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }
}
