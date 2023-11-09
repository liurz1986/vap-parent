package com.vrv.vap.admin.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vap.guowang")
public class GuoWangProperties {

    private String cmesAuthenticationToken;
    private String vapGwDefaultScreen;
    private String vapGwDefaultAudit;
    private String cemsLoginPage;
    private String defaultUser;
    private String vapToCmesAuthentication;
    private String vapToCmesAuthenticationToken;
    private String cemsHomePage;

}
