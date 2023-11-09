package com.vrv.vap.monitor.server.manager;

import com.vrv.vap.monitor.common.enums.CommandType;
import com.vrv.vap.monitor.common.model.*;
import com.vrv.vap.monitor.server.common.util.*;
import com.vrv.vap.monitor.server.config.MonitorProperties;
import com.vrv.vap.monitor.server.model.AlarmItem;
import com.vrv.vap.monitor.server.model.AlarmItemGroup;
import com.vrv.vap.monitor.server.service.AlarmCollectionService;
import com.vrv.vap.monitor.server.service.AlarmItemGroupService;
import com.vrv.vap.monitor.server.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AgentManager {

    @Resource
    MonitorProperties monitorProperties;

    @Resource
    RestTemplate restTemplate;

    private ExecutorService logServiceExec = Executors.newFixedThreadPool(10);
    private Map<String, AgentStateInfo> ipAgentStateInfoMap = new HashMap<>();

    public Map<String, AgentStateInfo> getIpAgentStateInfoMap() {
        return ipAgentStateInfoMap;
    }

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    public Boolean getMysqlStatus() {
        for (String key : ipAgentStateInfoMap.keySet()) {
            AgentStateInfo agentStateInfo = ipAgentStateInfoMap.get(key);
            Map<String, MetricInfo> metricInfoMap = agentStateInfo.getMetricInfoMap();
            if (metricInfoMap != null) {
                MetricInfo mysql = metricInfoMap.get("mysql");
                if (mysql != null && mysql.getStatus() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Async
    public void updateBeat(BeatInfo beatInfo) {
        //接收心跳信息，维护心跳状态
        log.info("[AGENT-BEAT] register agent ip:{},status:{},configStatus:{}", beatInfo.getIp(), beatInfo.getStatus(), beatInfo.getConfigStatus());
        if (!ipAgentStateInfoMap.containsKey(beatInfo.getIp())) {
            AgentStateInfo agentStateInfo = new AgentStateInfo();
            agentStateInfo.setUrl(String.format("http://%s:%d/agent/cmd", beatInfo.getIp(), beatInfo.getPort()));
            agentStateInfo.setMonitorConfigs(getMonitorConfig(beatInfo.getIp()));
            agentStateInfo.setIp(beatInfo.getIp());
            ipAgentStateInfoMap.put(beatInfo.getIp(), agentStateInfo);
        }
        AgentStateInfo agentStateInfo = ipAgentStateInfoMap.get(beatInfo.getIp());
        agentStateInfo.setStatus(beatInfo.getStatus());
        agentStateInfo.setBeatInfo(beatInfo);
        agentStateInfo.setTime(beatInfo.getTime());

        log.info("[AGENT-CONFIG] send config to {},config:{}", beatInfo.getIp(), JsonUtil.objToJson(agentStateInfo.getMonitorConfigs()));
        if (beatInfo.getStatus() == 1 && !beatInfo.getConfigStatus()) {
            //需要发送配置到agent
            sendConfig(agentStateInfo);
        }
    }

    public void restartAllTask() {
        for (String s : ipAgentStateInfoMap.keySet()) {
            AgentStateInfo agentStateInfo = ipAgentStateInfoMap.get(s);
            agentStateInfo.setMonitorConfigs(getMonitorConfig(s));
            sendConfig(agentStateInfo);
        }
    }

    public void sendConfig(AgentStateInfo agentStateInfo) {
        if (agentStateInfo.getStatus() != 1) {
            return;
        }
        Boolean open = monitorProperties.getOpen();
        CommandInfo commandInfo = buildCommandInfo(CommandType.CONFIG, JsonUtil.objToJson(agentStateInfo.getMonitorConfigs()));
        commandInfo.setOpen(open);
        sendAgent(agentStateInfo, commandInfo);
    }

    public void sendConfigs() {
        for (AgentStateInfo agentStateInfo : ipAgentStateInfoMap.values()) {
            sendConfig(agentStateInfo);
        }
    }

    public Result downloadLog(HttpServletResponse response, String serviceName) {
        List<LogSendInfo> logSendInfos = new ArrayList<>();
        String taskId = UUID.randomUUID().toString();
        String dirName = serviceName.replace("-", "_") + "_" + TimeTools.formatTimeStamp(new Date());
        String tempPath = Paths.get(monitorProperties.getLogFilePath(), dirName).toString();

        Optional<MonitorConfig> confOpt = monitorProperties.getComponents().values().stream().filter(p -> p.getName().equals(serviceName)).findFirst();
        if (!confOpt.isPresent()) {
            return Result.builder().code("-1").msg("配置信息不存在").build();
        }
        if (confOpt.get().getLog() == null || confOpt.get().getLog() == false) {
            return Result.builder().code("-1").msg("该组件无日志下载").build();
        }
        //根据组件名称，获取日志信息
        ipAgentStateInfoMap.values().forEach(agentStateInfo -> {
            Optional<MonitorConfig> configOptional = monitorProperties.getComponents().values().stream().filter(p -> p.getName().equals(serviceName)).findFirst();
            if (configOptional.isPresent()) {
                LogSendInfo logSendInfo = new LogSendInfo();
                logSendInfo.setIp(agentStateInfo.getIp());
                logSendInfo.setMonitorName(configOptional.get().getName());
                logSendInfo.setLogFileName(dirName + "_" + agentStateInfo.getIp() + ".log");
                logSendInfo.setTempDir(tempPath);
                logSendInfo.setFilePath(configOptional.get().getLogAddress());
                logSendInfo.setTime(new Date());
                logSendInfo.setTaskId(taskId);
                logSendInfo.setType("1");
                logSendInfo.setIp(agentStateInfo.getIp());
                logSendInfos.add(logSendInfo);

                Callable callable = new Callable<Result>() {
                    @Override
                    public Result call() throws Exception {
                        CommandInfo commandInfo = buildCommandInfo(CommandType.SEND_LOG, JsonUtil.objToJson(logSendInfo));
                        return sendAgent(agentStateInfo, commandInfo);
                    }
                };
                Future<Result> future = logServiceExec.submit(callable);
                try {
                    log.info("返回内容:{}", JsonUtil.objToJson(future.get()));
                } catch (Exception e) {
                    log.error("日志下载失败1", e);
                }
            }
        });
        //下载文件，如果是多个，组成zip包
        if (logSendInfos.size() == 0) {
            return Result.builder().code("-1").msg("主机不在线或无响应服务").build();
        }
        if (logSendInfos.size() == 1) {
            String path = Paths.get(tempPath, logSendInfos.get(0).getLogFileName()).toString();
            File logFile = new File(path);
            if (logFile.exists()) {
                FileUtils.downloadFile(path, response);
                return null;
            }
            return Result.builder().code("-1").msg("日志下载失败").build();
        }
        //
        String distPath = Paths.get(monitorProperties.getLogFilePath(), dirName + ".zip").toString();
        try {
            ZipUtil.toZip(tempPath, distPath, true);
            //删除原先文件夹
            File dir = new File(tempPath);
            if (dir.exists() && dir.isDirectory()) {
                File[] listFiles = dir.listFiles();
                for (File file : listFiles) {
                    System.out.println("Deleting " + file.getName());
                    file.delete();
                }
                dir.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.builder().code("-1").msg("压缩日志文件失败").build();
        }

        //使用zip方法生成
        File logFile = new File(distPath);
        if (logFile.exists()) {
            FileUtils.downloadFile(distPath, response);
            return null;
        }
        return Result.builder().code("-1").msg("日志下载失败").build();

    }


    public Result restartService(String serviceName) {
        //根据组件名称，获取日志信息
        ipAgentStateInfoMap.values().forEach(agentStateInfo -> {
            Optional<MonitorConfig> configOptional = agentStateInfo.getMonitorConfigs().stream().filter(p -> p.getName().equals(serviceName)).findFirst();
            if (configOptional.isPresent()) {
                RestartInfo restartInfo = new RestartInfo();
                restartInfo.setIp(agentStateInfo.getIp());
                restartInfo.setMonitorName(configOptional.get().getName());
                restartInfo.setTime(new Date());
                restartInfo.setStatus(0);
                restartInfo.setIp(agentStateInfo.getIp());
                CommandInfo commandInfo = buildCommandInfo(CommandType.RESTART, JsonUtil.objToJson(restartInfo));
                sendAgent(agentStateInfo, commandInfo);
            }
        });
        return Result.builder().code("0").build();
    }


    @Async
    public void updateMetric(MetricInfo metricInfo) {
        if (metricInfo == null || !ipAgentStateInfoMap.containsKey(metricInfo.getIp())) {
            log.error("[AGENT-METRIC] 指标信息为空或监控Agent不存在,metric:{}", metricInfo);
        }
        AgentStateInfo agentStateInfo = ipAgentStateInfoMap.get(metricInfo.getIp());
        if (agentStateInfo.getMetricInfoMap() == null) {
            agentStateInfo.setMetricInfoMap(new HashMap<>());
        }
        if (metricInfo.getStatus() == 3 && agentStateInfo.getRestartInfoMap() != null && agentStateInfo.getRestartInfoMap().containsKey(metricInfo.getMonitorName())) {
            //重启中，判断超过5分钟还未重启成功，则使用metric值信息
            RestartInfo restartInfo = agentStateInfo.getRestartInfoMap().get(metricInfo.getMonitorName());
            Long interval = DateUtil.getTime(restartInfo.getTime(), new Date());
            if (interval > monitorProperties.getMaxRestartWait()) {
                agentStateInfo.getMetricInfoMap().put(metricInfo.getMonitorName(), metricInfo);
            }
        } else {
            agentStateInfo.getMetricInfoMap().put(metricInfo.getMonitorName(), metricInfo);
        }
        if (metricInfo.getStatus() != 1) {
            log.info("[AGENT-METRIC-STATUS] status error,ip:{},name:{}", metricInfo.getIp(), metricInfo.getMonitorName());
        }
    }

    @Async
    public void updateAlarm(AlarmInfo alarmInfo) {
        AlarmItem alarmItem = new AlarmItem();
        alarmItem.setAlarmType(alarmInfo.getType());
        alarmItem.setAlarmLevel(alarmInfo.getLevel());
        alarmItem.setAlarmSource(alarmInfo.getIp());
        alarmItem.setAlarmDesc(alarmInfo.getDesc());
        alarmItem.setAlarmStatus(alarmInfo.getStatus());
        alarmItem.setOriginData(alarmInfo.getExtendContent());
        alarmItem.setAlarmTime(alarmInfo.getTime());
        alarmItem.setAlarmName(alarmInfo.getMonitorName());
        AlarmCollectionService bean = SpringContextUtil.getApplicationContext().getBean(AlarmCollectionService.class);
        bean.save(alarmItem);
        AlarmItemGroup alarmItemGroup = new AlarmItemGroup();
        alarmItemGroup.setAlarmType(alarmItem.getAlarmType());
        alarmItemGroup.setAlarmLevel(alarmItem.getAlarmLevel());
        alarmItemGroup.setAlarmSource(alarmItem.getAlarmSource());
        alarmItemGroup.setAlarmDesc(alarmItem.getAlarmDesc());
        AlarmItemGroupService alarmItemGroupService = SpringContextUtil.getApplicationContext().getBean(AlarmItemGroupService.class);
        AlarmItemGroup group = alarmItemGroupService.findOne(alarmItemGroup);
        if (group == null) {
            alarmItemGroupService.save(alarmItemGroup);
        }
    }

    @Async
    public void updateHandler(HandlerInfo handlerInfo) {
        AlarmItem alarmItem = new AlarmItem();
        alarmItem.setAlarmType(handlerInfo.getType());
        alarmItem.setAlarmLevel(handlerInfo.getLevel());
        alarmItem.setAlarmSource(handlerInfo.getIp());
        alarmItem.setAlarmDesc(handlerInfo.getDesc());
        alarmItem.setAlarmStatus(handlerInfo.getStatus());
        alarmItem.setOriginData(handlerInfo.getExtendContent());
        alarmItem.setAlarmTime(handlerInfo.getTime());
        alarmItem.setAlarmName(handlerInfo.getMonitorName());
        if (alarmItem.getAlarmStatus() == 1) {
            alarmItem.setUpdateTime(new Date());
        }
        AlarmCollectionService bean = SpringContextUtil.getApplicationContext().getBean(AlarmCollectionService.class);
        bean.save(alarmItem);
        AlarmItemGroup alarmItemGroup = new AlarmItemGroup();
        alarmItemGroup.setAlarmType(alarmItem.getAlarmType());
        alarmItemGroup.setAlarmLevel(alarmItem.getAlarmLevel());
        alarmItemGroup.setAlarmSource(alarmItem.getAlarmSource());
        alarmItemGroup.setAlarmDesc(alarmItem.getAlarmDesc());
        AlarmItemGroupService alarmItemGroupService = SpringContextUtil.getApplicationContext().getBean(AlarmItemGroupService.class);
        AlarmItemGroup group = alarmItemGroupService.findOne(alarmItemGroup);
        if (group == null) {
            alarmItemGroupService.save(alarmItemGroup);
        }
    }

    @Async
    public void updateRestart(RestartInfo restartInfo) {
        if (restartInfo == null || !ipAgentStateInfoMap.containsKey(restartInfo.getIp())) {
            log.error("[AGENT-RESTART] 指标信息为空或监控Agent不存在,info:{}", restartInfo);
        }

        AgentStateInfo agentStateInfo = ipAgentStateInfoMap.get(restartInfo.getIp());
        if (agentStateInfo.getMetricInfoMap() == null) {
            log.error("[AGENT-RESTART] 指标信息为空或监控Agent不存在2,info:{}", restartInfo);
            return;
        }
        if (agentStateInfo.getRestartInfoMap() == null) {
            agentStateInfo.setRestartInfoMap(new HashMap<>());
        }
        agentStateInfo.getRestartInfoMap().put(restartInfo.getMonitorName(), restartInfo);
        MetricInfo metricInfo = agentStateInfo.getMetricInfoMap().get(restartInfo.getMonitorName());
        if (restartInfo.getStatus() == 1) {
            metricInfo.setStatus(3);
            return;
        }
        if (restartInfo.getStatus() == 3) {
            metricInfo.setStatus(1);
            return;
        }
        if (restartInfo.getStatus() == 2) {
            metricInfo.setStatus(0);
            return;
        }
        if (restartInfo.getStatus() == 0) {
            log.error("[AGENT-RESTART-STATUS] status : 未执行重启 {}", JsonUtil.objToJson(restartInfo));
        }

    }

    public List<MonitorConfig> getMonitorConfig(String ip) {
//        List<MonitorConfig> monitorConfigs = monitorProperties.getComponents().values().stream().filter(p->p.getNodes()!=null && p.getNodes().contains(ip) && p.getMetric()).collect(Collectors.toList());
        //开关Metric信息传入agent方便关闭需要关闭得已在运行得任务
        List<MonitorConfig> monitorConfigs = monitorProperties.getComponents().values().stream().filter(p -> p.getNodes() != null && p.getNodes().contains(ip)).collect(Collectors.toList());
        return monitorConfigs;
    }


    private CommandInfo buildCommandInfo(CommandType commandType, String info) {
        CommandInfo commandInfo = new CommandInfo();
        commandInfo.setCommandBody(info);
        commandInfo.setIp(monitorProperties.getLocalIp());
//        commandInfo.setToken(monitorProperties.getToken());
        commandInfo.setTime(new Date());
        commandInfo.setCommandType(commandType);
        return commandInfo;
    }

    /**
     * 发送命令到agent
     *
     * @param agentStateInfo
     * @param commandInfo
     * @return
     */
    private Result sendAgent(AgentStateInfo agentStateInfo, CommandInfo commandInfo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        httpHeaders.setContentType(type);
        HttpEntity<CommandInfo> objectHttpEntity = new HttpEntity<>(commandInfo, httpHeaders);
        log.info("[SERVER-SEND] sendAgent, url:{}, entity:{}", agentStateInfo.getUrl(), objectHttpEntity);
        log.info("[SERVER-SEND] commandInfo:{}", JsonUtil.objToJson(commandInfo));
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result> responseResultResponseEntity = restTemplate.postForEntity(agentStateInfo.getUrl(), objectHttpEntity, Result.class);
        return responseResultResponseEntity.getBody();
    }

    public void startAgentStatus() {
        executorService.execute(new BeatStatusTask());
        executorService.execute(new LogFileCleanTask());
    }

    class BeatStatusTask implements Runnable {
        @Override
        public void run() {
            if (ipAgentStateInfoMap != null) {
                ipAgentStateInfoMap.values().forEach(p -> {
                    //判定是否超过下线事件
                    Long interval = DateUtil.getTime(p.getTime(), new Date());
                    if (interval > monitorProperties.getOfflineInterval()) {
                        p.setStatus(0);
                        log.info("[AGENT-OFFLINE] agent ip:{},no beat interval:{} ms", p.getIp(), interval);
                    }
                    log.info("[AGENT-LINE-INFO]:agentIP:" + p.getIp() + ",agentStatus:" + p.getStatus() + ", url:" + p.getUrl());
                });
            }


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executorService.execute(new BeatStatusTask());
        }
    }

    class LogFileCleanTask implements Runnable {
        @Override
        public void run() {
            //遍历所有的日志下载文件夹
            File logDir = new File(monitorProperties.getLogFilePath());
            if (logDir.exists() && logDir.isDirectory()) {
                File[] logFiles = logDir.listFiles();
                long timeStmp = new Date().getTime();
                for (File logFile : logFiles) {
                    try {
                        long cTime = FileUtils.getFileCreateTime(logFile.getAbsolutePath());
                        //超过24小时则删除文件
                        if ((timeStmp - cTime) > 24 * 60 * 60000) {
                            if (logFile.isDirectory()) {
                                File[] listFiles = logFile.listFiles();
                                for (File file : listFiles) {
                                    log.info("Deleting " + file.getName());
                                    file.delete();
                                }
                            }
                            logFile.delete();
                        }
                    } catch (Exception exception) {
                        log.error(exception.getMessage());
                    }
                }
            }
            try {
                Thread.sleep(60 * 60000);
            } catch (InterruptedException e) {
                log.error("agentManager出错了！", e);
            }
            executorService.execute(new LogFileCleanTask());
        }
    }

}
