package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

public class AuthLoginFieldQuery extends Query {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("授权字段值")
    private String authFieldValue;

    @ApiModelProperty("授权字段名称")
    private String fieldName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
