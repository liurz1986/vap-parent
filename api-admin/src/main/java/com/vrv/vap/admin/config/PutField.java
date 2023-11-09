package com.vrv.vap.admin.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface PutField {
	@AliasFor("value")
	String name() default "";
	@AliasFor("name")
	String value() default "";
}
