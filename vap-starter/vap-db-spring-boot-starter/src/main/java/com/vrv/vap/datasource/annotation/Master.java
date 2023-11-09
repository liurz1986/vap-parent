package com.vrv.vap.datasource.annotation;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.lang.annotation.*;

/**
 * 主库数据源
 *
 * @author wh1107066
 * @date 2021/12/11 20:58
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DS("master")
public @interface Master {

}