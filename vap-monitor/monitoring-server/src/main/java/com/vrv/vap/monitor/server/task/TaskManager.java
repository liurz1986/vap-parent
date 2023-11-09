package com.vrv.vap.monitor.server.task;

import com.vrv.vap.monitor.server.common.util.LogForgingUtil;
import com.vrv.vap.monitor.server.model.JobModel;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Map;
import java.util.Properties;

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
            log.error("Scheduler任务动态配置异常!", e);
        }
    }

    public static void addJob(JobModel jobModel) {
        addJob(jobModel, null);
    }

    public static void resumeJob(String jobName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJob(JobModel jobModel, Map<String, String> param) {
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
            log.error("添加任务异常", e);
        }
    }

    public static void removeJob(JobModel jobModel) {
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
            log.error("移除任务异常", e);
        }
    }

    public static boolean isExists(JobModel jobModel) {
        boolean result = false;
        try {
            log.info("判断任务:" + LogForgingUtil.validLog(jobModel.getJobName()) + "是否存在");
            String jobName = jobModel.getJobName();
            // 先判断是否存在
            if (scheduler.checkExists(JobKey.jobKey(jobName))) {
                result = true;
            }
        } catch (SchedulerException e) {
            log.error("判断任务异常", e);
        }
        return result;
    }

    public static void modifyJob(JobModel jobModel) throws SchedulerException, ParseException {
        log.info("修改任务:" + jobModel);
        removeJob(jobModel);
        addJob(jobModel);
    }

    public static void start() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            log.info("任务启动失败", e);
        }
    }

    public static void loadTask() {
        log.info("开始加载动态定时任务");
        TaskManager.addJob(generateJobModel("alarmClean", "0 0 2 * * ?", AlarmCleanTask.class));
        TaskManager.addJob(generateJobModel("localSystemInfoClean", "0 0 2 * * ?", LocalSystemInfoCleanTask.class));
        //添加数据库备份任务
        /*DbBackupStrategyService dbBackupStrategyService = SpringUtil.getApplicationContext().getBean(DbBackupStrategyService.class);
        List<DbBackupStrategy> dbBackupStrategyList = dbBackupStrategyService.findByProperty(DbBackupStrategy.class,"strategyStatus", 1);
        if (CollectionUtils.isNotEmpty(dbBackupStrategyList)) {
            dbBackupStrategyList.forEach(dbBackupStrategy -> {
                addBackupTask(dbBackupStrategy);
            });
        }*/
        start();
    }

    private static JobModel generateJobModel(String jobName, String cron, Class cls) {
        JobModel jobModel = new JobModel();
        jobModel.setJobName(jobName);
        jobModel.setCronTime(cron);
        jobModel.setJobClazz(cls);
        return jobModel;
    }


}
