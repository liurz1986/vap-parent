package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao.AlarmDealDao;

@Repository
public class AlarmDealDaoImpl implements AlarmDealDao {

	@Autowired
    protected JdbcTemplate jdbcTemplate;

	private Logger logger = LoggerFactory.getLogger(AlarmDealDaoImpl.class);
	
	
	@Override
	public List<Map<String, Object>> getDealedAlarm(String riskEventName, String nowDay, String beforeMouthDay) {
		List<Object> queryList = new ArrayList<>();
		queryList.add(beforeMouthDay);
		queryList.add(nowDay);
		queryList.add(riskEventName);
		String sql = "select SUBSTRING(alarmdeal.create_time,1,10) as triggerTime,COUNT(distinct alarmdeal.alarm_guid) as doc_count " 
		            + "FROM alarm_deal AS alarmdeal WHERE LEFT (alarmdeal.create_time, 10) BETWEEN ? AND ?"
		            + " AND risk_event_name= ? GROUP BY SUBSTRING(alarmdeal.create_time,1,10)";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,queryList.toArray());
		return list;
	}


	@Override
	public List<Map<String, Object>> getDealitemPeople(String beforesixMouthDays, String nowDays) {
		List<Object> queryList = new ArrayList<>();
		queryList.add(beforesixMouthDays);
		queryList.add(nowDays);
		String sql = "SELECT DISTINCT(alarmitemdeal.item_people_id) as people FROM alarm_deal AS alarmdeal  " 
		            + " INNER JOIN alarm_item_deal AS alarmitemdeal  ON alarmdeal.guid=alarmitemdeal.deal_guid "
				    + " where LEFT (alarmdeal.create_time, 7) BETWEEN ? AND ?";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,queryList.toArray());
		return list;
	}


	@Override
	public List<Map<String, Object>> getDealAlarmCountByStatus(String beforesixMouthDays, String nowDays,
			String peopleId, String status) {
		List<Object> queryList = new ArrayList<>();
		queryList.add(beforesixMouthDays);
		queryList.add(nowDays);
		queryList.add(peopleId);
		String sql = "SELECT LEFT (alarmdeal.create_Time, 7) AS triggerTime," + "COUNT(DISTINCT alarmdeal.guid) as doc_count "
				   + "FROM alarm_deal AS alarmdeal INNER JOIN alarm_item_deal AS alarmitemdeal  ON alarmdeal.guid = alarmitemdeal.deal_guid "
				   + "WHERE LEFT (alarmdeal.create_time, 7) BETWEEN ? and ? and alarmitemdeal.item_people_id=? ";
				   
		if("dealed".equals(status)){
			sql+=" and alarmdeal.deal_status='true' ";
		}else if("dealing".equals(status)){
			sql+=" and alarmdeal.deal_status='dealing' ";
		}	   
		sql+= "group by LEFT (alarmdeal.create_time, 7)";
		logger.info("sql: "+sql);
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,queryList.toArray());
		return list;
	}


	@Override
	public List<Map<String, Object>> getDealedAlarmByUser(String userId, String riskEventName, String nowDay,
			String beforeMouthDay) {
		List<Object> queryList = new ArrayList<>();
		queryList.add(userId);
		queryList.add(beforeMouthDay);
		queryList.add(nowDay);
		queryList.add(riskEventName);
		String sql = "select SUBSTRING(alarmdeal.create_time,1,10) as triggerTime,COUNT(distinct alarmdeal.alarm_guid) as doc_count " 
		            + "FROM alarm_deal AS alarmdeal WHERE create_people_Id = ? AND LEFT (alarmdeal.create_time, 10) BETWEEN ? AND ?"
		            + " AND risk_event_name= ? GROUP BY SUBSTRING(alarmdeal.create_time,1,10)";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,queryList.toArray());
		return list;
	}


	@Override
	public List<Map<String, Object>> getDealGuidByDealPeople(String itemPeopleId) {
		List<Object> queryList = new ArrayList<>();
		queryList.add(itemPeopleId);
		String sql = "SELECT DISTINCT(deal_guid) as dealGuid FROM alarm_item_deal"
				+ " WHERE item_people_id=?";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,queryList.toArray());
		return list;
	}

}
