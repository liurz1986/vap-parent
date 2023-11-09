package com.vrv.vap.alarmdeal.frameworks.contract.syslog;


public enum ResultEnum {
    /**
     *
     */
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    OK(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    SUCCESS(200,"成功"),
    FAIL(0001,"查询失败"),
    EMPTY_NULL_RESULT(0002,"未查询到数据"),
    EMPTY_FIELD_TABLENAME(0003,"表名为空"),
    NULL(9999,"空"),

    FORM_VALIDATE_ERROR(-2, "表单验证错误"),
    UNKNOW_FAILED(-1, "未知的错误"),
    Unauthorized(403, "未授权的请求");

    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
