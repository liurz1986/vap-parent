package com.vrv.vap.alarmdeal.frameworks.util;

import java.util.Date;

import com.vrv.vap.jpa.common.DateUtil;

public class IndexUtils {

	public static String GetIndexName(String  indexName,Date time)
	{
		return  indexName + "-" + GetIndexNameKey(time);
	}
	
	
	public static String GetIndexNameKey(Date time)
	{
		return    DateUtil.format(time, "yyyy");
	}
}
