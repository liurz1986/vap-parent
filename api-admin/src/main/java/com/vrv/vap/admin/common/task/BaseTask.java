package com.vrv.vap.admin.common.task;


import com.vrv.vap.admin.common.util.TimeTools;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础任务
 *
 */
public abstract class BaseTask implements Job {
	public static final Logger log = LoggerFactory.getLogger(BaseTask.class);
	/**
	 * 保存正在执行的任务,防止重复运行
	 */
	private static final Map<String, String> TASKS = new HashMap<>();

	public static final String ID_KEY = "id";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String jobClassName = arg0.getJobDetail().getJobClass().getName();
		JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();
		if (null != jobDataMap.get(ID_KEY)) {
			jobClassName = jobClassName + "_" + jobDataMap.get(ID_KEY);
		}

		Date nowTime = TimeTools.getNow();
		Date startTime = StringUtils.isNotEmpty(TASKS.get(jobClassName)) ? TimeTools.parseDate5(TASKS.get(jobClassName)) : nowTime;
		if (startTime.before(nowTime)){
			String val = TASKS.remove(jobClassName);
			log.info("",new StringBuffer().append("before task fail force remove job:").append(jobClassName).append(" start at:").append(val)
					.append(" remove at:").append(TimeTools.formatDate(TimeTools.getNow(), "yyyy/MM/dd HH:mm:ss")));
		}
		if (!TASKS.containsKey(jobClassName)) {
			log.info("start " + jobClassName);

			TASKS.put(jobClassName, TimeTools.format2(TimeTools.getNow()));
			if (jobDataMap.size() != 0) {
				this.run(jobClassName, jobDataMap);
			} else {
				this.run(jobClassName);
			}

			String val = TASKS.remove(jobClassName);

			log.info("remove job:" + jobClassName + " start at:" + val +
					" remove at:" + TimeTools.formatDate(TimeTools.getNow(), "yyyy/MM/dd HH:mm:ss"));
		} else {
			log.info("job " + jobClassName + " already running,start at:" +
					TASKS.get(jobClassName));

		}
	}

	abstract void run(String jobName);

	void run(String jobName, JobDataMap jobDataMap) {
		this.run(jobName);
	}
}
