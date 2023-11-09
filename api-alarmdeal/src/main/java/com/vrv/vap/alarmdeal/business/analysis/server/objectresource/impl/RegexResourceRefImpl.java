package com.vrv.vap.alarmdeal.business.analysis.server.objectresource.impl;

import java.util.List;
import java.util.regex.Pattern;

import com.vrv.vap.alarmdeal.business.analysis.server.objectresource.RegxResourceRef;

/**
 * 正则表达式关联实现
 * @author Administrator
 *
 */
public class RegexResourceRefImpl implements RegxResourceRef {

	private List<String> contents;    //配置的一系列的正则表达式的集合
	
	
	public RegexResourceRefImpl(List<String> contents) {
		this.contents = contents;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean computer(Object fieldValue, Boolean opt) {
		boolean result = false;
		if(fieldValue instanceof String) {
			String value = (String)fieldValue; //成员的值
			for (String pattern : contents) {
				boolean matches = Pattern.matches(pattern, value);
				if(opt) { //包含
					if(matches) { //包含且匹配，匹配中其中一个即可
						result = true;
						break; //跳出循环
					}else { //包含不匹配
						result = false;
					}
				}else { //不包含
					if(matches) { //不包含且匹配
						result = false;
						break; //跳出循环
					}else { //不包含不匹配
						result = true;
					}
				}
			}
		}
		return result;
	}

}
