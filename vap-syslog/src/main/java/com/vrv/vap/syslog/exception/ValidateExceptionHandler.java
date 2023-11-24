package com.vrv.vap.syslog.exception;


import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.model.ArgumentInvalidResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: liujinhui
 * @date: 2020/1/15 13:49
 */
@ControllerAdvice
public class ValidateExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(FieldValidateException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public VData handleValidateExceptionException(FieldValidateException ex) {
        List<ArgumentInvalidResult> invalidArguments = getArgumentInvalidResults(ex.getFieldErrors());
        String message = ex.getMessage();
        logger.debug("参数校验异常！！");
        return new VData<>(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), message, invalidArguments);
    }

    /**
     * 验证异常
     *
     * @return VData
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public VData handleMethodArgumentNotValidException(HttpServletRequest req, Exception ex) {
        BindingResult bindingResult = null;
        if (ex instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
        } else if (ex instanceof BindException) {
            bindingResult = ((BindException) ex).getBindingResult();
        } else {
            logger.debug("转化异常");
        }
        StringBuffer errorMesssage = new StringBuffer("服务器内部异常，请联系管理员!");
        List<ArgumentInvalidResult> invalidArguments = getArgumentInvalidResults(bindingResult.getFieldErrors());
        String message = errorMesssage.toString();
        logger.debug("错误消息！！", ex);
        return new VData<List<ArgumentInvalidResult>>(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), message, invalidArguments);
    }

    private List<ArgumentInvalidResult> getArgumentInvalidResults(List<FieldError> fieldErrors) {
        List<ArgumentInvalidResult> invalidArguments = new ArrayList<>();
        //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        for (FieldError error : fieldErrors) {
            ArgumentInvalidResult invalidArgument = new ArgumentInvalidResult();
            invalidArgument.setDefaultMessage(error.getDefaultMessage());
            invalidArgument.setField(error.getField());
            invalidArgument.setRejectedValue(error.getRejectedValue());
            invalidArguments.add(invalidArgument);
        }
        return invalidArguments;
    }


    /**
     * 全局异常
     *
     * @param request
     */
    @ExceptionHandler(value = VapRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public VData handleException(HttpServletRequest request, VapRuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        return new VData(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
    }

    /**
     * AOP syslog全局异常
     */
    @ExceptionHandler(value = VapSyslogException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public VData handleSyslogException(HttpServletRequest request, VapSyslogException ex) {
        return new VData(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public VData handleMethodArgumentNotValidException(Exception ex) {
        String message = "服务器内部异常，请联系管理员!";
        logger.error("错误消息！！", ex);
        return new VData(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), message);
    }
}
