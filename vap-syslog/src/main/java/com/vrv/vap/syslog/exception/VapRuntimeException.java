package com.vrv.vap.syslog.exception;

/**
 * @author: liujinhui
 * @date: 2020/1/16 9:28
 */
public class VapRuntimeException extends RuntimeException {

    public VapRuntimeException() {
        super();
    }

    public VapRuntimeException(String msg, Exception ex) {
        super(msg, ex);
    }

    public VapRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public VapRuntimeException(String msg) {
        super(msg);
    }
}
