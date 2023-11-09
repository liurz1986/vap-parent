package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import java.util.List;
import java.util.Map;

public interface AppSystemService {
	/**
	 * 30天内访问前5名
	 * @return
	 */
	public List<Map<String,Object>> getAppVisitTop(String timeType);
	
	/**
	 * 取某个应用系统访问来源排名
	 * @param app_id
	 * @return
	 */
	public List<Map<String,Object>> getAppVisitTop(String app_id,String timeType);
	
	/**
	 * 访问设备数
	 * @param appId
	 * @return
	 */
	public  int getUV(String appId,String timeType);
	
	
	/**
	 * 访问次数
	 * @param appId
	 * @return
	 */
	public  Long getPV(String appId,String timeType);
	
	
	/**
	 * 访问来源ip top10
	 * @param appId
	 * @return
	 */
	
	public List<Map<String,Object>>  getVisitTop10BySrcIP(String appId,String timeType);
	
	public List<Map<String,Object>>  getVisitTop10ByHH(String appId,String srcIp,String timeType);
}
