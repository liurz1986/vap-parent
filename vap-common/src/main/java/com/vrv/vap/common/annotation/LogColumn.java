package com.vrv.vap.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogColumn {
    String name() default "";

    boolean show() default true;

    MaskType mask() default MaskType.NONE;

    String mapping() default "";

}
