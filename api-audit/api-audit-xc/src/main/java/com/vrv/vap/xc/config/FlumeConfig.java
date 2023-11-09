package com.vrv.vap.xc.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 采集器相关配置
 * Created by lizj on 2020/12/2.
 */
@Component
@ConfigurationProperties(prefix = ConfigPrefix.VAP_FLUME)
public class FlumeConfig {

    private String clientId;

    private String clientSecret;

    private String baseUrl;

    private String listUrl;

    private String startUrl;

    private String stopUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getStopUrl() {
        return stopUrl;
    }

    public void setStopUrl(String stopUrl) {
        this.stopUrl = stopUrl;
    }
}
