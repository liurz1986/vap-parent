package com.vrv.vap.admin.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "licence")
public class Licence {

    private boolean imported = true;

    private boolean enable = true;

    private String validateUrl = "/hardware/?__hide=true#/configurationWizard";

    private String httpLicenseUrl = "";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getValidateUrl() {
        return validateUrl;
    }

    public void setValidateUrl(String validateUrl) {
        this.validateUrl = validateUrl;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public String getHttpLicenseUrl() {
        return httpLicenseUrl;
    }

    public void setHttpLicenseUrl(String httpLicenseUrl) {
        this.httpLicenseUrl = httpLicenseUrl;
    }
}
