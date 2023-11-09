package com.vrv.vap.xc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {

    private String host;

    private int port;

    private String user;

    private String password;

    private boolean withoutLogin;

    private String baseHome;

    private String addTopicCmd;

    private String delTopicCmd;

    private String addAclCmd;

    private String delAclCmd;

    private String addProAclCmd;

    private String delProAclCmd;

    private String describeGroupCmd;

    private String defaultUser;

    private String defaultProUser;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public boolean isWithoutLogin() {
        return withoutLogin;
    }

    public void setWithoutLogin(boolean withoutLogin) {
        this.withoutLogin = withoutLogin;
    }

    public String getBaseHome() {
        return baseHome;
    }

    public void setBaseHome(String baseHome) {
        this.baseHome = baseHome;
    }

    public String getAddTopicCmd() {
        return addTopicCmd;
    }

    public void setAddTopicCmd(String addTopicCmd) {
        this.addTopicCmd = addTopicCmd;
    }

    public String getDelTopicCmd() {
        return delTopicCmd;
    }

    public void setDelTopicCmd(String delTopicCmd) {
        this.delTopicCmd = delTopicCmd;
    }

    public String getAddAclCmd() {
        return addAclCmd;
    }

    public void setAddAclCmd(String addAclCmd) {
        this.addAclCmd = addAclCmd;
    }

    public String getDelAclCmd() {
        return delAclCmd;
    }

    public void setDelAclCmd(String delAclCmd) {
        this.delAclCmd = delAclCmd;
    }

    public String getDescribeGroupCmd() {
        return describeGroupCmd;
    }

    public void setDescribeGroupCmd(String describeGroupCmd) {
        this.describeGroupCmd = describeGroupCmd;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public String getAddProAclCmd() {
        return addProAclCmd;
    }

    public void setAddProAclCmd(String addProAclCmd) {
        this.addProAclCmd = addProAclCmd;
    }

    public String getDelProAclCmd() {
        return delProAclCmd;
    }

    public void setDelProAclCmd(String delProAclCmd) {
        this.delProAclCmd = delProAclCmd;
    }

    public String getDefaultProUser() {
        return defaultProUser;
    }

    public void setDefaultProUser(String defaultProUser) {
        this.defaultProUser = defaultProUser;
    }
}
