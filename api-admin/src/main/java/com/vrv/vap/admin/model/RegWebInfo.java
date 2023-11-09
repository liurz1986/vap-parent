package com.vrv.vap.admin.model;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2021/1/8
 * @description
 */
@Table(name = "reg_web_info")
public class RegWebInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sys_name")
    private String sysName;

    @Column(name = "mark")
    private String mark;

    @Column(name = "gl_net_ip")
    private String glNetIp;

    @Column(name = "gl_net_mac")
    private String glNetMac;

    @Column(name = "bm_net_ip")
    private String bmNetIp;

    @Column(name = "bm_net_mac")
    private String bmNetMac;

    @Column(name = "yw_net_ip")
    private String ywNetIp;

    @Column(name = "yw_net_mac")
    private String ywNetMac;

    @Column(name = "sys_type")
    private String sysType;

    @Column(name = "service_port")
    private String servicePort;

    @Column(name = "login_path")
    private String loginPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getGlNetIp() {
        return glNetIp;
    }

    public void setGlNetIp(String glNetIp) {
        this.glNetIp = glNetIp;
    }

    public String getGlNetMac() {
        return glNetMac;
    }

    public void setGlNetMac(String glNetMac) {
        this.glNetMac = glNetMac;
    }

    public String getBmNetIp() {
        return bmNetIp;
    }

    public void setBmNetIp(String bmNetIp) {
        this.bmNetIp = bmNetIp;
    }

    public String getBmNetMac() {
        return bmNetMac;
    }

    public void setBmNetMac(String bmNetMac) {
        this.bmNetMac = bmNetMac;
    }

    public String getYwNetIp() {
        return ywNetIp;
    }

    public void setYwNetIp(String ywNetIp) {
        this.ywNetIp = ywNetIp;
    }

    public String getYwNetMac() {
        return ywNetMac;
    }

    public void setYwNetMac(String ywNetMac) {
        this.ywNetMac = ywNetMac;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getLoginPath() {
        return loginPath;
    }

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }
}
