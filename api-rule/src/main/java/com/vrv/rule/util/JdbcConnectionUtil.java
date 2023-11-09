package com.vrv.rule.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;

import com.alibaba.druid.pool.DruidDataSource;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月1日 下午3:59:24 
* 类说明   
*/
public class JdbcConnectionUtil {
	public static final String event_table_sql = "select id,topic_name from event_table where name= ";
	public static final String event_column_field_sql = "select name,srcIp,dstIp,relateIp from event_column where EventTable = ";
	public static final String event_column_type_sql = "select type from event_column where EventTable = ";
	
	public static final String  tb_conf_sql = "select conf_value from conf_value where  conf_id = 'output_flag'";
	
	private static Logger logger = LoggerFactory.getLogger(JdbcConnectionUtil.class);
	
	public static volatile JdbcTemplate jdbcTemplate;
	
	
	public static void initJdbcTemplate(){
		if (jdbcTemplate == null) {
			synchronized (JdbcTemplate.class) {
				if (null == jdbcTemplate){
					DruidDataSource dataSource = new DruidDataSource();
					// 设置数据源属性参数
					dataSource.setDriverClassName(YmlUtil.getValue("application.yml", "jdbc_driveClass").toString());
					dataSource.setUsername(YmlUtil.getValue("application.yml", "MYSQL_USER").toString());
					dataSource.setPassword(YmlUtil.getValue("application.yml", "MYSQL_PASSWORD").toString());
					String dbUrl = "jdbc:mysql://"+YmlUtil.getValue("application.yml", "MYSQL_HOST")
					+":"+YmlUtil.getValue("application.yml", "MYSQL_PORT")
					+"/"+YmlUtil.getValue("application.yml", "MYSQL_DBNAME").toString()+"?useSSL=false";
					logger.info("dbUrl:"+dbUrl);
					logger.info("password:"+YmlUtil.getValue("application.yml", "MYSQL_PASSWORD").toString());
					dataSource.setUrl(dbUrl);
					// 获取spring的JdbcTemplate
					jdbcTemplate = new JdbcTemplate();
					// 设置数据源
					jdbcTemplate.setDataSource(dataSource);
				}
			}
		}
	}
	
	
	/**
	 * 根据sql进行查询List
	 * @param sql
	 * @return
	 */
	public static List<Map<String,Object>> querySqlForList(String sql){
		initJdbcTemplate();
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
   
	/**
	 * 柑橘sql查询map
	 * @param sql
	 * @return
	 */
	public static Map<String,Object> querySqlForMap(String sql){
		initJdbcTemplate();
		Map<String, Object> map = jdbcTemplate.queryForMap(sql);
		return map;
	}
	

	/**
	 * 获得每个字段的信息
	 * @param tableName
	 * @return
	 */
	public static String[] getColumnName(String tableName){
		initJdbcTemplate();
		String sql = "select * from "+ tableName;
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		jdbcTemplate.query(sql, rcch);
		String[] columnNames = rcch.getColumnNames();
		return columnNames;
	}
	
	
	public static void main(String[] args) {
		Map<String, Object> map = querySqlForMap(" select * from test");
		System.out.println("columnName:"+map);
	}
	
	
	
	public static String[] getColumnType(String tableName) {
		initJdbcTemplate();
		List<String> list  = new ArrayList<>();
		String sql = "select * from "+ tableName;
		RowCountCallbackHandler rcch = new RowCountCallbackHandler();
		jdbcTemplate.query(sql, rcch);
		int[] columnTypes = rcch.getColumnTypes();
		for (int column : columnTypes) {
		    String sqlType = SqlTypeAdapter.getSqlType(column);	
		    list.add(sqlType);
		}
		String[] columnArr=list.toArray(new String[list.size()]);
		return columnArr;
	}
	
	
}
