package com.vrv.vap.common.exception;


import org.springframework.http.HttpStatus;


/**
 * @author wh1107066
 * @date 2021/6/26 23:10
 */
public class ApiException extends RuntimeException {

    private Object code = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

    public ApiException() {
        super();
    }

    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(Throwable throwable) {
        super(throwable);
    }

    public ApiException(String msg,Object code) {
        super(msg);
        this.code = code;
    }

    public ApiException(String msg,Object code, Throwable e) {
        super(msg, e);
        this.code = code;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }
}
