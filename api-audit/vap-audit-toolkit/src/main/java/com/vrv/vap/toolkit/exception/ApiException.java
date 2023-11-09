package com.vrv.vap.toolkit.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by lizj on 2020/11/24
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

    public ApiException(String msg, Object code) {
        super(msg);
        this.code = code;
    }

    public ApiException(String msg, Object code, Throwable e) {
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

