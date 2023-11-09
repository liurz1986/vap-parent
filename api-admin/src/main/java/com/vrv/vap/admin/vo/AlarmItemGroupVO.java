package com.vrv.vap.admin.vo;


import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel(value="告警组信息")
public class AlarmItemGroupVO extends Query {
	/**
	 *  0 未处理，1 已处理，2 忽略
	 */
	@ApiModelProperty("更新状态")
	private Integer updateStatus;
	@ApiModelProperty("告警数据id")
	private String ids;
	@ApiModelProperty("告警类型")
	private String  alarmType;
	/**
	 *  1：低 2：中 3：高
	 */
	@ApiModelProperty("告警级别")
	private Integer alarmLevel;
	@ApiModelProperty("告警来源")
	private String alarmSource;
	@ApiModelProperty("告警描述")
	private String alarmDesc;
	@ApiModelProperty("开始时间")
	@QueryMoreThan
	@Column(name="alarmTime")
	private String  startTime;
	@ApiModelProperty("结束时间")
	@QueryLessThan
	@Column(name="alarmTime")
	private String  endTime;
	/**
	 *  0:未处理，1:已处理，2:忽略
	 */
	@ApiModelProperty("告警状态")
	private Integer alarmStatus;

	public Integer getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(Integer updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

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

	public Integer getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(Integer alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
}
