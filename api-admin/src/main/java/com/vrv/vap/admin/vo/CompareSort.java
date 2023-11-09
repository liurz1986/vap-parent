package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * 预警进行排序
 * @author wd-pc
 *
 */
public class CompareSort implements Comparator {

	private static Logger logger = LoggerFactory.getLogger(CompareSort.class);

	SimpleDateFormat format = new SimpleDateFormat(DateUtil.DEFAULT_YYYYMMDD);
	private String str;
	
	public CompareSort(String str){
		this.str = str;
	}
	
	
	@Override
	public int compare(Object a1, Object a2) {
		Date d1, d2;
		Map<String, Object> map1 = (Map<String, Object>) a1;
		Map<String, Object> map2 = (Map<String, Object>) a2;
		String triggerTime1 = map1.get(str).toString();
		String triggerTime2 = map2.get(str).toString();
		try {
			d1 = format.parse(triggerTime1);
			d2 = format.parse(triggerTime2);
			int compareTo = d1.compareTo(d2);
			return compareTo;
		} catch (ParseException e) {
			logger.error("",e);
		}
		return 0;
	}

}
