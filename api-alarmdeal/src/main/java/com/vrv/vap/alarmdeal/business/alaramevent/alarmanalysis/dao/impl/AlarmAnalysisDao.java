package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.dao.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.NameValueBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.DimensionTableColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ParamsColumn;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.NameValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * * 
 *
 * @author wudi   E‐mail:wudi@vrvmail.com.cn  @version 创建时间：2019年5月27日 下午2:32:12
 *          类说明 威胁类型查询接口
 */
@Repository
public class AlarmAnalysisDao {

	private static Logger logger = LoggerFactory.getLogger(AlarmAnalysisDao.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 按威胁类型统计威胁值的和
	 *
	 * @return
	 */
	public List<Map<String, Object>> queryThreatValueByThreatType() {
		String sql = "SELECT " + " risk_analysis.threat_guid AS threat_guid,"
				+ " risk_analysis.threat_name AS threat_name," + " SUM(risk_analysis.threat_level) as threat_value"
				+ " FROM " + " risk_analysis"
				+ " INNER JOIN threat_info ON risk_analysis.threat_guid = threat_info.id GROUP BY threat_guid,threat_name ORDER BY threat_value DESC";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	/**
	 * 根据威胁等级统计威胁等级的个数
	 *
	 * @return
	 */
	public List<Map<String, Object>> queryThreatLevelCountByThreatLevel() {
		List<String> threatLevelList = getThreatLevelList();
		String sql = "SELECT " + " CASE threat_level " + " WHEN 5 THEN " + " '很高' " + " WHEN 4 THEN " + " '高'"
				+ "WHEN 3 THEN " + " '中等' " + "WHEN 2 THEN " + "  '低' " + "WHEN 1 THEN " + "  '很低' END AS threat_level,"
				+ "	COUNT(threat_level) AS threat_count" + " FROM " + " risk_analysis " + " GROUP BY "
				+ " risk_analysis.threat_level";
		List<Map<String, Object>> queryList = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : queryList) {
			Object object = map.get("threat_level");
			if (object != null) {
				threatLevelList.remove(object.toString());
			}
		}

		for (String str : threatLevelList) {
			Map<String, Object> map = new HashMap<>();
			map.put("threat_level", str);
			map.put("threat_count", 0);
			queryList.add(map);
		}
		return queryList;
	}

	private List<String> getThreatLevelList() {
		List<String> list = new ArrayList<>();
		list.add("很高");
		list.add("高");
		list.add("中等");
		list.add("低");
		list.add("很低");
		return list;
	}

	/**
	 * 根据部门进行威胁排分组
	 *
	 * @return
	 */
	public List<Map<String, Object>> queryThreatRankByDepartMent() {
		String sql = "SELECT " + "	org.`name` AS org_name, " + "  COUNT(asset.Guid) AS asset_total, "
				+ "  COUNT(risk_analysis.threat_level) AS threat_value " + " FROM asset "
				+ " LEFT JOIN base_koal_org AS org ON asset.org = org.`code` "
				+ " INNER JOIN risk_analysis ON asset.Guid = risk_analysis.asset_guid "
				+ " WHERE asset.org IS NOT NULL " + " AND asset.org <> '' GROUP BY org_name ORDER BY threat_value DESC";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	/**
	 * 查询威胁排名通过责任人
	 *
	 * @return
	 */
	public List<Map<String, Object>> queryThreatRankByEmployee() {
		String sql = "SELECT " + " asset.employee_Code1 AS employee_code, " + " COUNT(asset.Guid) AS asset_total, "
				+ " COUNT(risk_analysis.threat_level) AS threat_value " + " FROM asset "
				+ " INNER JOIN risk_analysis ON asset.Guid = risk_analysis.asset_guid "
				+ " WHERE asset.employee_Code1 IS NOT NULL "
				+ "AND asset.employee_Code1 <> '' GROUP BY  employee_code ORDER BY threat_value DESC ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	/**
	 * 获得告警最新分数
	 *
	 * @return
	 */
	public Integer getSumAlarmScore(String ip, String startTime, String endTime) {
		String sql = "select sum(score_value) from src_ip_score where srcIp = ?"  + " and create_date between  ?" + " and  ?" ;
		Integer sum = jdbcTemplate.queryForObject(sql, Integer.class,new Object[]{ip,startTime,endTime});
		return sum;
	}

	/**
	 * 告警扣分趋势图
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Map<String, Object>> getAlarmScoreTrend(String ip, String startTime, String endTime) {
		String sql = "SELECT SUM(score_value) AS sum,create_date from src_ip_score where srcIp = ?"
				+ " and create_date between ?"  + " and  ?" + " GROUP BY create_date";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,new Object[]{ip,startTime,endTime});
		return list;

	}

	public List<DimensionTableColumn> getDimensionTableColumns(String dimensionTableName) {
		String sql = "SELECT   " + "	COLUMN_NAME AS 'columnName',   " + "	data_type AS 'columnType',   "
				+ "	CHARACTER_MAXIMUM_LENGTH AS 'characterMaximumLength',   "
				+"  (CASE WHEN  IS_NULLABLE='NO' THEN FALSE  ELSE FALSE END ) AS 'isMust', "
				+ "	COLUMN_COMMENT AS 'columnComment'   " + "FROM   " + "	INFORMATION_SCHEMA. COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "'";

		List<DimensionTableColumn> query = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<DimensionTableColumn>(DimensionTableColumn.class));
		return query;
	}

	public List<DataRow> getDimensionTableData(String dimensionTableName, List<ParamsColumn> columns) {
		if (columns == null || columns.isEmpty()) {
			return new ArrayList<>();
		}

		String sqlCount = "SELECT count(*)  FROM  INFORMATION_SCHEMA.COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "' and COLUMN_NAME='foreign_key_id'";
		Long count = jdbcTemplate.queryForObject(sqlCount, Long.class);
		// 判断该维表是否有外键
		String sqlQuery = "select  {0}  from " + dimensionTableName;
		if (count > 0) {
			sqlQuery += " where foreign_key_id = 'null'";
		}

		List<String> columnNames = new ArrayList<>();

		for (ParamsColumn column : columns) {
			columnNames.add("`" + column.getDataIndex() + "`");
		}

		sqlQuery = sqlQuery.replace("{0}", StringUtils.join(columnNames, ","));
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sqlQuery);

//		String sqlNull = "select  {0}  from " + dimensionTableName;
//		if (count > 0) {
//			sqlNull += " where rule_code is null and filter_code is null";
//		}
//		List<Map<String, Object>> queryForListNull = jdbcTemplate.queryForList(sqlNull);
//		queryForList.addAll(queryForListNull);
		List<DataRow> result = new ArrayList<>();

		for (Map<String, Object> map : queryForList) {
			DataRow row = new DataRow();
			List<NameValueBean> cells = new ArrayList<>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object obj = entry.getValue();
				// 如果值为null，值转换成空
				if(obj ==null){
					cells.add(new NameValueBean("", entry.getKey()));
				}else{
					cells.add(new NameValueBean(obj, entry.getKey()));
				}

			}
			row.setRow(cells);
			result.add(row);
		}
		return result;
	}

	public List<DataRow> getDimensionTableData(String dimensionTableName, String ruleId,String filterCode, List<ParamsColumn> columns) {
		if (columns == null || columns.isEmpty()) {
			return new ArrayList<>();
		}

		String sqlCount = "SELECT count(*)  FROM  INFORMATION_SCHEMA.COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "' and COLUMN_NAME='foreign_key_id'";
		Long count = jdbcTemplate.queryForObject(sqlCount, Long.class);
		// 判断该维表是否有外键
		String sqlQuery = "select  {0}  from " + dimensionTableName;
		if (count > 0) {
			sqlQuery += " where rule_code='" + ruleId + "' and filter_code =  '"+filterCode+"'";
		}

		List<String> columnNames = new ArrayList<>();

		for (ParamsColumn column : columns) {
			columnNames.add("`" + column.getDataIndex() + "`");
		}

		sqlQuery = sqlQuery.replace("{0}", StringUtils.join(columnNames, ","));
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sqlQuery);

//		String sqlNull = "select  {0}  from " + dimensionTableName;
//		if (count > 0) {
//			sqlNull += " where rule_code is null and filter_code is null";
//		}
//		List<Map<String, Object>> queryForListNull = jdbcTemplate.queryForList(sqlNull);
//		queryForList.addAll(queryForListNull);
		List<DataRow> result = new ArrayList<>();

		for (Map<String, Object> map : queryForList) {
			DataRow row = new DataRow();
			List<NameValueBean> cells = new ArrayList<>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getValue()==null)
				{
					continue;
				}
				cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
			}
			row.setRow(cells);
			result.add(row);
		}
		return result;
	}

	public List<DataRow> getDimensionTableData(String dimensionTableName, String ruleId, List<ParamsColumn> columns) {
		if (columns == null || columns.isEmpty()) {
			return new ArrayList<>();
		}

		String sqlCount = "SELECT count(*)  FROM  INFORMATION_SCHEMA.COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "' and COLUMN_NAME='foreign_key_id'";
		Long count = jdbcTemplate.queryForObject(sqlCount, Long.class);
		// 判断该维表是否有外键
		String sqlQuery = "select  {0}  from " + dimensionTableName;
		if (count > 0) {
			sqlQuery += " where foreign_key_id='" + ruleId + "' or foreign_key_id = 'null'";
		}

		List<String> columnNames = new ArrayList<>();

		for (ParamsColumn column : columns) {
			columnNames.add("`" + column.getDataIndex() + "`");
		}

		sqlQuery = sqlQuery.replace("{0}", StringUtils.join(columnNames, ","));
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sqlQuery);

		List<DataRow> result = new ArrayList<>();

		for (Map<String, Object> map : queryForList) {
			DataRow row = new DataRow();
			List<NameValueBean> cells = new ArrayList<>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getValue()==null)
				{
					continue;
				}
				cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
			}
			row.setRow(cells);
			result.add(row);
		}
		return result;
	}

	public void saveDimensionTableData(String dimensionTableName, String ruleId, List<DataRow> rows) {
		if(!checkString(dimensionTableName)){
			throw new RuntimeException("dimensionTableName 参数异常");
		}
		String sqlCount = "SELECT count(*)" + "FROM   " + "	INFORMATION_SCHEMA. COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "' and COLUMN_NAME='foreign_key_id'";
		Long count = jdbcTemplate.queryForObject(sqlCount, Long.class);

		// 判断是否有主键
		String prisql = "SELECT     " + "  COLUMN_NAME  AS 'columnName',    " + "  data_type  AS 'columnType',    "
				+ "      "
				+ "  (CASE WHEN  extra LIKE '%auto_increment%' THEN '是' ELSE '否' END ) AS 'autoIncrement'    "
				+ "FROM    " + "  INFORMATION_SCHEMA.COLUMNS     " + "WHERE table_name = '" + dimensionTableName
				+ "'     " + "AND column_key = 'PRI'     ";

		String idColumnName = "";
		boolean autoIncrement = false;
		//上回尝试改过，不好改，怕影响功能。
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(prisql);
		if (queryForList != null && !queryForList.isEmpty()) {
			Map<String, Object> map = queryForList.get(0);
			idColumnName = map.get("columnName").toString();

			autoIncrement = "是".equals(map.get("autoIncrement").toString());
		}

		if (count == 0) {
			String delete = "delete  from " + dimensionTableName + " ;";
			jdbcTemplate.execute(delete);

		} else {
			String delete = "delete  from " + dimensionTableName + " where foreign_key_id='" + ruleId + "';";
			jdbcTemplate.execute(delete);
		}

		List<DimensionTableColumn> dimensionTableColumns = getDimensionTableColumns(dimensionTableName);
		Map<String,String>  colMap=new HashMap<>();
		dimensionTableColumns.forEach(col->{
			colMap.put(col.getColumnName(), col.getColumnComment());
		});





		for (DataRow row : rows) {
			String insert = "INSERT INTO `" + dimensionTableName + "` ({0})   " + "VALUES({1}) ;";

			List<String> columns = new ArrayList<>();
			List<String> values = new ArrayList<>();

			if (StringUtils.isNotEmpty(idColumnName)) {
				if (!autoIncrement) {
					columns.add("`" + idColumnName + "`");
					values.add("'" + UUIDUtils.get32UUID() + "'");
				}
			}

			for (NameValueBean cell : row.getRow()) {
				if (colMap.containsKey(cell.getName())) {
					columns.add("`" + cell.getName() + "`");
					values.add("'" + cell.getValue() + "'");
				}
			}
			// 存在外键
			if (count > 0) {
				columns.add("`foreign_key_id`");
				values.add("'" + ruleId + "'");
			}

			insert = insert.replace("{0}", StringUtils.join(columns, ",")).replace("{1}",
					StringUtils.join(values, ","));
			jdbcTemplate.execute(insert);
		}
	}

	public void saveDimensionTableData(String dimensionTableName, String ruleId,String filterCode, List<DataRow> rows,boolean isSync) {
		if(!checkString(dimensionTableName)){
			throw new RuntimeException("dimensionTableName 参数异常");
		}
		String sqlCount = "SELECT count(*)" + "FROM   " + "	INFORMATION_SCHEMA. COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "' and COLUMN_NAME='foreign_key_id'";
		Long count = jdbcTemplate.queryForObject(sqlCount, Long.class);

		// 判断是否有主键
		String prisql = "SELECT     " + "  COLUMN_NAME  AS 'columnName',    " + "  data_type  AS 'columnType',    "
				+ "      "
				+ "  (CASE WHEN  extra LIKE '%auto_increment%' THEN '是' ELSE '否' END ) AS 'autoIncrement'    "
				+ "FROM    " + "  INFORMATION_SCHEMA.COLUMNS     " + "WHERE table_name = '" + dimensionTableName
				+ "'     " + "AND column_key = 'PRI'     ";

		String idColumnName = "";
		boolean autoIncrement = false;
		boolean isInsertTime = false;
		String insertTime = DateUtil.format(DateUtil.addDay(new Date(),-1),DateUtil.Year_Mouth_Day);
		//上回尝试改过，不好改，怕影响功能。
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(prisql);
		if (queryForList != null && !queryForList.isEmpty()) {
			Map<String, Object> map = queryForList.get(0);
			idColumnName = map.get("columnName").toString();
			autoIncrement = "是".equals(map.get("autoIncrement").toString());
		}

		if (count == 0) {
			String delete = "delete  from " + dimensionTableName + " ;";
			jdbcTemplate.execute(delete);
		}else if(StringUtils.isBlank(ruleId) && StringUtils.isBlank(filterCode)){
			String delete = "delete  from " + dimensionTableName + " where foreign_key_id='" + ruleId + "';";
			jdbcTemplate.execute(delete);
		}else if(isSync){
			String delete = "delete  from " + dimensionTableName + " where is_sync = 1 and rule_code='" + ruleId + "' and filter_code='"+filterCode+"';";
			jdbcTemplate.execute(delete);
		}else {
			String delete = "delete  from " + dimensionTableName + " where rule_code='" + ruleId + "' and filter_code='"+filterCode+"';";
			jdbcTemplate.execute(delete);
		}

		List<DimensionTableColumn> dimensionTableColumns = getDimensionTableColumns(dimensionTableName);
		Map<String,String>  colMap=new HashMap<>();
		dimensionTableColumns.stream().filter(item->!"id".equals(item.getColumnName())).forEach(col->{
			colMap.put(col.getColumnName(), col.getColumnComment());
		});
		List<DimensionTableColumn> filterList = dimensionTableColumns.stream().filter(item->"insert_time".equals(item.getColumnName())).collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(filterList)){
			isInsertTime = true;
		}

		boolean isSyncFlag = false;

		for (DataRow row : rows) {
			String insert = "INSERT INTO `" + dimensionTableName + "` ({0})   " + "VALUES({1}) ;";

			List<String> columns = new ArrayList<>();
			List<String> values = new ArrayList<>();

			if (StringUtils.isNotEmpty(idColumnName)) {
				if (!autoIncrement) {
					columns.add("`" + idColumnName + "`");
					values.add("'" + UUIDUtils.get32UUID() + "'");
				}
			}

			for (NameValueBean cell : row.getRow()) {
				if (colMap.containsKey(cell.getName())) {
					columns.add("`" + cell.getName() + "`");
					values.add("'" + cell.getValue() + "'");
				}
				if("is_sync".equals(cell.getName())){
					isSyncFlag = true;
				}
			}
			if(!columns.contains("`insert_time`") && isInsertTime){
				columns.add("`insert_time`");
				values.add("'"+insertTime+"'");
			}
			// 存在外键
			if (count > 0) {
				columns.add("`foreign_key_id`");
				values.add("'" + ruleId + "'");
				if(StringUtils.isNotBlank(ruleId) && !"null".equals(ruleId)){
					columns.add("`rule_code`");
					values.add("'" + ruleId + "'");
				}
				if(StringUtils.isNotBlank(filterCode) && !"null".equals(filterCode)){
					columns.add("`filter_code`");
					values.add("'" + filterCode + "'");
				}
				if(!isSyncFlag){
					if(isSync){
						columns.add("`is_sync`");
						values.add("1");
					}else{
						columns.add("`is_sync`");
						values.add("0");
					}
				}
			}
			insert = insert.replace("{0}", StringUtils.join(columns, ",")).replace("{1}",
					StringUtils.join(values, ","));
			jdbcTemplate.execute(insert);
		}
	}


	public void saveDimensionTableData(String dimensionTableName, String ruleId,String filterCode, List<DataRow> rows) {
		if(!checkString(dimensionTableName)){
			throw new RuntimeException("dimensionTableName 参数异常");
		}
		String sqlCount = "SELECT count(*)" + "FROM   " + "	INFORMATION_SCHEMA. COLUMNS   " + "WHERE   "
				+ "	 table_name = '" + dimensionTableName + "' and COLUMN_NAME='foreign_key_id'";
		Long count = jdbcTemplate.queryForObject(sqlCount, Long.class);

		// 判断是否有主键
		String prisql = "SELECT     " + "  COLUMN_NAME  AS 'columnName',    " + "  data_type  AS 'columnType',    "
				+ "      "
				+ "  (CASE WHEN  extra LIKE '%auto_increment%' THEN '是' ELSE '否' END ) AS 'autoIncrement'    "
				+ "FROM    " + "  INFORMATION_SCHEMA.COLUMNS     " + "WHERE table_name = '" + dimensionTableName
				+ "'     " + "AND column_key = 'PRI'     ";

		String idColumnName = "";
		boolean autoIncrement = false;
		boolean isInsertTime = false;
		String insertTime = DateUtil.format(DateUtil.addDay(new Date(),-1),DateUtil.Year_Mouth_Day);
		//上回尝试改过，不好改，怕影响功能。
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(prisql);
		if (queryForList != null && !queryForList.isEmpty()) {
			Map<String, Object> map = queryForList.get(0);
			idColumnName = map.get("columnName").toString();
			autoIncrement = "是".equals(map.get("autoIncrement").toString());
		}

		if (count == 0) {
			String delete = "delete  from " + dimensionTableName + " ;";
			jdbcTemplate.execute(delete);
		}else if(StringUtils.isBlank(ruleId) && StringUtils.isBlank(filterCode)){
			String delete = "delete  from " + dimensionTableName + " where foreign_key_id='" + ruleId + "';";
			jdbcTemplate.execute(delete);
		}else {
			String delete = "delete  from " + dimensionTableName + " where rule_code='" + ruleId + "' and filter_code='"+filterCode+"';";
			jdbcTemplate.execute(delete);
		}

		List<DimensionTableColumn> dimensionTableColumns = getDimensionTableColumns(dimensionTableName);
		Map<String,String>  colMap=new HashMap<>();
		dimensionTableColumns.stream().filter(item->!"id".equals(item.getColumnName())).forEach(col->{
			colMap.put(col.getColumnName(), col.getColumnComment());
		});
		List<DimensionTableColumn> filterList = dimensionTableColumns.stream().filter(item->"insert_time".equals(item.getColumnName())).collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(filterList)){
			isInsertTime = true;
		}

		for (DataRow row : rows) {
			String insert = "INSERT INTO `" + dimensionTableName + "` ({0})   " + "VALUES({1}) ;";

			List<String> columns = new ArrayList<>();
			List<String> values = new ArrayList<>();

			if (StringUtils.isNotEmpty(idColumnName)) {
				if (!autoIncrement) {
					columns.add("`" + idColumnName + "`");
					values.add("'" + UUIDUtils.get32UUID() + "'");
				}
			}

			for (NameValueBean cell : row.getRow()) {
				if (colMap.containsKey(cell.getName())) {
					columns.add("`" + cell.getName() + "`");
					values.add("'" + cell.getValue() + "'");
				}
			}

			if(!columns.contains("insert_time") && isInsertTime){
				columns.add("`insert_time`");
				values.add("'"+insertTime+"'");
			}
			// 存在外键
			if (count > 0) {
				columns.add("`foreign_key_id`");
				values.add("'" + ruleId + "'");
				if(StringUtils.isNotBlank(ruleId) && !"null".equals(ruleId)){
					columns.add("`rule_code`");
					values.add("'" + ruleId + "'");
				}
				if(StringUtils.isNotBlank(filterCode) && !"null".equals(filterCode)){
					columns.add("`filter_code`");
					values.add("'" + filterCode + "'");
				}
//				if(isSync){
//					columns.add("`is_sync`");
//					values.add("1");
//				}else{
//					columns.add("`is_sync`");
//					values.add("0");
//				}
			}
			insert = insert.replace("{0}", StringUtils.join(columns, ",")).replace("{1}",
					StringUtils.join(values, ","));
			jdbcTemplate.execute(insert);
		}
	}

	/**
	 * 校验名称
	 *
	 * @param name 名称
	 * @return 是否校验通过
	 */
	public boolean checkString(String name){
		String regex = "(([a-zA-Z]+)([_]*)){1,}([a-zA-Z]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}


	/**
	 * 事件规则 开启统计
	 * @return  isStarted (0/1)  eventRuleId  eventRuleParentId
	 */
	public List<Map<String, Object>> getEventRuleStartedStatistics()
	{
		String sql=" SELECT ( CASE WHEN  `risk_event_rule`.`isStarted`=TRUE  THEN  1  ELSE   0 END) AS 'isStarted',`event_category`.`id`  AS 'eventRuleId',`event_category`.`parent_id`   AS 'eventRuleParentId'  " +
				"FROM `risk_event_rule`     " +
				"INNER JOIN `event_category` ON  `event_category`.id=`risk_event_rule`.`riskEventId`   " +
				"WHERE `risk_event_rule`.`delete_flag`=TRUE AND  (`risk_event_rule`.`rule_type` IS NULL OR  `risk_event_rule`.`rule_type`<>'model')  ";

		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		return queryForList;
	}

}
