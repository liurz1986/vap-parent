package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

/**
 * 正在运行的VO
 * @author Administrator
 *
 *
 */
@Data
public class RunningTaskVO {

	
	private String runningTime; //运行时间
	private String jobID;  //运行的任务ID
	private String jobName; //任务名称
	
	
}
