package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao.AlarmWhitelistDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AlarmWhitelistDaoImpl implements AlarmWhitelistDao {

	@Autowired
    protected JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getOneExist() {
		String sql = "select * from alarm_whitelist LIMIT 1";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

}
