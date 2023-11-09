package com.vrv.vap.admin.vo;

/**
 * Created by Administrator on 2017/2/9.
 */
public class PkiLoginInfoVO {
    private String authURL;
    private String authMode;
    private String token;
    private String qrcode;
    private String original_data;
    private String signed_data;
    private String original_jsp;
    private String remoteAddr;

    public String getAuthURL() {
        return authURL;
    }

    public void setAuthURL(String authURL) {
        this.authURL = authURL;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getOriginal_data() {
        return original_data;
    }

    public void setOriginal_data(String original_data) {
        this.original_data = original_data;
    }

    public String getSigned_data() {
        return signed_data;
    }

    public void setSigned_data(String signed_data) {
        this.signed_data = signed_data;
    }

    public String getOriginal_jsp() {
        return original_jsp;
    }

    public void setOriginal_jsp(String original_jsp) {
        this.original_jsp = original_jsp;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
}
