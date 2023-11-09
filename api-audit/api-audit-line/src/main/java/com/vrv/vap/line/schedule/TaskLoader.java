package com.vrv.vap.line.schedule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.AuditXcClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.line.mapper.StrategyConfigMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.tools.LineMessageTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.util.*;

/**
 * 加载定时任务
 *
 * @author xw
 */
public class TaskLoader {
    private static final Log LOGGER = LogFactory.getLog(TaskLoader.class);
    private AuditXcClient xcClient = VapLineApplication.getApplicationContext().getBean(AuditXcClient.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private StrategyConfigMapper strategyConfigMapper = VapLineApplication.getApplicationContext().getBean(StrategyConfigMapper.class);
    private static Scheduler scheduler;

    public static final String TASK_OFFLINE_KEY = "TaskOfflineModel";

    public static final String ID_KEY = "id";

    private volatile int globalId = 0;
    private String LINE_TASK_CLASS = "com.vrv.vap.line.schedule.task.BaseLineTask";
    private String LINE_RESULT_TASK_CLASS = "com.vrv.vap.line.schedule.task.LineResultTask";
    private String STRATERY_TASK_CLASS = "com.vrv.vap.line.schedule.task.StrategyTask";
    private String JOB_PRE = "baseLineTask-";
    private String STRATERY_JOB_PRE = "strateyTask-";

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
        List<BaseLine> lines = baseLineMapper.selectList(new QueryWrapper<>());
        try{
            LOGGER.info("开始加载基线定时任务");
            //注册基线任务
            //List<BaseLine> lines = xcClient.findAllEnable();
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
                    if(LineConstants.LINE_TYPE.TS.equals(line.getType())){
                        BaseLineSpecial baseLineSpecial = baseLineSpecialMapper.selectById(line.getSpecialId());
                        if(LineConstants.SPECIAL_TYPE.ACTUAL.equals(baseLineSpecial.getType())){
                            //实时类增加监控任务
                            LOGGER.info("添加基线实时任务："+line.getId());
                            JobModel specialJobModel = new JobModel();
                            specialJobModel.setJobName(JOB_PRE + baseLineSpecial.getId());
                            specialJobModel.setCronTime(baseLineSpecial.getActualCron());
                            specialJobModel.setJobClazz(baseLineSpecial.getActualClass());
                            Map<String, Object> p = new HashMap<String, Object>();
                            p.put("id", line.getId());
                            specialJobModel.setParams(p);
                            TaskLoader.addJob(specialJobModel,p);
                        }
                    }
                }
            }
            //增加基线基线失败检测任务
            JobModel jobModel = new JobModel();
            jobModel.setJobName(JOB_PRE + "line-verify");
            jobModel.setCronTime("0 05 0 * * ?");
            jobModel.setJobClazz(LINE_RESULT_TASK_CLASS);
            TaskLoader.addJob(jobModel,new HashMap<String, Object>());
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        try{
            new LineMessageTools().batchSendMessage(lines);
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }

        try{
            LOGGER.info("开始加载策略离线任务");
            List<StrategyConfig> strategyConfigs = strategyConfigMapper.selectList(new QueryWrapper<>());
            for(StrategyConfig strategy : strategyConfigs){
                if(LineConstants.STRATEY_STATUS.ENABLE.equals(strategy.getStatus())){
                    JobModel jobModel = new JobModel();
                    jobModel.setJobName(STRATERY_JOB_PRE + strategy.getId());
                    jobModel.setCronTime(strategy.getCron());
                    jobModel.setJobClazz(STRATERY_TASK_CLASS);
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("id", strategy.getId());
                    jobModel.setParams(param);
                    TaskLoader.addJob(jobModel,param);
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
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
        LOGGER.info("任务新增开始："+ JSONObject.toJSONString(param));
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
        LOGGER.info("任务新增结束："+ JSONObject.toJSONString(param));
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
            e.printStackTrace();
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
