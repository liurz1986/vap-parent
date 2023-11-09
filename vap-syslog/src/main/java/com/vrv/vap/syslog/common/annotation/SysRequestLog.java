package com.vrv.vap.syslog.common.annotation;

import com.vrv.vap.syslog.common.enums.ActionType;

import java.lang.annotation.*;

/**
 * @author huipei.x
 * @data 创建时间 2018/9/4
 * @description 类说明 :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface SysRequestLog {
    /**
     *方法说明
     */
    String description() default "";

    /**
     * "登录" 0,"查询"1,"新增"2"修改"3,"删除"4
     */
    ActionType actionType() default ActionType.AUTO;

    /**
     * 扩展AOP是否是自动的进行拦截请求并发送。 例如不需要手动发送,  系统自动发送
     * @return  默认是手动的发送日志
     */
    boolean manually() default true;
}
