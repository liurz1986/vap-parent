package com.vrv.vap.data.common.excel.out;

import java.util.Map;

/**
 * 实现如何处理数据
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
@FunctionalInterface
public interface FieldHandler {
	String[] fix(Map<String, Object> t);
}
