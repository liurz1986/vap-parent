package com.vrv.vap.admin.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求字段注解
 * 
 * 主要用在将Request里面的值直接存到对应的bean里面
 * 值为字段对应在数据库里面的字段名
 * @author xw
 * @date 2015年11月10日
 */
@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertField
{
	public abstract String name() default "";
}
