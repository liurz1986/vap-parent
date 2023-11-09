package com.vrv.vap.admin.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "site")
public class Site {

    // PKI登录地址
//    private String pkiUrl = "https://192.168.118.127:444/pki";
    // 机构根节点的CODE
    public String orgRoot = "010000000000";
    // 版本
    private String version = "gab";
    // 发布时间
    private String release = "lasted";
    // 是否启动消息通知
    private boolean message = true;
    // 默认皮肤
    private String theme = "default";
    // 默认标题
    private String title = "安全监测大数据平台";
    // 默认副标题
    private String subTitle = "SECURITY MONITORING BIG DATA PLATFORM";

    // 默认密码
    private String pwdDefault = "f8705cb0cb84bd6277c1499d0eae4d74ae2db39c2af9851bdfe4f12206d61ba5";

    private List<Service> services = new ArrayList<>();

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getOrgRoot() {
        return orgRoot;
    }

    public void setOrgRoot(String orgRoot) {
        this.orgRoot = orgRoot;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public boolean getMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPwdDefault() {
        return pwdDefault;
    }

    public void setPwdDefault(String pwdDefault) {
        this.pwdDefault = pwdDefault;
    }
}
