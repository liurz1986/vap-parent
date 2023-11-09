package com.vrv.vap.monitor.agent.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.flume.cmd.FlumeTools;
import com.vrv.flume.cmd.model.AppState;
import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

import java.util.*;

@Slf4j
public class ReceiveMonitorTask extends MonitorBaseTask {
    private BaseProperties baseProperties;
    private ServerManager serverManager;
    private String ip = System.getenv("LOCAL_SERVER_IP");

    private String workDir = System.getenv("VAP_WORK_DIR");

    private Result collectorResult;

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        serverManager = applicationContext.getBean(ServerManager.class);
        baseProperties = applicationContext.getBean(BaseProperties.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String, MonitorConfig> monitorConfigMap = (Map<String, MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig apiNetflow = monitorConfigMap.get("api-netflow");
        MonitorConfig flume = monitorConfigMap.get("flume");
        //获取组件名
        log.info("开始监控组件任务：{}，{}", apiNetflow.getName(), flume.getName());
//        //获取组件连接信息
        Map<String, Object> apiNetflowConnectConfig = apiNetflow.getConnectConfig();
//        Map<String, Object> flumeConnectConfig = apiNetflow.getConnectConfig();
        //本机ip
        String localIp = baseProperties.getLocalIp();
        //api-netflow监控
        if (apiNetflow.getMetric()) {
            MetricInfo metricInfo = buildBaseMetric(apiNetflow, localIp);
            metricInfo.setStatus(1);
            log.info("开始监控" + "api-netflow");
            try {
                if (!ServiceCheckUtil.checkNetflow("http://" + ip + apiNetflowConnectConfig.get("checkNetflowUrl").toString())) {
                    metricInfo.setStatus(0);
                    if (apiNetflow.getAlarm()) {
                        pushAlarm(AlarmTypeEnum.ALARM_NETFLOW.getCode(), AlarmTypeEnum.ALARM_NETFLOW.getDesc(), localIp, apiNetflow.getName());
                    }

                    if (apiNetflow.getHandler()) {
                        log.info("自动处置异常" + "api-netflow");
                        Boolean dealResult = restartService("api-netflow", localIp, apiNetflowConnectConfig.get("checkNetflowUrl").toString());
                        metricInfo.setStatus(dealResult ? 1 : 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("组件：{},状态：{}", apiNetflow.getName(), metricInfo.getStatus() == 1 ? "正常" : "异常");
            pushMetric(metricInfo);
        }
        //开始监控flume
        if (flume.getMetric()) {
            MetricInfo metricInfo = buildBaseMetric(flume, localIp);
            metricInfo.setStatus(1);
            log.info("开始监控" + "flume");
            Map<String, Object> flumeConnectConfig = flume.getConnectConfig();
            LinkedHashMap<String, String> cids = (LinkedHashMap<String, String>) flumeConnectConfig.get("cids");
            List<String> monitorList = new ArrayList<>();
            for (String s : cids.keySet()) {
                monitorList.add(cids.get(s));
            }
            Map<String, String> cidsStatus = new HashMap<>();
            for (String cid : monitorList) {
                String[] split = cid.split(",");
                String cidValue = split[0];
                String cidName = split[1];
                cidsStatus.put(cidName, "1");
                log.info("====cidValue====:" + cidValue);
                AppState appState = new FlumeTools(workDir + "/flume/flume").status(cidValue);
                if (!appState.isRunning()) {
                    cidsStatus.put(cidName, "0");
                    metricInfo.setStatus(0);
                    if (flume.getAlarm()) {
                        pushAlarm(AlarmTypeEnum.ALARM_FLUME.getCode(), String.format(AlarmTypeEnum.ALARM_FLUME.getDesc(), cidName), localIp, cidName);
                    }
                    //开始处理cid
                    if (flume.getHandler()) {
                        Boolean dealResult = restartCidService(cidValue, localIp, cidName);
                        metricInfo.setStatus(dealResult ? 1 : 0);
                        cidsStatus.put(cidName, dealResult ? "1" : "0");
                    }
                }
            }
            metricInfo.setExtendContent(JsonUtil.objToJson(cidsStatus));
            log.info("组件：{},状态：{},详细：{}", flume.getName(), metricInfo.getStatus() == 1 ? "正常" : "异常", JsonUtil.objToJson(cidsStatus));
            pushMetric(metricInfo);
        }
    }

    private Boolean restartCidService(String cidValue, String localIp, String cidName) {
        pushHandler(AlarmTypeEnum.ALARM_FLUME_DEAL.getCode(), String.format(AlarmTypeEnum.ALARM_FLUME_DEAL.getDesc(), cidName), localIp, cidName, 0);
        Boolean status = false;
        FlumeTools flumeTools = new FlumeTools(workDir + "/flume/flume");
        Result result = flumeCidGet(cidValue);
        if ((result == null || !"0".equals(result.getCode())) && collectorResult != null) {
            log.warn("使用缓存配置数据");
            result = collectorResult;
        }
        log.info("flumeCidGet========" + result);
        if (result.getCode().equals("0") && result.getData() != null) {
            collectorResult = result;
//            CollectorDataAccess collectorDataAccess1=(CollectorDataAccess)result.getData();
//            log.info("collectorDataAccess1========"+collectorDataAccess1);
            ObjectMapper mapper = new ObjectMapper();
            CollectorDataAccess collectorDataAccess = mapper.convertValue(result.getData(), new TypeReference<CollectorDataAccess>() {
            });
            log.info("collectorDataAccess========" + collectorDataAccess);
            Integer initMemory = collectorDataAccess.getInitMemory();
            String encoding = StringUtils.isEmpty(collectorDataAccess.getEncoding()) ? "UTF-8" : collectorDataAccess.getEncoding();
            String jvmOption = "-Djava.security.auth.login.config=" + workDir + "/flume/file/00/kafka_client_jaas.conf" +
                    " -Da1.sources.r1.charsetIn=" + encoding;
            flumeTools.start(cidValue, initMemory, jvmOption, 60000);
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AppState state = flumeTools.status(cidValue);
            if (state.isRunning()) {
                status = true;
                pushHandler(AlarmTypeEnum.ALARM_FLUME_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_FLUME_RESULT.getDesc(), cidName, "成功"), localIp, cidName, 1);
            } else {
                pushHandler(AlarmTypeEnum.ALARM_FLUME_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_FLUME_RESULT.getDesc(), cidName, "失败"), localIp, cidName, 0);
            }

        } else {
            log.error("无法获取服务端数据-接收器明细信息");
        }
        return status;
    }

    private Boolean restartService(String s, String localIp, String url) {
        boolean status = false;
        if (!ServiceCheckUtil.checkServiceStatus("api-netflow")) {
            try {
                pushHandler(AlarmTypeEnum.ALARM_TRY_DEAL.getCode(), AlarmTypeEnum.ALARM_TRY_DEAL.getDesc(), localIp, s, 0);
                Runtime.getRuntime().exec("systemctl restart api-netflow");

                Thread.currentThread().sleep(30 * 1000);

                if (ServiceCheckUtil.checkServiceStatus("api-netflow")) {
                    pushHandler(AlarmTypeEnum.ALARM_DEAL_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_DEAL_RESULT.getDesc(), "成功"), localIp, s, 1);
                    status = true;
                } else {
                    pushHandler(AlarmTypeEnum.ALARM_DEAL_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_DEAL_RESULT.getDesc(), "失败"), localIp, s, 0);
                }
            } catch (Exception e) {
                log.error("api-netflow尝试重启失败");
                pushHandler(AlarmTypeEnum.ALARM_DEAL_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_DEAL_RESULT.getDesc(), "失败"), localIp, s, 0);
            }
        } else {
            pushHandler(AlarmTypeEnum.ALARM_DEAL_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_DEAL_RESULT.getDesc(), "失败"), localIp, s, 0);
        }
        return status;
    }

    @Override
    public void run(String jobName) {
        run(jobName, null);
    }


    @Override
    public Boolean restart(MonitorConfig config) {
        if (config.getName().equals("flume")) {
            return true;
        }
        if (config.getName().equals("api-netflow")) {
            try {
                ServiceCheckUtil.restartService(config.getName());
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
            return true;
        }
        return true;

    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {

        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b1 = ServiceCheckUtil.checkServiceStatus(config.getName());
        return b1;
    }


}
