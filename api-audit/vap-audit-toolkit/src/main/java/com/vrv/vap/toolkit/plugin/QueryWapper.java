package com.vrv.vap.toolkit.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.*;
/**
 * Created by lizj on 2021/5/13
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryWapper {

    QueryWapperEnum queryWapperEnum() default QueryWapperEnum.EQ ;
}