package com.vrv.vap.xc.schedule;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.custom.TaskMapper;
import com.vrv.vap.xc.model.JobModel;
import com.vrv.vap.xc.model.TaskModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.HashMap;
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
    private String LINE_TASK_CLASS = "com.vrv.vap.xc.schedule.task.BaseLineTask";
    private String JOB_PRE = "baseLineTask-";

    /**
     * 为兼容旧工程下的定时任务类路径而定义的映射列表
     */
    private static final Map<String, String> JOB_CLASS_PATH_MAP = new HashMap<>();

    static {
        JOB_CLASS_PATH_MAP.put("com.vrv.vap.schedular.schedule.task.DataDumpTask", "com.vrv.vap.xc.schedule.task.DataDumpTask");
        JOB_CLASS_PATH_MAP.put("com.vrv.vap.schedular.schedule.task.DatasourceReadTask", "com.vrv.vap.xc.schedule.task.DatasourceReadTask");
        JOB_CLASS_PATH_MAP.put("com.vrv.vap.schedular.schedule.task.DataSendKafkaTask", "com.vrv.vap.xc.schedule.task.DataSendKafkaTask");
    }

    public TaskLoader() {
    }

    public void start() {
        this.registJob();
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
    private void registJob() {
        try {
            /*
            Properties props = new Properties();
            props.put("org.quartz.scheduler.instanceName", "static");
            props.put("org.quartz.threadPool.threadCount", "10");
            StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
            stdSchedulerFactory.initialize(props);

            scheduler = stdSchedulerFactory.getScheduler();
*/
            LOGGER.info("开始加载java定时任务");
            TaskMapper dao = VapXcApplication.getApplicationContext().getBean(TaskMapper.class);
            for (TaskModel taskModel : dao.queryTasks()) {
                if (!"1".equals(taskModel.getShouldRun())) {
                    continue;
                }

                Class<? extends Job> clazz = this.getJobClass(taskModel);

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

                globalId++;

                scheduler.scheduleJob(job, trigger);
            }


        } catch (SchedulerException | ParseException e) {
            LOGGER.error("", e);
        }
/*
        try{
            LOGGER.info("开始加载基线定时任务");
            //注册基线任务
            BaseLineService lineService = VapXcApplication.getApplicationContext().getBean(BaseLineService.class);
            List<BaseLine> lines = lineService.findAll();
            for(BaseLine line : lines){
                if(LineConstants.LINE_STATUS.ENABLE.equals(line.getStatus())){
                    JobModel jobModel = new JobModel();
                    jobModel.setJobName(JOB_PRE + line.getId());
                    jobModel.setCronTime(line.getCron());
                    jobModel.setJobClazz(LINE_TASK_CLASS);
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("id", line.getId());
                    jobModel.setParams(param);
                    TaskLoader.addJob(jobModel,param);
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }*/
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
    private Class<? extends Job> getJobClass(TaskModel bean) {
        if ("0".equals(bean.getShouldRun())) {
            //LOGGER.info("not load " + bean.getDescription());
            return null;
        } else {
            //LOGGER.info("load " + bean.getTaskName() + " -> " + bean.getCronTime());
        }

        try {
            return (Class<? extends Job>) Class.forName(JOB_CLASS_PATH_MAP.getOrDefault(bean.getClasspath(), bean.getClasspath()));
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static void addJob(JobModel jobModel, Map<String, Object> param){
        if (!jobModel.isOpen()) {
            return;
        }
        try {
            JobDetailImpl job = new JobDetailImpl();
            job.setKey(JobKey.jobKey(jobModel.getJobName()));
            job.setName(jobModel.getJobName());
            job.setJobClass((Class<? extends Job>)Class.forName(jobModel.getJobClazz()));
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

    public static void removeJob(JobModel jobModel){
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

    public static boolean isExists(JobModel jobModel){
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
