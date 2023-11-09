package com.vrv.vap.line.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * flink任务提交配置
 *
 * @author xw
 * @date 2018年6月13日
 */
@Component
@ConfigurationProperties(prefix = "flink")
public class SubmitConfig {

    /**
     * master地址
     */
    private String master;

    /**
     * flink-submit路径
     */
    private String flinkSubmit;

    /**
     * flink应用jar地址
     */
    private String appJar;

    /**
     * ssh的ip地址
     */
    private String sshClientHost;

    /**
     * ssh的端口
     */
    private int sshClientPort;

    /**
     * ssh的用户
     */
    private String sshClientUser;

    /**
     * ssh的密码
     */
    private String sshClientPassword;

    /**
     * 本地账号的私钥路径
     */
    private String publickey;

    /**
     * 依赖的jars
     */
    private String jars;

    /**
     * 数据库驱动
     */
    private String driver;

    /**
     * 提交方式
     */
    private int type;

    /**
     * 正则表达式匹配提交输出流中的host
     */
    private String hostRegexp = "ApplicationMaster host:(.*)";

    /**
     * 正则表达式匹配提交输出流中的port
     */
    private String portRegexp = "ApplicationMaster RPC port:(.*)";

    /**
     * 正则表达式匹配提交输出流中的url
     */
    private String urlRegexp = "tracking URL:(.*)";

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getAppJar() {
        return appJar;
    }

    public void setAppJar(String appJar) {
        this.appJar = appJar;
    }

    public String getSshClientHost() {
        return sshClientHost;
    }

    public void setSshClientHost(String sshClientHost) {
        this.sshClientHost = sshClientHost;
    }

    public int getSshClientPort() {
        return sshClientPort;
    }

    public void setSshClientPort(int sshClientPort) {
        this.sshClientPort = sshClientPort;
    }

    public String getSshClientUser() {
        return sshClientUser;
    }

    public void setSshClientUser(String sshClientUser) {
        this.sshClientUser = sshClientUser;
    }

    public String getSshClientPassword() {
        return sshClientPassword;
    }

    public void setSshClientPassword(String sshClientPassword) {
        this.sshClientPassword = sshClientPassword;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getJars() {
        return jars;
    }

    public void setJars(String jars) {
        this.jars = jars;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHostRegexp() {
        return hostRegexp;
    }

    public void setHostRegexp(String hostRegexp) {
        this.hostRegexp = hostRegexp;
    }

    public String getPortRegexp() {
        return portRegexp;
    }

    public void setPortRegexp(String portRegexp) {
        this.portRegexp = portRegexp;
    }

    public String getUrlRegexp() {
        return urlRegexp;
    }

    public void setUrlRegexp(String urlRegexp) {
        this.urlRegexp = urlRegexp;
    }
}
