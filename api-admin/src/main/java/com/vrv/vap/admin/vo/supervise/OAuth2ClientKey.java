package com.vrv.vap.admin.vo.supervise;

public class OAuth2ClientKey {
    String clientId;
    String clientSecret;

    public OAuth2ClientKey(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public OAuth2ClientKey() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
