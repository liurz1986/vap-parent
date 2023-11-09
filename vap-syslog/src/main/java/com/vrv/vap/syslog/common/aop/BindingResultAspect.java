package com.vrv.vap.syslog.common.aop;

import com.vrv.vap.syslog.exception.FieldValidateException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * 主要是对后台数据校验，自定义异常返回
 * 越小越先执行， SysLogAspect是-5， BindingResultAspect是-4，
 *
 * @author wh1107066
 */
@Aspect
@Component
@Order(-6)
public class BindingResultAspect {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 统一表单验证切面
     *
     * @param bindingResult 表单验证的最后一个参数一定是BindingResult
     */
    @Before("(execution(* com.vrv.vap.*.web..*.*(..)) || execution(* com.vrv.vap.*.controller..*.*(..))) && args(..,bindingResult)")
    public void valid(BindingResult bindingResult) {
        if (bindingResult != null) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder msg = new StringBuilder();
            if (!errors.isEmpty()) {
                for (FieldError error : errors) {
                    msg.append(error.getObjectName() + "." + error.getField());
                    msg.append(error.getDefaultMessage());
                    msg.append(";");
                }
                logger.error("出现异常处理！！" + msg);
                throw new FieldValidateException(errors, msg.toString());
            }
        }
    }
}
