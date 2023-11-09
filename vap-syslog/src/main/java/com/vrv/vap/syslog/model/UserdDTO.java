package com.vrv.vap.syslog.model;

import lombok.Data;

import java.util.List;

/**
 * @author huipei.x
 * @data 创建时间 2018/11/14
 * @description 类说明 :
 */
@Data
public class UserdDTO {

    private Integer id;

    /**
     * 用户名称
     */

    private String name;

    /**
     * 用户用到登录的帐号名
     */

    private String account;

    /**
     * 密码
     */
    private String password;


    private List<Integer> roleIds;

    private String idcard;

    private String orgName;

    private int loginType;

    private List<String> roleCode;

    private List<String> roleName;



}
