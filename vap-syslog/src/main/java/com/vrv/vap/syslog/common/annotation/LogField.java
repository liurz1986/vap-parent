package com.vrv.vap.syslog.common.annotation;

import java.lang.annotation.*;

/**
 * @author huipei.x
 * @data 创建时间 2019/8/27
 * @description 类说明 :
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogField {
    /**
     *数据库字段名
     */
    String name() default "";
    /**
     *字段说明
     */
    String description() default "";

    /**
     * 数据脱敏
     */
    boolean desensitization() default false;
}
