package com.vrv.vap.admin.model;

/**
 * @author huipei.x
 * @data 创建时间 2018/8/10
 * @description 类说明 :
 */
public enum ResultEnum {
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    OK(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    ;

    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    private void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }
}
