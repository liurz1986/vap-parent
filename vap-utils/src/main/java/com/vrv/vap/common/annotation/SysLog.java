package com.vrv.vap.common.annotation;

import java.lang.annotation.*;

/**
 * Created by ${huipei.x} on 2018-3-26.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String value() default "";
}
