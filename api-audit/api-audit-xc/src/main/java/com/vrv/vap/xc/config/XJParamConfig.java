package com.vrv.vap.xc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 新疆相关参数
 */
@Component
@ConfigurationProperties(prefix = "xj")
public class XJParamConfig {

    /**
     * 设备快过期期限(7,30)
     */
    private String alarmDeadline;
    /**
     * 设备快过期告警短信
     */
    private String alarmContent;
    /**
     * pki同步url
     */
    private String pkiUrl;
    /**
     * pki同步应用标识
     */
    private String pkiSyscode;
    /**
     * pki同步每页行数
     */
    private int pkiRows;

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public String getPkiUrl() {
        return pkiUrl;
    }

    public void setPkiUrl(String pkiUrl) {
        this.pkiUrl = pkiUrl;
    }

    public String getPkiSyscode() {
        return pkiSyscode;
    }

    public void setPkiSyscode(String pkiSyscode) {
        this.pkiSyscode = pkiSyscode;
    }

    public int getPkiRows() {
        return pkiRows;
    }

    public void setPkiRows(int pkiRows) {
        this.pkiRows = pkiRows;
    }

    public String getAlarmDeadline() {
        return alarmDeadline;
    }

    public void setAlarmDeadline(String alarmDeadline) {
        this.alarmDeadline = alarmDeadline;
    }
}
