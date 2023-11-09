package com.vrv.vap.admin.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author lilang
 * @date 2020/11/13
 * @description
 */
@Component
@ConfigurationProperties(prefix = "webinfo")
public class WebInfoConfig {

    private String systemName;
    private String secmark;
    private String systype;
    private String serviceport;
    private String appSingleLoginPath;

    @NestedConfigurationProperty
    private ServerGLnet serverGLnet = new ServerGLnet();

    @NestedConfigurationProperty
    private ServerBMnet serverBMnet = new ServerBMnet();

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSecmark() {
        return secmark;
    }

    public void setSecmark(String secmark) {
        this.secmark = secmark;
    }

    public String getSystype() {
        return systype;
    }

    public void setSystype(String systype) {
        this.systype = systype;
    }

    public String getServiceport() {
        return serviceport;
    }

    public void setServiceport(String serviceport) {
        this.serviceport = serviceport;
    }

    public String getAppSingleLoginPath() {
        return appSingleLoginPath;
    }

    public void setAppSingleLoginPath(String appSingleLoginPath) {
        this.appSingleLoginPath = appSingleLoginPath;
    }

    public ServerGLnet getServerGLnet() {
        return serverGLnet;
    }

    public void setServerGLnet(ServerGLnet serverGLnet) {
        this.serverGLnet = serverGLnet;
    }

    public ServerBMnet getServerBMnet() {
        return serverBMnet;
    }

    public void setServerBMnet(ServerBMnet serverBMnet) {
        this.serverBMnet = serverBMnet;
    }
}
