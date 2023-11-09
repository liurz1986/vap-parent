package com.vrv.logVO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午2:51:23 
* 类说明  类型字段
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDesc {
     
	String value();  //日志描述名称
	String tableName(); //日志表名
	String topicName(); //kafka topicName
}
