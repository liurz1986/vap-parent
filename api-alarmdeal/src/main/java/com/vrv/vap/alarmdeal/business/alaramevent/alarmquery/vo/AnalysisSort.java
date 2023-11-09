package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import java.util.Comparator;
import java.util.Map;
/**
 * 预警进行排序
 * @author wd-pc
 *
 */
public class AnalysisSort implements Comparator {

	private String key;
	
	public AnalysisSort(String key){
		this.key = key;
	}
	
	
	@Override
	public int compare(Object a1, Object a2) {
		Map<String, Object> map1 = (Map<String, Object>) a1;
		Map<String, Object> map2 = (Map<String, Object>) a2;
		String triggerTime1 = map1.get(key).toString();
		String triggerTime2 = map2.get(key).toString();
		int compareTo = triggerTime1.compareTo(triggerTime2);
		return compareTo;
	}

}
