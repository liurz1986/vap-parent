package com.vrv.vap.monitor.agent.manager;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.model.JobModel;
import com.vrv.vap.monitor.agent.task.*;
import com.vrv.vap.monitor.agent.task.base.*;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.agent.utils.LogForgingUtil;
import com.vrv.vap.monitor.common.model.LogSendInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import com.vrv.vap.monitor.common.model.RestartInfo;
import com.vrv.vap.monitor.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MonitorManager {

    @Resource
    private BaseProperties baseProperties;

    //组件&监控配置
    private Map<String, MonitorConfig> monitorConfigMap;

    private Boolean configStatus = false;


    public Boolean getConfigStatus() {
        return configStatus;
    }

    public void setConfigStatus(Boolean configStatus) {
        this.configStatus = configStatus;
    }

    public Map<String, MonitorConfig> getMonitorConfigMap() {
        return monitorConfigMap;
    }

    private final static Map<String, Class> taskHandlerClasses = new HashMap<String, Class>();

    private static Scheduler scheduler = null;

    static {
        try {
            taskHandlerClasses.put("DatabaseMonitor", DatabaseMonitorTask.class);
            taskHandlerClasses.put("CollectorMonitor", CollectorMonitorTask.class);
            taskHandlerClasses.put("RedisMonitor", RedisMonitorTask.class);
            taskHandlerClasses.put("KafkaMonitor", KafkaMonitorTask.class);
            taskHandlerClasses.put("ElasticMonitor", ElasticMonitorTask.class);
            taskHandlerClasses.put("ReceiveMonitor", ReceiveMonitorTask.class);
            taskHandlerClasses.put("webMonitor", WebMonitorTask.class);
            taskHandlerClasses.put("AnalyseMonitor", AnalyseMonitorTask.class);
            taskHandlerClasses.put("SystemMonitor", SystemMonitorTask.class);
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


    @Async
    public void updateConfig(List<MonitorConfig> monitorConfigs, boolean open) {
        configStatus = true;
        monitorConfigMap = new HashMap<>();
        monitorConfigs.forEach(p -> {
            monitorConfigMap.put(p.getName(), p);
        });
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        //刷新所有任务信息
        if (open) {
            //删除目前所有任务
            for (String s:taskHandlerClasses.keySet()) {


                    try {
                        boolean b = scheduler.checkExists(JobKey.jobKey(s));
                        if (b){
                            log.info("删除任务"+s);
                            scheduler.deleteJob(JobKey.jobKey(s));
                        }
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }

            }
            if (monitorConfigMap.size() > 0) {
                Map<String, MonitorConfig> collect = monitorConfigMap.entrySet().stream().filter(r -> r.getValue().getMonitorType().equals("one") ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, MonitorConfig> collectMultiple = monitorConfigMap.entrySet().stream().filter(r -> r.getValue().getMonitorType().equals("multiple") ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                //一个任务监控多个组件
                if (collectMultiple.size() > 0) {
                    Map<String, List<Map.Entry<String,MonitorConfig>>>result= collectMultiple.entrySet().stream().collect(Collectors.groupingBy(c -> c.getValue().getModule()));
                    for (String k:result.keySet()) {
                        Map<String,MonitorConfig> monitorConfigMap=new HashMap<>();
                        List<Map.Entry<String, MonitorConfig>> entries = result.get(k);
                        for (Map.Entry<String, MonitorConfig> monitorConfigEntry:entries){
                            monitorConfigMap.put(monitorConfigEntry.getKey(), monitorConfigEntry.getValue());
                        }
                        JobModel jobModel = generateJobModel(entries.get(0).getValue().getModule(), entries.get(0).getValue().getCron(), taskHandlerClasses.get(entries.get(0).getValue().getModule()),monitorConfigMap);
                        //开启状态，修改任务
                        try {
                            modifyJob(jobModel);
                            log.debug("modifyJob{}", jobModel);
                        } catch (SchedulerException e) {
                            log.error("modifyJob error:{}", jobModel);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            log.error("modifyJob error:{}", jobModel);
                            e.printStackTrace();
                        }
                    }
                }
                //一个任务监控一个组件
                if (collect.size()>0){
                    for (String key : collect.keySet()) {
                        MonitorConfig monitorConfig = monitorConfigMap.get(key);
                        Map<String,MonitorConfig> monitorConfigMap=new HashMap<>();
                        monitorConfigMap.put(monitorConfig.getName(),monitorConfig);
                        Boolean metric = monitorConfig.getMetric();
                        JobModel jobModel = generateJobModel(monitorConfig.getModule(), monitorConfig.getCron(), taskHandlerClasses.get(monitorConfig.getModule()),monitorConfigMap);
                        //查看组件监控开关，关
                        if (!metric) {
                            //任务已存在，删除任务
                            removeJob(jobModel);
                            log.debug("removeJob{}", jobModel);
                        } else {
                            //开启状态，修改任务
                            try {
                                modifyJob(jobModel);
                                log.debug("modifyJob{}", jobModel);
                            } catch (SchedulerException e) {
                                log.error("modifyJob error:{}", jobModel);
                                e.printStackTrace();
                            } catch (ParseException e) {
                                log.error("modifyJob error:{}", jobModel);
                                e.printStackTrace();
                            }
                        }
                        
                    }
                }
            }
            MonitorConfig monitorConfig = new MonitorConfig();
            monitorConfig.setCron("0 /10 * * * ?");
            Map<String,MonitorConfig> monitorConfigMap=new HashMap<>();
            monitorConfigMap.put("offlineFileClean",monitorConfig);
            JobModel jobModel = generateJobModel("OfflineFileClean", "0 /10 * * * ?", OfflineFileCleanTask.class,monitorConfigMap);
            try {
                modifyJob(jobModel);
            } catch (SchedulerException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            start();
        }
        //暂停所有任务
        else {
            try {
                scheduler.pauseAll();
                log.debug("pauseAll暂停所有任务");
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        log.debug("[AGENT-CONFIG-UPDATE] info:{}", JsonUtil.objToJson(monitorConfigMap));
    }

    public static void addJob(JobModel jobModel) {
        addJob(jobModel,null);
    }

    public static void resumeJob(String jobName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void addJob(JobModel jobModel, Map<String, Map<String,MonitorConfig>> param) {
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
            if (param == null&&jobModel.getMonitorConfigMap()!=null) {
                param = new HashMap<>();
                param.put("monitorConfig", jobModel.getMonitorConfigMap());
            }
            if (null != param) {
                for (Map.Entry<String, Map<String,MonitorConfig>> entry : param.entrySet()) {
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
            log.error(e + "");
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
            log.error(e + "");
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
            log.error(e + "");
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


    private static JobModel generateJobModel(String jobName, String cron, Class cls,Map<String,MonitorConfig> monitorConfigMap) {
        JobModel jobModel = new JobModel();
        jobModel.setJobName(jobName);
        jobModel.setCronTime(cron);
        jobModel.setJobClazz(cls);
        jobModel.setMonitorConfigMap(monitorConfigMap);
        return jobModel;
    }



    public Result sendLog(LogSendInfo logSendInfo) throws InstantiationException, IllegalAccessException, IOException {
        if(monitorConfigMap.containsKey(logSendInfo.getMonitorName())){
           MonitorConfig config = monitorConfigMap.get(logSendInfo.getMonitorName());
           if(config.getLog()==null || !config.getLog()){
               return Result.builder().code("-1").msg("无日志下载").build();
           }
           MonitorBaseTask baseTask =  (MonitorBaseTask) taskHandlerClasses.get(config.getModule()).newInstance();
            ApplicationContext applicationContext = AgentApplication.getApplicationContext();
            ServerManager serverManager = applicationContext.getBean(ServerManager.class);
            baseTask.setServerManager(serverManager);

           return baseTask.pushLog(logSendInfo);
        }
        return Result.builder().code("-1").msg("组件不存在").build();
    }

    @Async
    public void sendLogSync(LogSendInfo logSendInfo) throws InstantiationException, IllegalAccessException, IOException {
        if(monitorConfigMap.containsKey(logSendInfo.getMonitorName())){
            MonitorConfig config = monitorConfigMap.get(logSendInfo.getMonitorName());
            MonitorBaseTask baseTask =  (MonitorBaseTask) taskHandlerClasses.get(config.getModule()).newInstance();
            ApplicationContext applicationContext = AgentApplication.getApplicationContext();
            ServerManager serverManager = applicationContext.getBean(ServerManager.class);
            baseTask.setServerManager(serverManager);
            baseTask.pushLog(logSendInfo);
        }
    }



    @Async
    public void restartService(RestartInfo restartInfo)  {
        restartInfo.setTime(new Date());
        restartInfo.setMsg("");
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        ServerManager serverManager = applicationContext.getBean(ServerManager.class);
        if(monitorConfigMap.containsKey(restartInfo.getMonitorName())){

            MonitorConfig config = monitorConfigMap.get(restartInfo.getMonitorName());
            if(!config.getRestart()){
                restartInfo.setStatus(0);
                restartInfo.setMsg("组件不能不重启");
                serverManager.sendRestartBack(restartInfo);
                return;
            }
            MonitorBaseTask baseTask = null;
            try {
                baseTask = (MonitorBaseTask) taskHandlerClasses.get(config.getModule()).newInstance();
            } catch (Exception e) {
                restartInfo.setStatus(0);
                restartInfo.setMsg("加载类错误");
                serverManager.sendRestartBack(restartInfo);
                log.error("加载类错误，{}",e.getMessage());
                e.printStackTrace();
                return;
            }

            baseTask.setServerManager(serverManager);
            restartInfo.setStatus(1);
            serverManager.sendRestartBack(restartInfo);
            baseTask.restartService(config,restartInfo);
            return;

        }
        restartInfo.setStatus(0);
        restartInfo.setMsg("组件不存在");
        serverManager.sendRestartBack(restartInfo);
    }


}
