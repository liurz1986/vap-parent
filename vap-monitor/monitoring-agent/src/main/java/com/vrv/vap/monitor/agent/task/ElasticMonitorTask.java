package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.ESClient;
import com.vrv.vap.monitor.agent.manager.MonitorManager;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.AlarmInfo;
import com.vrv.vap.monitor.common.model.MetricInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ElasticMonitorTask extends MonitorBaseTask {
    private BaseProperties baseProperties;
    ServerManager serverManager;

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        baseProperties=applicationContext.getBean(BaseProperties.class);
        serverManager=applicationContext.getBean(ServerManager.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String,MonitorConfig> monitorConfigMap=(Map<String,MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig monitorConfig = monitorConfigMap.get("elasticsearch");
        String monitorName = monitorConfig.getName();
        log.debug("开始监控组件任务：{}",monitorName);
        Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
        long l = System.currentTimeMillis();
        String localIp = baseProperties.getLocalIp();
        GetResponse documentFields = null;
        RestHighLevelClient instance = ESClient.getInstance(connectConfig);
        MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
        metricInfo.setStatus(1);
        try {
            //测试索引写数据
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("test", l);
            IndexRequest indexRequest = new IndexRequest("monitortest")
                    .id("1").source(jsonMap);
            IndexResponse index = instance.index(indexRequest, RequestOptions.DEFAULT);
            String result = index.getResult().toString();
            if (!(index != null && (result.equals("UPDATED") || result.equals("CREATED")))) {
                metricInfo.setStatus(0);
                if (monitorConfig.getAlarm()) {
                    pushAlarm(AlarmTypeEnum.ALARM_ES_WRITE.getCode(), AlarmTypeEnum.ALARM_ES_WRITE.getDesc(), localIp, monitorName);
                }
                if (monitorConfig.getHandler()) {
                    Boolean elasticsearch = dealEsAlram("elasticsearch", monitorConfig, localIp);
                    metricInfo.setStatus(elasticsearch ? 1 : 0);
                }
            }
            //测试索引读数据
            GetRequest getRequest = new GetRequest("monitortest", "1");
            documentFields = instance.get(getRequest, RequestOptions.DEFAULT);
            if (documentFields == null && !documentFields.getSourceAsMap().get("test").equals(l)) {
                //组件异常
                metricInfo.setStatus(0);
                if (monitorConfig.getAlarm()) {
                    pushAlarm(AlarmTypeEnum.ALARM_ES_READ.getCode(), AlarmTypeEnum.ALARM_ES_READ.getDesc(), localIp, monitorName);
                }
                if (monitorConfig.getHandler()) {
                    Boolean elasticsearch = dealEsAlram("elasticsearch", monitorConfig, localIp);
                    metricInfo.setStatus(elasticsearch ? 1 : 0);
                }
            }
            //
        } catch (IOException e) {
            metricInfo.setStatus(0);
            if (monitorConfig.getAlarm()) {
                pushAlarm(AlarmTypeEnum.ALARM_ES_LINK.getCode(), AlarmTypeEnum.ALARM_ES_LINK.getDesc(), localIp, monitorName);
            }
            if (monitorConfig.getHandler()) {
                Boolean elasticsearch = dealEsAlram("elasticsearch", monitorConfig, localIp);
                metricInfo.setStatus(elasticsearch ? 1 : 0);
            }
            e.printStackTrace();
        }
        log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");
        pushMetric(metricInfo);

    }

    private Boolean dealEsAlram(String elasticsearch, MonitorConfig config, String localIp) {
        boolean status = false;
        //查询es状态
        boolean b = ServiceCheckUtil.checkServiceStatus(elasticsearch);
        if (!b) {
            pushHandler(AlarmTypeEnum.ALARM_ES_DEAL.getCode(), AlarmTypeEnum.ALARM_ES_DEAL.getDesc(), localIp, elasticsearch, 0);
            ServiceCheckUtil.restartService(elasticsearch);
            if(checkRestartStatus(config)){
                pushHandler(AlarmTypeEnum.ALARM_ES_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_ES_RESULT.getDesc(), "成功"), localIp, elasticsearch, 1);
                status = true;
            }else {
                pushHandler(AlarmTypeEnum.ALARM_ES_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_ES_RESULT.getDesc(), "失败"), localIp, elasticsearch, 0);
            }
        }
        return status;
    }

    @Override
    public void run(String jobName) {
      run(jobName,null);
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
        Map<String, Object> connectConfig = config.getConnectConfig();
        RestHighLevelClient instance = ESClient.getInstance(connectConfig);
        //再次检测
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Map<String, Object> jsonMap = new HashMap<>();
            long l = System.currentTimeMillis();
            jsonMap.put("test", l);
            IndexRequest indexRequest = new IndexRequest("monitortest")
                    .id("1").source(jsonMap);
            IndexResponse index = null;
            index = instance.index(indexRequest, RequestOptions.DEFAULT);
            String result = index.getResult().toString();
            if (index != null &&(result.equals("UPDATED") || result.equals("CREATED"))){
                return true;
            }else {
                return false;
            }
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }
    }

}
