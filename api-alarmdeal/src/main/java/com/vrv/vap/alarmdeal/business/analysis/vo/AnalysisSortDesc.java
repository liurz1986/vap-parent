package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.Comparator;
import java.util.Map;
/**
 * 预警进行排序
 * @author wd-pc
 *
 */
public class AnalysisSortDesc implements Comparator {

	private String sort;
	
	public AnalysisSortDesc(String sort){
		this.sort = sort;
	}
	
	
	@Override
	public int compare(Object a1, Object a2) {
		Map<String, Object> map1 = (Map<String, Object>) a1;
		Map<String, Object> map2 = (Map<String, Object>) a2;
		Long triggerTime1 = (Long)map1.get(sort);
		Long triggerTime2 = (Long)map2.get(sort);
		int compareTo = triggerTime2.compareTo(triggerTime1);
		return compareTo;
	}

}
