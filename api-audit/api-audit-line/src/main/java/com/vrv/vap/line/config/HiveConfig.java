package com.vrv.vap.line.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by lil on 2021/3/17.
 */
@Component
@ConfigurationProperties(prefix = "hive")
public class HiveConfig {

    private List<Map<String,Object>> hosts;

    private String dataMountDir;

    private String pushUrl;

    private int pushUserId;

    public List<Map<String, Object>> getHosts() {
        return hosts;
    }

    public void setHosts(List<Map<String, Object>> hosts) {
        this.hosts = hosts;
    }

    public String getDataMountDir() {
        return dataMountDir;
    }

    public void setDataMountDir(String dataMountDir) {
        this.dataMountDir = dataMountDir;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public int getPushUserId() {
        return pushUserId;
    }

    public void setPushUserId(int pushUserId) {
        this.pushUserId = pushUserId;
    }
}
