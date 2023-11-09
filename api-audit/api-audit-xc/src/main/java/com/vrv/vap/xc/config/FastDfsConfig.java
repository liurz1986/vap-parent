package com.vrv.vap.xc.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by lil on 2019/7/16.
 */
@Component
@ConfigurationProperties(prefix = ConfigPrefix.VAP_FASTDFS)
public class FastDfsConfig {

    private String isUse;

    private String connectTimeout;

    private String networkTimeout;

    private String charset;

    private String httpPort;

    private String stealToken;

    private String secretKey;

    private String trackerServers;

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(String networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getStealToken() {
        return stealToken;
    }

    public void setStealToken(String stealToken) {
        this.stealToken = stealToken;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTrackerServers() {
        return trackerServers;
    }

    public void setTrackerServers(String trackerServers) {
        this.trackerServers = trackerServers;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }
}
