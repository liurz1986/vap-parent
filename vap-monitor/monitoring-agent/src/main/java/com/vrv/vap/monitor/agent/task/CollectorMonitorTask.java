package com.vrv.vap.monitor.agent.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.MonitorManager;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.agent.utils.TimeTools;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class CollectorMonitorTask extends MonitorBaseTask {
    BaseProperties baseProperties;
    ServerManager serverManager;
    private Result collectorResult;

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {

        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        serverManager=applicationContext.getBean(ServerManager.class);
        baseProperties=applicationContext.getBean(BaseProperties.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String,MonitorConfig> monitorConfigMap=(Map<String,MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig monitorConfig = monitorConfigMap.get("collector");
        //获取组件名
        String name = monitorConfig.getName();
        log.debug("开始监控组件任务：{}",name);
        //获取组件连接信息
        Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
        //连接配置
        Integer interval = Integer.valueOf(connectConfig.get("report-interval").toString());
        //本机ip
        String localIp = baseProperties.getLocalIp();
        MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
        metricInfo.setStatus(1);
        if (serverManager.getServerStateInfo().getStatus()){
            Result result = collectorInfoGet();
            if((result==null || !"0".equals(result.getCode()))&& collectorResult!=null){
                log.warn("使用缓存配置数据");
                result = collectorResult;
            }
            if (result.getCode().equals("0")){
                collectorResult = result;
                List<NetworkMonitor> monitorList1 = (List<NetworkMonitor>)result.getData();
                ObjectMapper mapper = new ObjectMapper();
                List<NetworkMonitor> monitorList= mapper.convertValue(monitorList1, new TypeReference<List<NetworkMonitor>>() { });
//                List<NetworkMonitor> monitorList = JsonUtil.jsonToEntityList(result.getData().toString(), NetworkMonitor.class);
                if (CollectionUtils.isNotEmpty(monitorList)) {
                    for (NetworkMonitor networkMonitor : monitorList) {
                        boolean onlineFlag = false;
                        String deviceId = networkMonitor.getDeviceId();
                        Date reportTime = networkMonitor.getReportTime();
                        if(reportTime!=null) {
                            Date onlineTime = TimeTools.getNowBeforeByMinute(interval);
                            if (onlineTime.getTime() <= reportTime.getTime()) {
                                onlineFlag = true;
                            }
                        }
                        Integer netStatus = onlineFlag?1:0;
                        if(networkMonitor.getNetworkMonitorStatus()==null ||networkMonitor.getNetworkMonitorStatus()!= netStatus){
                            networkMonitor.setNetworkMonitorStatus(netStatus);
                            try {
                                collectorInfoUpdate(networkMonitor);
                            } catch (Exception e) {
                                log.error("collectorInfoUpdate update NetworkMonitor error");
                                e.printStackTrace();
                            }
                        }
                        if (!onlineFlag && networkMonitor.getStatus() == 0) {
                            metricInfo.setStatus(0);
                            if (monitorConfig.getAlarm()){
                                pushAlarm(AlarmTypeEnum.ALARM_MONITOR.getCode(),String.format(AlarmTypeEnum.ALARM_MONITOR.getDesc(), deviceId),localIp,name);
                            }
                        }
                    }
                  metricInfo.setExtendContent(JsonUtil.objToJson(monitorList));
                }
            }else {
                log.error("collectorInfoGet get NetworkMonitor error");
            }
            log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");
            pushMetric(metricInfo);
        }else {
            log.error("CollectorMonitorTask ServerState offline");
        }

    }


    @Override
    public void run(String jobName) {

    }


    @Override
    public Boolean restart(MonitorConfig config) {

        return true;

    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {

        return true;
    }
}
