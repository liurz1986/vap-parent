package com.vrv.vap.common.exception;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vrv.vap.common.vo.VData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author wh1107066
 * @date 2021/6/26 23:10
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

    private final static String MESSAGE = "系统繁忙，请稍后再试";

    private final Logger logger = LogManager.getLogger(this.getClass());

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public VData handleValidateExceptionException(InvalidFormatException ex) {
        logger.error("传入的参数类型不匹配，绑定异常！！", ex);
        return new VData(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "传入的参数类型不匹配，绑定异常！");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object messageNotReadable(HttpMessageNotReadableException exception) {
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.BAD_REQUEST));
        result.put("message", MESSAGE);
        result.put("reason", exception.getMessage());
        return result;
    }


    @ExceptionHandler(ParamValidException.class)
    public Object paramValidException(ParamValidException exception) {
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        result.put("message", MESSAGE);
        result.put("reason", exception.getMessage());
        return result;
    }

    @ExceptionHandler(ApiException.class)
    public Object apiAxception(ApiException exception) {
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        result.put("message", MESSAGE);
        result.put("reason", exception.getMessage());
        return result;
    }
}
