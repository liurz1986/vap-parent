package com.vrv.vap.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedirectMapping {

    String value() ;

    String project()  ;

    String selfHeader() default "" ;
}
