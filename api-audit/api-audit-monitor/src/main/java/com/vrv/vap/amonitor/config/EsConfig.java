package com.vrv.vap.amonitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ES配置实体类
 *
 * @author lil
 * @date 2019年3月20日
 */
@Component
public class EsConfig {

    @Value("${elk.cluster.name}")
    private String clusterName;
    @Value("${elk.ip}")
    private String ip;
    @Value("${elk.port}")
    private int port;
    @Value("${elk.xpack.security.user}")
    private String user;
    @Value("${elk.xpack.security.password}")
    private String password;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
