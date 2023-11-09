package com.vrv.vap.admin.common.manager;


import com.vrv.vap.admin.common.task.DbBackupTask;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.DbBackupStrategyService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.common.task.ReportCycleTask;
import com.vrv.vap.admin.common.task.ServiceMonitorTask;
import com.vrv.vap.admin.service.SystemConfig2Service;
import com.vrv.vap.admin.service.VisualReportCycleService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * 动态定时任务管理
 * Created by lizj on 2020/05/26.
 */
public class TaskManager {
	private static Logger log = LoggerFactory.getLogger(TaskManager.class);


	private static Scheduler scheduler = null;

	static {
		try {
			Properties props = new Properties();
			props.put("org.quartz.scheduler.instanceName", "dynamic");
			props.put("org.quartz.threadPool.threadCount", "10");
			StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
			stdSchedulerFactory.initialize(props);

			scheduler = stdSchedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			log.error("", e);
		}
	}

	public static void addJob(JobModel jobModel){
		addJob(jobModel, null);
	}

	public static void resumeJob(String jobName){
		log.info("重启开启任务：" + LogForgingUtil.validLog(jobName));
		try {
			scheduler.resumeJob(JobKey.jobKey(jobName));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void addJob(JobModel jobModel, Map<String, String> param){
		if (!jobModel.isOpen()) {
			return;
		}
		try {
			log.info("添加任务:" + LogForgingUtil.validLog(jobModel.getJobName()));
			log.info(LogForgingUtil.validLog(jobModel.getJobName() + ":" + jobModel.getCronTime()));
			JobDetailImpl job = new JobDetailImpl();
			job.setKey(JobKey.jobKey(jobModel.getJobName()));
			job.setName(jobModel.getJobName());
			job.setJobClass(jobModel.getJobClazz());

			JobDataMap jobDataMap = new JobDataMap();

			if (null != param) {
				for (Map.Entry<String, String> entry : param.entrySet()) {
					jobDataMap.put(entry.getKey(), entry.getValue());
				}
			}

			job.setJobDataMap(jobDataMap);

			CronTriggerImpl trigger = new CronTriggerImpl();
			trigger.setKey(TriggerKey.triggerKey(jobModel.getJobName()));
			trigger.setName(jobModel.getJobName());
			trigger.setCronExpression(jobModel.getCronTime());
			scheduler.scheduleJob(job, trigger);
		} catch (Exception e) {
			log.error("",e);
		}
	}

	public static void removeJob(JobModel jobModel){
		try {
			log.info("移除任务:" + LogForgingUtil.validLog(jobModel.getJobName()));
			String jobName = jobModel.getJobName();
			// 先判断是否存在
			if (isExists(jobModel)) {
				scheduler.pauseTrigger(TriggerKey.triggerKey(jobName));
				scheduler.pauseJob(JobKey.jobKey(jobName));
				scheduler.unscheduleJob(TriggerKey.triggerKey(jobName));
				scheduler.deleteJob(JobKey.jobKey(jobName));
			}
		} catch (SchedulerException e) {
			log.error("",e);
		}
	}

	public static void triggerJob(String jobName) {
		log.info("立即触发任务：" + LogForgingUtil.validLog(jobName));
		try {
			scheduler.triggerJob(JobKey.jobKey(jobName));
		} catch (Exception e) {
			log.error("",e);
		}
	}

	public static void pauseJob(String jobName) {
		log.info("中止任务：" + LogForgingUtil.validLog(jobName));
		try {
			scheduler.pauseTrigger(TriggerKey.triggerKey(jobName));
			scheduler.pauseJob(JobKey.jobKey(jobName));
		} catch (Exception e) {
			log.error("",e);
		}
	}

	public static boolean isExists(JobModel jobModel){
		boolean result = false;
		try {
			log.info("判断任务:" + LogForgingUtil.validLog(jobModel.getJobName()) + "是否存在");
			String jobName = jobModel.getJobName();
			// 先判断是否存在
			if (scheduler.checkExists(JobKey.jobKey(jobName))) {
				result = true;
			}
		} catch (SchedulerException e) {
			log.error("",e);
		}
		return result;
	}

	public static void modifyJob(JobModel jobModel) throws SchedulerException, ParseException {
		log.info("修改任务:" + jobModel);
		removeJob(jobModel);
		addJob(jobModel);
	}

	public static void start() throws SchedulerException {
		scheduler.start();
	}

	public static void loadTask() throws ParseException, SchedulerException {
		log.info("开始加载动态定时任务");
		VisualReportCycleService visualReportCycleService = SpringContextUtil.getApplicationContext().getBean(VisualReportCycleService.class);
		List<VisualReportCycle>  visualReportCycleList = visualReportCycleService.findAll();
		// 加载周期报表
		for (VisualReportCycle info : visualReportCycleList) {
			if(info.getStatus()!=null && info.getStatus() == 1) {
				JobModel jobModel = new JobModel();
				jobModel.setJobName("reportCycle-" + info.getId());
				jobModel.setCronTime(info.getCron());
				jobModel.setJobClazz(ReportCycleTask.class);
				Map<String, String> param = new HashMap<String, String>();
				param.put("id", info.getId().toString());
				TaskManager.addJob(jobModel, param);
			}
		}

		// 添加服务状态监控任务
		//addServiceMonitorTask();

		//添加数据库备份任务
		DbBackupStrategyService dbBackupStrategyService = SpringContextUtil.getApplicationContext().getBean(DbBackupStrategyService.class);
		List<DbBackupStrategy> dbBackupStrategyList = dbBackupStrategyService.findByProperty(DbBackupStrategy.class,"strategyStatus", 1);
		if (CollectionUtils.isNotEmpty(dbBackupStrategyList)) {
			dbBackupStrategyList.forEach(dbBackupStrategy -> {
				addBackupTask(dbBackupStrategy);
			});
		}

		start();
	}

	public static void addServiceMonitorTask() {
		SystemConfig2Service systemConfigService = SpringContextUtil.getApplicationContext().getBean( SystemConfig2Service.class);
		JobModel jobModel = new JobModel();
		jobModel.setJobName("serviceMonitor");
		jobModel.setCronTime(getConfigValue("service_monitor_cycle", systemConfigService));
		jobModel.setJobClazz(ServiceMonitorTask.class);

		Map<String, String> param = new HashMap<String, String>();
		param.put("warnValue", getConfigValue("es_disk_use_warn", systemConfigService));
		TaskManager.addJob(jobModel, param);
	}

	public static void addBackupTask(DbBackupStrategy dbBackupStrategy) {
		JobModel jobModel = new JobModel();
		jobModel.setJobName("dbBackupTask_" + dbBackupStrategy.getId());
		String[] time = dbBackupStrategy.getBackupTime().split(":");
		if (time[1].startsWith("0")) {
			time[1] = time[1].substring(1);
		}
		if (time[0].startsWith("0")) {
			time[0] = time[0].substring(1);
		}
		jobModel.setCronTime(String.format("0 %s %s */%s * ?", time[1], time[0], dbBackupStrategy.getBackPeriod()));
		jobModel.setJobClazz(DbBackupTask.class);

		Map<String, String> param = new HashMap<>();
		param.put("expireTime", String.valueOf(dbBackupStrategy.getMaxVersion() * dbBackupStrategy.getBackPeriod()));
		param.put("dataTypes", dbBackupStrategy.getDataTypes());
		TaskManager.addJob(jobModel, param);
	}

	private static String getConfigValue(String confId, SystemConfig2Service systemConfigService) {
		SystemConfig2 model = new SystemConfig2();
		model.setConfId(confId);
		SystemConfig2 systemConfig = systemConfigService.findOne(model);
		return systemConfig.getConfValue();
	}

	public static List<JobModel> getTaskList() {
		List<JobModel> list = new ArrayList<>();
		try {
			CronTriggerImpl trigger = null;
			JobModel jobModel = null;
			for (JobKey jobName : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP))) {
				String name = jobName.getName();
				trigger = (CronTriggerImpl) scheduler.getTrigger(TriggerKey.triggerKey(name));
				jobModel = new JobModel();
				jobModel.setCronTime(trigger.getCronExpression());
				String[] idName = jobName.getName().split("_");
				jobModel.setJobName(jobName.getName());
				jobModel.setId(Integer.parseInt(idName[1]));
				jobModel.setName(idName[0]);
				jobModel.setRunning(false);
				jobModel.setOpen(true);
				list.add(jobModel);
			}
			for (JobExecutionContext jc : scheduler.getCurrentlyExecutingJobs()) {
				jobModel = new JobModel();
				jobModel.setCronTime(null);
				String[] idName = jc.getJobDetail().getKey().getName().split("_");
				jobModel.setJobName(jc.getJobDetail().getKey().getName());
				jobModel.setId(Integer.parseInt(idName[0]));
				jobModel.setName(idName[1]);
				jobModel.setRunning(true);
				jobModel.setOpen(true);
				jobModel.setJobClazz(jc.getJobInstance().getClass());
				list.add(jobModel);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return list;
	}

}
