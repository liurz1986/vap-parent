package com.vrv.vap.syslog.exception;

import lombok.Data;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @author: liujinhui
 * @date: 2019/9/16 10:22
 */
@Data
public class FieldValidateException extends RuntimeException {

    private List<FieldError> fieldErrors;

    public FieldValidateException(List<FieldError> fieldErrors) {
        super("服务器异常，请联系管理员!!");
        this.fieldErrors = fieldErrors;
    }

    public FieldValidateException(String message) {
        super(message);
    }

    public FieldValidateException(List<FieldError> fieldErrors, String message) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
}
