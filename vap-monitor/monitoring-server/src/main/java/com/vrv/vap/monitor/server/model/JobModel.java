package com.vrv.vap.monitor.server.model;

import org.quartz.Job;

public class JobModel {
	private int id;

	private String name;

	private String jobName;

	private String startTime;

	private String cronTime;

	private boolean isOpen = true;

	private boolean isRunning = false;

	private Class<? extends Job> jobClazz;

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

	public Class<? extends Job> getJobClazz() {
		return jobClazz;
	}

	public void setJobClazz(Class<? extends Job> jobClazz) {
		this.jobClazz = jobClazz;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	@Override
	public String toString() {
		return "JobModel [id=" + id + ", name=" + name + ", jobName=" + jobName + ", startTime=" + startTime
				+ ", cronTime=" + cronTime + ", isOpen=" + isOpen + ", isRunning=" + isRunning + ", jobClazz="
				+ jobClazz + "]";
	}
}
