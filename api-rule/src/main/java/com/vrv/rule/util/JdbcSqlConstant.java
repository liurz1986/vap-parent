package com.vrv.rule.util;

/**
 * flink各类查询数据
 * @author wd-pc
 *
 */
public class JdbcSqlConstant {

	public static final String EVENT_TABLE_COLUMN_SQL = "select * from event_column where 1=1 and EventTable  = ? ORDER BY col_order asc";
	
	public static final String EVENT_TABLE_SQL = "select * from event_table where 1=1 and id  = ?";
	
	public static final String FILTER_TABLE_SQL = "select * from filter_operator where 1=1 and code = ? and delete_flag=1";
	
	public static final String RISK_CODE_SQL = "select rule_code from risk_event_rule where 1=1 and id = ?";
	
	//获得策略的等级
    public static final String RISK_NAME_WEIGHT_SQL = "select name_,levelstatus from risk_event_rule where 1=1 and analysis_id = ? and isStarted=1";

	public static final String OFFLINE_TOPIC="select event_table_id,topic from offline_extract_task where 1=1 and guid= ?";

    //查询规则
	public static final String FILTER_NAME_WEIGHT_SQL = "select label from filter_operator where 1=1 and code = ? and delete_flag =1";
	
}
