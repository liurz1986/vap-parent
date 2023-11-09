package com.vrv.vap.toolkit.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * 空值校验
 *
 * @author xw
 * @date 2018年4月19日
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

}
