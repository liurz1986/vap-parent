package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

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
