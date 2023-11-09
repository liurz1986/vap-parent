package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.AuthLoginField;

import java.util.List;

/**
 * @author lilang
 * @date 2023/1/9
 * @description
 */
public class AuthLoginFieldVO {

    private List<AuthLoginField> loginFieldList;

    private Integer userId;

    private Integer ipLogin;

    public List<AuthLoginField> getLoginFieldList() {
        return loginFieldList;
    }

    public void setLoginFieldList(List<AuthLoginField> loginFieldList) {
        this.loginFieldList = loginFieldList;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getIpLogin() {
        return ipLogin;
    }

    public void setIpLogin(Integer ipLogin) {
        this.ipLogin = ipLogin;
    }
}
