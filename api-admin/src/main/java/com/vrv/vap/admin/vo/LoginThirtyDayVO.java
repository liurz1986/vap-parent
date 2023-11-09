package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class LoginThirtyDayVO {
    @JsonFormat(pattern= "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;
    private  int loginNum;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(int loginNum) {
        this.loginNum = loginNum;
    }
}
