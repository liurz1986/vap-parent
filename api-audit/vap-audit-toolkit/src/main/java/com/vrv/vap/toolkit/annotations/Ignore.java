package com.vrv.vap.toolkit.annotations;

import java.lang.annotation.*;

/**
 * 忽略
 *
 * @author xw
 * @date 2018年5月4日
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {

}
