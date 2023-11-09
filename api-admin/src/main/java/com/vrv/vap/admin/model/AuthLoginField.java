package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * 授权登陆字段信息表
 */
@Table(name = "user_auth_login_field")
public class AuthLoginField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    /**
     * 授权字段值
     */
    @Column(name = "auth_field_value")
    @ApiModelProperty(value = "授权字段值")
    private String authFieldValue;

    /**
     * 授权字段名称
     */
    @Column(name = "field_name")
    @ApiModelProperty(value = "授权字段名称")
    private String fieldName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAuthFieldValue() {
        return authFieldValue;
    }

    public void setAuthFieldValue(String authFieldValue) {
        this.authFieldValue = authFieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
