package com.vrv.rule.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.vrv.rule.model.EventColumn;
import com.vrv.rule.model.EventTable;
import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.model.FlinkRunningTimeErrorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月1日 下午3:59:24 
* 类说明      单例Jdbc
*/
public class JdbcSingeConnectionUtil {
	public static final String event_table_sql = "select id,topic_name from event_table where name= ";
	public static final String event_column_field_sql = "select name,srcIp,dstIp,relateIp from event_column where EventTable = ";
	public static final String event_column_type_sql = "select type from event_column where EventTable = ";
	private static Logger logger = LoggerFactory.getLogger(JdbcSingeConnectionUtil.class);
	
	public volatile static  JdbcTemplate jdbcTemplate;
	
	private volatile static JdbcSingeConnectionUtil jdbcConnectionUtil;
	
	private JdbcSingeConnectionUtil() { //私有构造函数
		
	}
	
	public static JdbcSingeConnectionUtil getInstance(){
		if(jdbcTemplate==null){
			synchronized (JdbcSingeConnectionUtil.class) {
				if(jdbcTemplate==null){
					jdbcConnectionUtil = new JdbcSingeConnectionUtil();
					initJdbcTemplate();
				}
			}
		}
		return jdbcConnectionUtil;
	}
	
	
	
	public static void initJdbcTemplate(){
		if (jdbcTemplate == null) {
			DruidDataSource dataSource = new DruidDataSource();
			// 设置数据源属性参数
			dataSource.setDriverClassName(YmlUtil.getValue("application.yml", "jdbc_driveClass").toString());
			dataSource.setUsername(YmlUtil.getValue("application.yml", "MYSQL_USER").toString());
			String decryptPassword = JasyptUtil.decryptPassword("MYSQL_PASSWORD");
			dataSource.setPassword(decryptPassword);
			String dbUrl = "jdbc:mysql://"+YmlUtil.getValue("application.yml", "MYSQL_HOST")+
					":"+YmlUtil.getValue("application.yml", "MYSQL_PORT")
					+"/"+YmlUtil.getValue("application.yml", "MYSQL_DBNAME").toString()+"?useSSL=false";;
			logger.info("dbUrl:"+dbUrl);
			logger.info("password:"+decryptPassword);
			dataSource.setUrl(dbUrl);
			dataSource.setMaxActive(10);
			dataSource.setInitialSize(1);
			dataSource.setMinIdle(1);
			dataSource.setKeepAlive(true);
			// 获取spring的JdbcTemplate
			jdbcTemplate = new JdbcTemplate();
			// 设置数据源
			jdbcTemplate.setDataSource(dataSource);
		}
	}
	
