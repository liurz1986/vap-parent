package com.vrv.vap.alarmdeal.business.flow.core.dao.impl;


import com.vrv.vap.alarmdeal.business.flow.core.dao.BusinessInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年2月12日 上午11:49:48 
* 类说明 
*/
@Repository
public class BusinessInstanceDaoImpl implements BusinessInstanceDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public List<Map<String, Object>> queryBusinessInstanceStatics() {
		String sql = "select process_def_name as name,COUNT(*) AS count FROM business_intance group by process_def_name";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

}
