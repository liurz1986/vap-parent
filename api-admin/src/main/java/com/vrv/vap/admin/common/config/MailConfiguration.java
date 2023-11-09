package com.vrv.vap.admin.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author huipei.x
 * @data 创建时间 2019/6/18
 * @description 类说明 :
 */
@Configuration
public class MailConfiguration {

    @Value("${mail.host:smtp.163.com}")
    private String host;

    @Value("${mail.port:25}")
    private Integer port;

    @Value("${mail.auth:true}")
    private String auth;

    @Value("${mail.timeout:25000}")
    private Integer timeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
