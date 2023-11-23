package com.vrv.vap.xc.schedule;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.custom.TaskMapper;
import com.vrv.vap.xc.model.JobModel;
import com.vrv.vap.xc.model.TaskModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;
import java.util.Properties;

/**
 * 加载定时任务
 *
 * @author xw
 */
@Component
public class TaskLoader {
    private static final Log LOGGER = LogFactory.getLog(TaskLoader.class);

    private static Scheduler scheduler;

    public static final String TASK_OFFLINE_KEY = "TaskOfflineModel";

    public static final String ID_KEY = "id";

    private volatile int globalId = 0;

    public TaskLoader() {
    }

    public void start() {
        this.registerJob();
        this.run();
    }

    static {
        try {
            Properties props = new Properties();
            props.put("org.quartz.scheduler.instanceName", "static");
            props.put("org.quartz.threadPool.threadCount", "10");
            StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
            stdSchedulerFactory.initialize(props);
            scheduler = stdSchedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 注册任务信息
     */
    private void registerJob() {
        try {
            LOGGER.info("开始加载java定时任务");
            TaskMapper dao = VapXcApplication.getApplicationContext().getBean(TaskMapper.class);
            for (TaskModel taskModel : dao.queryTasks()) {
                if (!"1".equals(taskModel.getShouldRun())) {
                    continue;
                }
                Class<? extends Job> clazz = this.getJobClass(taskModel);
                LOGGER.warn("自定义表schedule_tasks_config中taskModel值：" + taskModel);
                LOGGER.warn("反射类型clazz：" + clazz);
                if (null == clazz) {
                    continue;
                }
                JobDetailImpl job = new JobDetailImpl();
                job.setKey(JobKey.jobKey(clazz.getName(), "java"));
                job.setName(clazz.getName());
                job.setJobClass(clazz);
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put(ID_KEY, String.valueOf(globalId));
                job.setJobDataMap(jobDataMap);

                CronTriggerImpl trigger = new CronTriggerImpl();
                trigger.setKey(TriggerKey.triggerKey(clazz.getName()));
                trigger.setName(clazz.getName());
                trigger.setCronExpression(taskModel.getCronTime());
                LOGGER.warn("register任务：" + taskModel.getTaskName() + "，cron表达式：" + taskModel.getCronTime());
                globalId++;
                scheduler.scheduleJob(job, trigger);
            }
        } catch (SchedulerException | ParseException e) {
            LOGGER.error("自动任务异常！", e);
        }
    }


    /**
     * 执行定时任务
     */
    private void run() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * 获取定时任务的实现类
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Job> getJobClass(TaskModel taskModel) {
        if ("0".equals(taskModel.getShouldRun())) {
            LOGGER.info("not load定时任务,直接返回! " + taskModel);
            return null;
        }
        if (StringUtils.isEmpty(taskModel.getClasspath())) {
            LOGGER.info("定时任务的实现类为空，直接返回! " + taskModel);
            return null;
        }
        try {
            return (Class<? extends Job>) Class.forName(taskModel.getClasspath());
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static void addJob(JobModel jobModel, Map<String, Object> param) {
        if (!jobModel.isOpen()) {
            return;
        }
        try {
            JobDetailImpl job = new JobDetailImpl();
            job.setKey(JobKey.jobKey(jobModel.getJobName()));
            job.setName(jobModel.getJobName());
            job.setJobClass((Class<? extends Job>) Class.forName(jobModel.getJobClazz()));
            JobDataMap jobDataMap = new JobDataMap();
            if (null != param) {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
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
            LOGGER.error(e);
        }
    }

    public static void removeJob(JobModel jobModel) {
        try {
            String jobName = jobModel.getJobName();
            // 先判断是否存在
            if (isExists(jobModel)) {
                scheduler.pauseTrigger(TriggerKey.triggerKey(jobName));
                scheduler.pauseJob(JobKey.jobKey(jobName));
                scheduler.unscheduleJob(TriggerKey.triggerKey(jobName));
                scheduler.deleteJob(JobKey.jobKey(jobName));
            }
        } catch (SchedulerException e) {
            LOGGER.error(e);
        }
    }

    public static boolean isExists(JobModel jobModel) {
        boolean result = false;
        try {
            String jobName = jobModel.getJobName();
            // 先判断是否存在
            if (scheduler.checkExists(JobKey.jobKey(jobName))) {
                result = true;
            }
        } catch (SchedulerException e) {
            LOGGER.error(e);
        }
        return result;
    }

}
