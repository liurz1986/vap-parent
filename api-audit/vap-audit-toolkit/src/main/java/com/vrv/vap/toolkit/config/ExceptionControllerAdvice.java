package com.vrv.vap.toolkit.config;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.exception.ApiException;
import com.vrv.vap.toolkit.exception.ParamValidException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by lizj on 2020/11/24
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final Log log = LogFactory.getLog(ExceptionControllerAdvice.class);

    private static final String MESSAGE = "系统繁忙，请稍后再试";

    private static final String REASON = "服务器内部异常，请联系管理员!!";

    public ExceptionControllerAdvice() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Object messageNotReadable(HttpMessageNotReadableException exception) {
        log.error(exception);
        exception.printStackTrace();
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.BAD_REQUEST));
        result.put("message", MESSAGE);
        result.put("reason", REASON);
        return result;
    }

    @ExceptionHandler({ParamValidException.class})
    public Object paramValidException(ParamValidException exception) {
        log.error(exception);
        exception.printStackTrace();
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        result.put("message", MESSAGE);
        result.put("reason", REASON);
        return result;
    }

    @ExceptionHandler({ApiException.class})
    public Object apiAxception(ApiException exception) {
        log.error(exception);
        exception.printStackTrace();
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        result.put("message", MESSAGE);
        result.put("reason", REASON);
        return result;
    }

    @ExceptionHandler({BadSqlGrammarException.class})
    public Object badSqlAxception(BadSqlGrammarException exception) {
        log.error(exception);
        exception.printStackTrace();
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        result.put("message", MESSAGE);
        result.put("reason", "sql脚本执行异常");
        return result;
    }

    @ExceptionHandler({Exception.class})
    public Object defaultErrorHandler(Exception e) {
        log.error(e);
        e.printStackTrace();
        JSONObject result = new JSONObject();
        result.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
        result.put("message", MESSAGE);
        result.put("reason", REASON);
        return result;
    }
}
