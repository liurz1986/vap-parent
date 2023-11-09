package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.MetricInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

import java.util.*;

@Slf4j
public class WebMonitorTask extends MonitorBaseTask {
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
        Iterator iterator = monitorConfigMap.keySet().iterator();
        List<MonitorConfig> webGroupList = new ArrayList<>();
        while (iterator.hasNext()) {
            String serverName = (String) iterator.next();
            MonitorConfig webServer = monitorConfigMap.get(serverName);
            if ("web".equals(webServer.getGroup()) && !"nacos".equals(webServer.getName())) {
                webGroupList.add(webServer);
            }
        }
        MonitorConfig nacos = monitorConfigMap.get("nacos");
        log.info("开始监控组件任务：{}",nacos.getName());
        boolean nacosStatus = ServiceCheckUtil.checkServiceStatus("nacos");
        String localIp = baseProperties.getLocalIp();
        if (nacos.getMetric()) {
            log.info("开始监控nacos");
            MetricInfo metricInfo = buildBaseMetric(nacos,localIp);
            metricInfo.setStatus(1);
            if (!nacosStatus) {
                metricInfo.setStatus(0);
                if (nacos.getAlarm()) {
                    pushAlarm(AlarmTypeEnum.ALARM_WEB_NACOS.getCode(), AlarmTypeEnum.ALARM_WEB_NACOS.getDesc(), localIp, nacos.getName());
                }
                if (nacos.getHandler()) {
                    pushHandler(AlarmTypeEnum.ALARM_WEB_NACOS_DEAL.getCode(), AlarmTypeEnum.ALARM_WEB_NACOS_DEAL.getDesc(), localIp, nacos.getName(), 0);
                    ServiceCheckUtil.restartService(nacos.getName());
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ServiceCheckUtil.checkServiceStatus("nacos")) {
                        pushHandler(AlarmTypeEnum.ALARM_WEB_NACOS_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_WEB_NACOS_RESULT.getDesc(), "成功"), localIp, nacos.getName(), 1);
                        metricInfo.setStatus(1);
                    } else {
                        pushHandler(AlarmTypeEnum.ALARM_WEB_NACOS_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_WEB_NACOS_RESULT.getDesc(), "失败"), localIp, nacos.getName(), 0);
                    }
                }
            }
            log.info("组件：{},状态：{}",nacos.getName(),metricInfo.getStatus()==1?"正常":"异常");
            pushMetric(metricInfo);
        }
        //nacos状态正常是监控其他服务
        if (nacosStatus) {
            if (CollectionUtils.isNotEmpty(webGroupList)) {
                for (MonitorConfig monitorConfig : webGroupList) {
                    //web服务监控
                    if (monitorConfig.getMetric()) {
                        MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
                        metricInfo.setStatus(1);
                        Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
                        String url = "http://" + connectConfig.get("SERVER_ADDR") + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + connectConfig.get("NAMESPACE");
                        if (!ServiceCheckUtil.checkService(String.format(url, monitorConfig.getName()))) {
                            metricInfo.setStatus(0);
                            if (monitorConfig.getAlarm()) {
                                pushAlarm(AlarmTypeEnum.ALARM_WEB_ADMIN.getCode(), String.format(AlarmTypeEnum.ALARM_WEB_ADMIN.getDesc(), monitorConfig.getName()), localIp, monitorConfig.getName());
                            }
                            if (monitorConfig.getHandler()) {
                                pushHandler(AlarmTypeEnum.ALARM_WEB_ADMIN_DEAL.getCode(), String.format(AlarmTypeEnum.ALARM_WEB_ADMIN_DEAL.getDesc(), monitorConfig.getName()), localIp, monitorConfig.getName(),0);
                                Boolean b=dealServer(monitorConfig.getName());
                                if (b){
                                    pushHandler(AlarmTypeEnum.ALARM_WEB_ADMIN_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_WEB_ADMIN_RESULT.getDesc(),monitorConfig.getName(),"成功"),localIp, monitorConfig.getName(),1);
                                    metricInfo.setStatus(1);
                                }else {
                                    pushHandler(AlarmTypeEnum.ALARM_WEB_ADMIN_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_WEB_ADMIN_RESULT.getDesc(),monitorConfig.getName(),"失败"),localIp, monitorConfig.getName(),0);
                                }
                            }
                        }
                        log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");
                        pushMetric(metricInfo);
                    }
                }
            }
        }
    }

    private Boolean dealServer(String name) {
        boolean b = ServiceCheckUtil.checkServiceStatus(name);
        if (!b){
            ServiceCheckUtil.restartService(name);
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //nacos验证时间较长改用checkServiceStatus
//                    String url = "http://" + nacosAddr + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + namespace;
//                    boolean b1 = ServiceCheckUtil.checkService(String.format(url, servers));
            boolean b1 = ServiceCheckUtil.checkServiceStatus(name);
            return b1;
        }
        return false;
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
