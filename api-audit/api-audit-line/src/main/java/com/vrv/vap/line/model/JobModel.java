package com.vrv.vap.line.model;

import java.util.HashMap;
import java.util.Map;

public class JobModel {
	private int id;

	private String name;

	private String jobName;

	private String startTime;

	private String cronTime;

	private Map<String,Object> params;

	private boolean isOpen = true;

	private boolean isRunning = false;

	private String jobClazz;

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCronTime() {
		return cronTime;
	}

	public void setCronTime(String cronTime) {
		this.cronTime = cronTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobClazz() {
		return jobClazz;
	}

	public void setJobClazz(String jobClazz) {
		this.jobClazz = jobClazz;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public JobModel() {
		this.params = new HashMap<>();
	}

	@Override
	public String toString() {
		return "JobModel [id=" + id + ", name=" + name + ", jobName=" + jobName + ", startTime=" + startTime
				+ ", cronTime=" + cronTime + ", isOpen=" + isOpen + ", isRunning=" + isRunning + ", jobClazz="
				+ jobClazz + "]";
	}
}
