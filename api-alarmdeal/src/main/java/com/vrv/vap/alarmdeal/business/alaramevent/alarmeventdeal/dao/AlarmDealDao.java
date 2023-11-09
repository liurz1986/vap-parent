package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao;

import java.util.List;
import java.util.Map;

public interface AlarmDealDao {
	/**
	 * 已处置的告警
	 * @param riskEventName
	 * @param nowDay
	 * @param beforeMouthDay
	 * @return
	 */
	public List<Map<String, Object>> getDealedAlarm(String riskEventName, String nowDay, String beforeMouthDay);
	
	
	/**
	 * 根据用户权限进行筛选
	 * @param userId
	 * @param riskEventName
	 * @param nowDay
	 * @param beforeMouthDay
	 * @return
	 */
	public List<Map<String, Object>> getDealedAlarmByUser(String userId, String riskEventName, String nowDay, String beforeMouthDay);
	
	
	/**
	 * 获得对应的人员
	 * @param beforesixMouthDays
	 * @param nowDays
	 * @return
	 */
	public List<Map<String,Object>> getDealitemPeople(String beforesixMouthDays,String nowDays);
	
	
	/**
	 * 根据不同的状态获得不同状态的告警
	 */
	public List<Map<String, Object>> getDealAlarmCountByStatus(String beforesixMouthDays, String nowDays, String peopleId,String status);


	/**
	 * 根据处置人查询处置记录
	 * @param itemPeopleId
	 * @return
	 */
	public List<Map<String, Object>> getDealGuidByDealPeople(String itemPeopleId);
	
	
}
