package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * flink规则异常日志收集
 * @author wd
 *
 */
@Data
@Entity
@Table(name = "flink_error_log")
public class FlinkRunningTimeErrorLog {
	@Id
	@Column(name="guid",length = 50)
	private String guid;
	@Column(name="rule_name")
	private String ruleName;
	@Column(name="log_info")
	private String logInfo;
	@Column(name="date_time")
	private Date dataTime;
	@Column(name="rule_level")
	private String ruleLevel; //规则等级
	@Column(name="exception_type")
	private String exceptionType; //异常类型
	
}
