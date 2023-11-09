package com.vrv.vap.admin.dao.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vrv.vap.admin.util.SqlCheckUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vrv.vap.admin.dao.LogStatisticsDao;
import com.vrv.vap.admin.vo.LogStasticsVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月8日 下午4:08:20 
* 类说明 数据运维查询实现类 
*/
/*@Repository
public class LogStatisticsDaoImpl implements LogStatisticsDao {

	private Logger logger = LoggerFactory.getLogger(LogStatisticsDaoImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	@Override
	public List<Map<String, Object>> logCategorySearch(){
        String sql = "SELECT name,count,sum,ycount,ysum FROM\r\n" + 
        		"(SELECT\r\n" + 
        		"        CONCAT_WS(\"=>\",l_s.category_name,l_s.source_company) AS NAME,\r\n" + 
        		"	SUM(l_s.storage_count) AS COUNT,\r\n" + 
        		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS SUM\r\n" + 
        		"FROM log_statistics l_s  \r\n" + 
        		"WHERE l_s.`small_category_tablename` LIKE 'ods_%'\r\n" +
        		" AND l_s.category  IS NOT NULL"+
        		" GROUP BY NAME) AS t_table \r\n" + 
        		"LEFT JOIN \r\n" + 
        		"(SELECT\r\n" + 
        		"        CONCAT_WS(\"=>\",l_s.category_name,l_s.source_company) AS YNAME,	\r\n" + 
        		"	SUM(l_s.storage_count) AS YCOUNT,\r\n" + 
        		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS YSUM\r\n" + 
        		"FROM log_statistics l_s\r\n" + 
        		"WHERE l_s.`small_category_tablename` LIKE 'ods_%'\r\n" + 
        		" AND l_s.category  IS NOT NULL"+
        		" AND TO_DAYS(NOW()) - TO_DAYS(STR_TO_DATE(storage_date, '%Y%m%d')) = 1\r\n" + 
        		"GROUP BY YNAME) AS y_table \r\n" + 
        		"ON t_table.NAME = y_table.YNAME";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	
	@Override
	public List<Map<String, Object>> logCategorySearchByCategory(String categoryName,String snoName) {
        List<Object> list = new ArrayList<>();
		String sql ="SELECT name,count,sum,ycount,ysum FROM\r\n" + 
				"(SELECT\r\n" + 
				"	l_s.area_name AS NAME,\r\n" + 
				"        SUM(l_s.storage_count) AS COUNT,\r\n" + 
				"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS SUM\r\n" + 
				"FROM log_statistics l_s\r\n" + 
				"WHERE\r\n" + 
				"	l_s.category_name = ? AND l_s.source_company = ?\r\n" + 
				"	AND l_s.`small_category_tablename` LIKE 'ods_%'\r\n" + 
				"	AND l_s.sub_category_number IS NOT NULL\r\n" + 
				"	GROUP BY NAME) AS t_table \r\n" + 
				"LEFT JOIN\r\n" + 
				"(SELECT\r\n" + 
				"	l_s.area_name AS YNAME,\r\n" + 
				"        SUM(l_s.storage_count) AS YCOUNT,\r\n" + 
				"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS YSUM\r\n" + 
				"FROM log_statistics l_s \r\n" + 
				"WHERE\r\n" + 
				"	l_s.category_name = ? AND l_s.source_company = ?\r\n" + 
				"	AND TO_DAYS(NOW()) - TO_DAYS(STR_TO_DATE(storage_date, '%Y%m%d')) = 1\r\n" + 
				"	AND l_s.`small_category_tablename` LIKE 'ods_%'\r\n" + 
				"	AND l_s.sub_category_number IS NOT NULL\r\n" + 
				"	GROUP BY YNAME) AS y_table\r\n" + 
				"ON t_table.NAME = y_table.YNAME";
        for (int i = 0; i < 2; i++) {
        	list.add(categoryName);
        	list.add(snoName);
		}
        List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}


	@Override
	public List<Map<String, Object>> logCategorySearchByCategoryAndArea(String categoryName, String areaName,String snoName) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
        	list.add(areaName);
        	list.add(categoryName);
        	list.add(snoName);
		}
        String sql = "SELECT name,count,sum,ycount,ysum FROM	\r\n" + 
        		"(SELECT\r\n" + 
        		"	l_s.sub_category_name AS NAME,\r\n" + 
        		"	SUM(l_s.storage_count) AS COUNT,\r\n" + 
        		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS SUM	\r\n" + 
        		"FROM log_statistics l_s \r\n" + 
        		"WHERE l_s.area_name = ?\r\n" + 
        		"AND l_s.category_name = ?\r\n" + 
        		"AND l_s.source_company = ?\r\n" + 
        		"AND l_s.`small_category_tablename` LIKE 'ods_%'\r\n" + 
        		"AND l_s.sub_category_number IS NOT NULL\r\n" + 
        		" GROUP BY NAME) AS t_table\r\n" + 
        		" LEFT JOIN \r\n" + 
        		"(SELECT\r\n" + 
        		"	l_s.sub_category_name AS YNAME,\r\n" + 
        		"	SUM(l_s.storage_count) AS YCOUNT,\r\n" + 
        		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS YSUM	\r\n" + 
        		"FROM log_statistics l_s \r\n" + 
        		"WHERE l_s.area_name = ?\r\n" + 
        		"AND l_s.category_name = ?\r\n" + 
        		"AND l_s.source_company = ?\r\n" + 
        		"AND l_s.`small_category_tablename` LIKE 'ods_%'\r\n" + 
        		"AND TO_DAYS(NOW()) - TO_DAYS(STR_TO_DATE(storage_date, '%Y%m%d')) = 1\r\n" + 
        		"AND l_s.sub_category_number IS NOT NULL\r\n" + 
        		" GROUP BY YNAME \r\n" + 
        		" )AS y_table\r\n" + 
        		" ON t_table.NAME = y_table.YNAME";
        List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> logCategoryTrend(){
		String sql = "SELECT\r\n" + 
	       		"	storage_date AS NAME,\r\n" + 
	       		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS\r\n" + 
	       		"VALUE\r\n" + 
	       		"\r\n" + 
	       		"FROM\r\n" + 
	       		"	log_statistics AS l_s\r\n" + 
	       		"WHERE\r\n" + 
	       		"	DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(l_s.storage_date)\r\n" + 
	       		"   AND l_s.`small_category_tablename` LIKE 'ods_%' "+
	       		"   GROUP BY\r\n" + 
	       		"	storage_date\r\n" + 
	       		"ORDER BY\r\n" + 
	       		"	storage_date ASC";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> logCategoryTrend(LogStasticsVO logStasticsVO) {
		List<Object> list = new ArrayList<>();
		Map<String,Object> params = new HashMap<>();
		params.put("categoryName", logStasticsVO.getCategory());
		params.put("sourceCompany", logStasticsVO.getSno_name());
		params.put("areaName", logStasticsVO.getArea_name());
		params.put("kValue", logStasticsVO.getKey());
		if (SqlCheckUtil.checkSql(logStasticsVO.getKey())) {
			return new ArrayList<>();
		}
		String sql = "SELECT\r\n" + 
       		"	storage_date AS NAME,\r\n" + 
       		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS\r\n" + 
       		"VALUE\r\n" + 
       		"\r\n" + 
       		"FROM\r\n" + 
       		"	log_statistics AS l_s\r\n" + 
       		"WHERE\r\n" + 
       		"	DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(l_s.storage_date)\r\n" + 
       		"   AND l_s.`small_category_tablename` LIKE 'ods_%'"+
       		"AND l_s."+ logStasticsVO.getField()+" = :kValue\r\n";
		    list.add(logStasticsVO.getKey());
       		if(StringUtils.isNotEmpty(logStasticsVO.getCategory()) && !SqlCheckUtil.checkSql(logStasticsVO.getCategory())){
       			sql+="AND l_s.category_name = :categoryName\r\n";
       			list.add(logStasticsVO.getCategory());
       		}
       		if(StringUtils.isNotEmpty(logStasticsVO.getSno_name()) && !SqlCheckUtil.checkSql(logStasticsVO.getSno_name())){
       			sql+="AND l_s.source_company = :sourceCompany\r\n";
       			list.add(logStasticsVO.getSno_name());
       		}
       		if(StringUtils.isNotEmpty(logStasticsVO.getArea_name()) && !SqlCheckUtil.checkSql(logStasticsVO.getArea_name())){
       			sql+="AND l_s.area_name = :areaName\r\n";
       			list.add(logStasticsVO.getArea_name());
       		}
       		sql+="GROUP BY\r\n" + 
       		"	storage_date\r\n" + 
       		"ORDER BY\r\n" + 
       		"	storage_date ASC";
		List<Map<String,Object>> queryForList = namedParameterJdbcTemplate.queryForList(sql, params);
		return queryForList;
	}


	@Override
	public List<Map<String, Object>> logCatetoryBar(String categoryname,String sourceCompany) {
		List<Object> list = new ArrayList<>();
	    list.add(categoryname);
	    list.add(sourceCompany);
		String sql = "SELECT\r\n" + 
				"	area_name AS NAME,\r\n" + 
				"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS  VALUE\r\n" + 
				"FROM\r\n" + 
				"	log_statistics AS l_s\r\n" + 
				"WHERE\r\n" + 
				"	DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(l_s.storage_date)\r\n" + 
				" AND l_s.category_name = ?\r\n" +
				" AND l_s.source_company = ?\r\n" +
				"GROUP BY\r\n" + 
				"	l_s.area_name";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}
	
	@Override
	public List<Map<String, Object>> logCatetoryAllBar() {
		String sql = "SELECT\r\n" + 
				"	area_name AS NAME,\r\n" + 
				"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS  VALUE\r\n" + 
				"FROM\r\n" + 
				"	log_statistics AS l_s\r\n" + 
				"WHERE\r\n" + 
				"	DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(l_s.storage_date)\r\n" +
				"GROUP BY\r\n" + 
				"	l_s.area_name";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		return queryForList;
	}

	@Override
	public Map<String, Object> logCategorySearchSum() {

        String sql = "SELECT sum(count) as  count ,sum(sum) as sum,sum(ycount) as ycount,sum(ysum) as ysum FROM\r\n" + 
        		"(SELECT\r\n" + 
        		"        CONCAT_WS(\"=>\",l_s.category_name,l_s.source_company) AS NAME,\r\n" + 
        		"	SUM(l_s.storage_count) AS COUNT,\r\n" + 
        		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS SUM\r\n" + 
        		"FROM log_statistics l_s  \r\n" + 
        		"WHERE l_s.`small_category_tablename` LIKE 'ods_%'\r\n" +
        		" AND l_s.category  IS NOT NULL"+
        		" GROUP BY NAME) AS t_table \r\n" + 
        		"LEFT JOIN \r\n" + 
        		"(SELECT\r\n" + 
        		"        CONCAT_WS(\"=>\",l_s.category_name,l_s.source_company) AS YNAME,	\r\n" + 
        		"	SUM(l_s.storage_count) AS YCOUNT,\r\n" + 
        		"	ROUND((SUM(l_s.storage_size)/(1024*1024))) AS YSUM\r\n" + 
        		"FROM log_statistics l_s\r\n" + 
        		"WHERE l_s.`small_category_tablename` LIKE 'ods_%'\r\n" + 
        		" AND l_s.category  IS NOT NULL"+
        		" AND TO_DAYS(NOW()) - TO_DAYS(STR_TO_DATE(storage_date, '%Y%m%d')) = 1\r\n" + 
        		"GROUP BY YNAME) AS y_table \r\n" + 
        		"ON t_table.NAME = y_table.YNAME";
        Map<String,Object> map = jdbcTemplate.queryForMap(sql);
		return map;
	
	}

}*/
