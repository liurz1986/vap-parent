package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.service.AlarmCollectionService;
import com.vrv.vap.admin.model.EsServerStatusModel;
import com.vrv.vap.admin.service.StatusService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.quartz.JobDataMap;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ServiceMonitorTask extends BaseTask {
    private static Logger log = LoggerFactory.getLogger(ServiceMonitorTask.class);

    //告警级别-低
    private static final Integer ALARM_GRADE_LOW = 1;

    private StatusService statusService = SpringContextUtil.getApplicationContext().getBean(StatusService.class);

    private AlarmCollectionService alarmCollectionService = SpringContextUtil.getApplicationContext().getBean(AlarmCollectionService.class);

    @Override
    void run(String jobName, JobDataMap jobDataMap) {
        Map<String,Object> esInfo = statusService.getEsClusterInfo();
        if (esInfo == null) {
            pushInfo("服务告警", "存储模块状态异常");
        }
        List<EsServerStatusModel> list = (List<EsServerStatusModel>) esInfo.get("extends");
        if (CollectionUtils.isNotEmpty(list)) {
            EsServerStatusModel esServerStatusModel = list.get(0);
            String esStatus = esServerStatusModel.getStatus();
            if ("red".equals(esStatus) || "none".equals(esStatus)) {
                pushInfo("服务告警", "存储模块状态异常");
            }

            double warnValue = Double.parseDouble((String) jobDataMap.get("warnValue"));
            if (esServerStatusModel.getDiskUsed()/esServerStatusModel.getDiskAll() >= warnValue) {
                pushInfo("磁盘告警", "磁盘使用率过高");
            }
        } else {
            pushInfo("服务告警", "存储模块状态异常");
        }

        if ((Integer) statusService.getLogStashInfo().get("status") == 1) {
            pushInfo("服务告警", "采集模块状态异常");
        }

        if ((Integer) statusService.extractKafkaData().get("status") == 1) {
            pushInfo("服务告警", "分发模块状态异常");
        }
    }

    /*private void pushInfo(int contentType, String title) {
//        MessageVo messageVo = new MessageVo();
//        messageVo.setContent(getContent(contentType));
//        messageVo.setTitle(title);
//        messageVo.setUserId(31);
//        pushInfoClient.pushMessage(messageVo);
        Message message = new Message();
        message.setContent(getContent(contentType));
        message.setTitle(title);
        message.setUrl("");
        message.setUserId(31);
        message.setAlarmGrade(ALARM_GRADE_LOW);
        pushController.pushMessage(message);
        *//*Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("content", getContent(contentType));
        paramMap.put("title", title);
        paramMap.put("url", "");
        paramMap.put("userId", 31);
        paramMap.put("alarmGrade", ALARM_GRADE_LOW);
        String requestParam = JSON.toJSONString(paramMap);
        log.info("pushParam:" + requestParam);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {
            String pushUrl = statusService.getPushUrl();
            log.info("pushUrl:" + pushUrl);
            HTTPUtil.PUT(pushUrl, headers, requestParam);
        } catch (Exception e) {
            e.printStackTrace();
        }*//*
    }*/

    private void pushInfo(String alarmType, String alarmDesc) {
        AlarmItem alarmItem = new AlarmItem();
        alarmItem.setAlarmType(alarmType);
        alarmItem.setAlarmLevel(ALARM_GRADE_LOW);
        alarmItem.setAlarmSource("127.0.0.1");
        alarmItem.setAlarmDesc(alarmDesc);
        alarmItem.setAlarmStatus(0);
        alarmItem.setAlarmTime(new Date());
        alarmCollectionService.save(alarmItem);
    }

    private String getContent(int contentType) {
        switch (contentType) {
            case 1:
                return "存储模块健康状态异常！请前往“平台状态监控”功能模块恢复存储服务。";
            case 2:
                return "数据存储磁盘使用率过高！请尽快进行数据备份或者清理磁盘空间。";
            case 3:
                return "采集模块健康状态异常！请前往“平台状态监控”功能模块恢复采集服务。";
            case 4:
                return "分发模块健康状态异常！请前往“平台状态监控”功能模块恢复分发服务。";
            default:
                return null;
        }
    }

    @Override
    void run(String jobName) {
        this.run(jobName, null);
    }
}
