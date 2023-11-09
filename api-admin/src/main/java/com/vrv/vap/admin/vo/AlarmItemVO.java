package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel(value="告警信息")
public class AlarmItemVO extends Query {
	@ApiModelProperty("告警类型")
	private String  alarmType;
	@ApiModelProperty("开始时间")
	@QueryMoreThan
	@Column(name="alarmTime")
	private String  startTime;
	@ApiModelProperty("结束时间")
	@QueryLessThan
	@Column(name="alarmTime")
	private String  endTime;

	private String formatType;
	/**
	 *  1：低 2：中 3：高
	 */
	@ApiModelProperty("告警级别")
	private Integer alarmLevel;
	@ApiModelProperty("告警来源")
	private String alarmSource;
	@ApiModelProperty("告警描述")
	private String alarmDesc;

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	public Integer getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(Integer alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getAlarmSource() {
		return alarmSource;
	}

	public void setAlarmSource(String alarmSource) {
		this.alarmSource = alarmSource;
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}
}
