package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.MetricInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import com.vrv.vap.monitor.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Map;
@Slf4j
public class AnalyseMonitorTask extends MonitorBaseTask {
    BaseProperties baseProperties;
    ServerManager serverManager;
    @Override
    public void run(String jobName, JobDataMap jobDataMap) {

        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        serverManager = applicationContext.getBean(ServerManager.class);
        baseProperties = applicationContext.getBean(BaseProperties.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String, MonitorConfig> monitorConfigMap = (Map<String, MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig apiAlarmdeal = monitorConfigMap.get("api-alarmdeal");
        MonitorConfig apiAuditXc = monitorConfigMap.get("api-audit-xc");
        MonitorConfig flink = monitorConfigMap.get("flink");
        log.info("开始监控组件任务：{}，{}，{}",apiAlarmdeal.getName(),apiAuditXc.getName(),flink.getName());
        boolean nacosStatus = ServiceCheckUtil.checkServiceStatus("nacos");
        String localIp = baseProperties.getLocalIp();
        if (nacosStatus){
            if (apiAlarmdeal.getMetric()){
                log.info("开始监控api-alarmdeal");
                MetricInfo metricInfo = buildBaseMetric(apiAlarmdeal,localIp);
                metricInfo.setStatus(1);
                Map<String, Object> connectConfig = apiAlarmdeal.getConnectConfig();
                String url = "http://" + connectConfig.get("SERVER_ADDR") + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + connectConfig.get("NAMESPACE");
                if (!ServiceCheckUtil.checkService(String.format(url, "api-alarmdeal"))){
                    metricInfo.setStatus(0);
                    if (apiAlarmdeal.getAlarm()){
                        pushAlarm(AlarmTypeEnum.ALARM_DEAL.getCode(), AlarmTypeEnum.ALARM_DEAL.getDesc(),localIp,apiAlarmdeal.getName());
                    }
                }
                log.info("组件：{},状态：{}",apiAlarmdeal.getName(),metricInfo.getStatus()==1?"正常":"异常");
                pushMetric(metricInfo);
            }
            if (apiAuditXc.getMetric()){
                log.info("开始监控apiAuditXc");
                MetricInfo metricInfo = buildBaseMetric(apiAuditXc,localIp);
                metricInfo.setStatus(1);
                Map<String, Object> connectConfig = apiAlarmdeal.getConnectConfig();
                String url = "http://" + connectConfig.get("SERVER_ADDR") + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + connectConfig.get("NAMESPACE");
                if (!ServiceCheckUtil.checkService(String.format(url, "api-audit-xc"))){
                    metricInfo.setStatus(0);
                    if (apiAuditXc.getAlarm()){
                        pushAlarm(AlarmTypeEnum.ALARM_AUDIT.getCode(), AlarmTypeEnum.ALARM_AUDIT.getDesc(),localIp,apiAuditXc.getName());
                    }
                }
                log.info("组件：{},状态：{}",apiAuditXc.getName(),metricInfo.getStatus()==1?"正常":"异常");
                pushMetric(metricInfo);
            }
            if (flink.getMetric()){
                log.info("开始监控flink");
                MetricInfo metricInfo = buildBaseMetric(flink,localIp);
                metricInfo.setStatus(1);
                if (!ServiceCheckUtil.checkServiceStatus("flink")){
                    metricInfo.setStatus(0);
                    if (flink.getAlarm()){
                        pushAlarm(AlarmTypeEnum.ALARM_FLINK_SERVER.getCode(), AlarmTypeEnum.ALARM_FLINK_SERVER.getDesc(),localIp,flink.getName());
                    }
                }
                log.info("组件：{},状态：{}",flink.getName(),metricInfo.getStatus()==1?"正常":"异常");
                pushMetric(metricInfo);
            }
        }
    }





    @Override
    public void run(String jobName) {
        run(jobName, null);
    }



    @Override
    public Boolean restart(MonitorConfig config) {
        try {
            ServiceCheckUtil.restartService(config.getName());
        }catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {

        try {
            Thread.sleep(15*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b1 = ServiceCheckUtil.checkServiceStatus(config.getName());
        return b1;
    }
}
