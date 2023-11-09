package com.vrv.rule.model.filter;

import lombok.Data;

/**
 * 过滤器对象
 * @author wd-pc
 *
 */
@Data
public class FilterConfigObject {
  
	private Exchanges[][] exchanges; //转换器
	
	private Tables[][] tables; //对应的表名
	
}
