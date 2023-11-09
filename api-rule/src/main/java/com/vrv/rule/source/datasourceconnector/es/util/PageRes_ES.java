package com.vrv.rule.source.datasourceconnector.es.util;

import lombok.Data;

import java.util.List;

/**
* @author wudi
* @version 创建时间：2018年7月28日 下午3:49:48
* @ClassName PageRes
* @Description 返回分页数据
*/
@Data
public class PageRes_ES<T> {
	private String code;
	private List<T> list;
	private Long total;
	private String message;
	
}
