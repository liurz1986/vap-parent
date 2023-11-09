package com.vrv.vap.admin.vo;


import lombok.Data;

@Data
public class PwUpdateVO {
    //账户，登录状态下可不填
    private String uuu;
    //新密码
    private String npp;
    //旧密码
    private String opp;
}
