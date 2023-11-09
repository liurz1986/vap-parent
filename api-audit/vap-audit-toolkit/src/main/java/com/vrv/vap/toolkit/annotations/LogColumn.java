package com.vrv.vap.toolkit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lizj on 2021/3/10
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogColumn {
    String name() default "";

    boolean show() default true;

    MaskType mask() default MaskType.NONE;

    String mapping() default "";

}

