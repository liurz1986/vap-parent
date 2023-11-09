package com.vrv.rule.source.datasourceconnector.es.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 游标分页Map<String,Object>类型VO
 * @author Administrator
 *
 */
@Data
public class ScrollMapVO {
	private List<Map<String,Object>> list;
	private String scrollId;
	private long total;
}