	/**
	 * 查询sql
	 * @param sql
	 * @param params
	 * @return
	 */
	public  Map<String,Object> querySqlForMap(String sql,List<String> params){
		Map<String, Object> map = jdbcTemplate.queryForMap(sql, params.toArray());
		return map;
	}
	
	
	/**
	 * 根据sql进行查询List
	 * @param sql
	 * @return
	 */
	public  List<Map<String,Object>> querySqlForList(String sql){
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	
	/**
	 * 参数化查询List
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> querySqlForList(String sql,List<String> params){
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql, params.toArray());
		return list;
	}
	
   
	public List<EventTable> querySqlForEventtable(String sql,List<String> params){
		List<EventTable> list = jdbcTemplate.query(sql, new EventTableMapper(), params.toArray());
		return list;
	}
	
	public List<EventColumn> querySqlForEventColumn(String sql,List<String> params){
		List<EventColumn> list = jdbcTemplate.query(sql, params.toArray(), new EventColumnMapper());
		return list;
	}
	
	/**
	 * 根据guid查询对应的过滤器
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<FilterOperator> querySqlForFilterOperator(String sql,List<String> params){
		List<FilterOperator> list = jdbcTemplate.query(sql, params.toArray(), new FilterOperatorMapper());
		return list;
	}
	
	public void insertFlinkRunningTimeErrorLog(FlinkRunningTimeErrorLog flinkRunningTimeErrorLog) {
		String guid = flinkRunningTimeErrorLog.getGuid();
		String ruleName = flinkRunningTimeErrorLog.getRuleName();
		String logInfo = flinkRunningTimeErrorLog.getLogInfo();
		logInfo = logInfo.replace("'"," ");
		String dateTime = DateUtil.format(new Date());
		String sql = "INSERT INTO `flink_error_log` (`guid`, `rule_name`,`log_info`,`date_time`) VALUES" +"('"+guid+"','"+ruleName+"','"+logInfo+"','"+dateTime+"')";
		logger.info("打印sql:{}",sql);
		jdbcTemplate.execute(sql);
	}
	
	
	
	
	public class EventTableMapper implements RowMapper<EventTable>{
		@Override
		public EventTable mapRow(ResultSet rs, int rowNum) throws SQLException {
			EventTable eventTable = new EventTable();
			eventTable.setId(rs.getString("id"));
			eventTable.setDescription(rs.getString("description"));
			eventTable.setDevicetype(rs.getString("devicetype"));
			eventTable.setDevicetypelevel(rs.getString("devicetypelevel"));
			eventTable.setEventtype(rs.getString("eventtype"));
			eventTable.setEventtypelevel(rs.getString("eventtypelevel"));
			eventTable.setGroupName(rs.getString("groupName"));
			eventTable.setLabel(rs.getString("groupName"));
			eventTable.setMultiTable(rs.getBoolean("is_multi_table"));
			eventTable.setIndexName(rs.getString("index_name"));
			eventTable.setTopicName(rs.getString("topic_name"));
			eventTable.setMonitor(rs.getBoolean("monitor"));
			eventTable.setFormatter(rs.getString("formatter"));
			eventTable.setName(rs.getString("name"));
			eventTable.setType(rs.getInt("type"));
			eventTable.setVersion(rs.getInt("version"));
			return eventTable;
		}
		
	}
	
	
	public class EventColumnMapper implements RowMapper<EventColumn>{
		@Override
		public EventColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
			EventColumn eventColumn = new EventColumn();
			eventColumn.setId(rs.getString("id"));
			eventColumn.setLabel(rs.getString("label"));
			eventColumn.setLen(rs.getString("len"));
			eventColumn.setName(rs.getString("name"));
			eventColumn.setNotNull(rs.getBoolean("not_null"));
			eventColumn.setPrimaryKey(rs.getBoolean("is_primary_key"));
			eventColumn.setSrcIp(rs.getBoolean("srcIp"));
			eventColumn.setDstIp(rs.getBoolean("dstIp"));
			eventColumn.setRelateIp(rs.getBoolean("relateIp"));
			eventColumn.setTimeLine(rs.getBoolean("timeLine"));
			eventColumn.setType(rs.getString("type"));
			eventColumn.setEventTableId(rs.getString("EventTable"));
			eventColumn.setDataHint(rs.getString("dataHint"));
			eventColumn.setOrder(rs.getInt("col_order"));
			eventColumn.setEventTime(rs.getBoolean("is_event_time"));
			return eventColumn;
		}
		
	}
	
	
	public class FilterOperatorMapper implements RowMapper<FilterOperator>{
		@Override
		public FilterOperator mapRow(ResultSet rs, int rowNum) throws SQLException {
			FilterOperator filterOperator = new FilterOperator();
			filterOperator.setGuid(rs.getString("guid"));
			filterOperator.setName(rs.getString("name"));
			filterOperator.setConfig(rs.getString("config"));
			filterOperator.setSource(rs.getString("source"));
			filterOperator.setOutputFields(rs.getString("output_fields"));
			filterOperator.setVersion(rs.getString("version"));
			filterOperator.setDeleteFlag(rs.getBoolean("delete_flag"));
			filterOperator.setDependencies(rs.getString("dependencies"));
			filterOperator.setOutputs(rs.getString("outputs"));
			filterOperator.setOperatorType(rs.getString("operator_type"));
			filterOperator.setMultiVersion(rs.getString("multi_version"));
			filterOperator.setCode(rs.getString("code"));
			filterOperator.setDesc(rs.getString("desc_"));
			filterOperator.setLabel(rs.getString("label"));
			filterOperator.setCreateTime(rs.getDate("create_time"));
			filterOperator.setUpdateTime(rs.getDate("update_time"));
			filterOperator.setRoomType(rs.getString("room_type"));
			filterOperator.setTag(rs.getString("tag"));
			filterOperator.setStartConfig(rs.getString("start_config"));
			return filterOperator;
		}
		
	}

    
	/**
	 * 查询个数
	 * @param sql
	 * @return
	 */
	public int querySqlForCount(String sql) {
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
		return count;
	}
	
	
	
}
