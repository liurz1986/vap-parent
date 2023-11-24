package com.vrv.vap.syslog.exception;

/**
 * @author: liujinhui
 * @date: 2020/1/16 9:28
 */
public class VapSyslogException extends RuntimeException {

    public VapSyslogException() {
        super();
    }

    public VapSyslogException(String msg, Exception ex) {
        super(msg, ex);
    }

    public VapSyslogException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public VapSyslogException(String msg) {
        super(msg);
    }
}
