package com.vrv.rule.model;

import java.util.Date;

import lombok.Data;

/**
 * flink规则异常日志收集
 * @author wd
 *
 */
@Data
public class FlinkRunningTimeErrorLog {

	private String guid;
	private String ruleName;
	private String logInfo;
	private Date dataTime;
	private String ruleLevel;
	private String exceptionType;
	
}
