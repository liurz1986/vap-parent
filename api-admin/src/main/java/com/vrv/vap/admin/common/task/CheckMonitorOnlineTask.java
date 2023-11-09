package com.vrv.vap.admin.common.task;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.model.NetworkMonitor;
import com.vrv.vap.admin.service.NetworkMonitorReportService;
import com.vrv.vap.admin.service.NetworkMonitorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author lilang
 * @date 2021/8/11
 * @description
 */
@Component
public class CheckMonitorOnlineTask {

    private static final Logger logger = LoggerFactory.getLogger(CheckMonitorOnlineTask.class);

    @Autowired
    NetworkMonitorService networkMonitorService;
    @Autowired
    NetworkMonitorReportService networkMonitorReportService;

    @Value("${vap.zjg.monitor.reportInterval:5}")
    private Integer interval;

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String bootstrapServers;

    @Value("${kafka.userName:admin}")
    private String userName;

    @Value("${kafka.password:vrv@12345}")
    private String password;

    //告警级别：低
    private static final Integer ALARMLEVEL_LOW = 1;

    //告警状态：未处理
    private static final Integer ALARMSTATUS_NO = 0;

    //@Scheduled(cron = "0 0/5 * * * ?")
    public void checkMonitorOnline() {
        List<NetworkMonitor> monitorList = networkMonitorService.findAll();
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
                    networkMonitorService.update(networkMonitor);
                }
                if (!onlineFlag && networkMonitor.getStatus() == 0) {
                    AlarmItem alarmItem = new AlarmItem();
                    alarmItem.setAlarmType("数据告警");
                    alarmItem.setAlarmDesc("设备ID为" + deviceId + "的监视器不在线");
                    alarmItem.setAlarmLevel(ALARMLEVEL_LOW);
                    alarmItem.setAlarmSource("127.0.0.1");
                    alarmItem.setAlarmStatus(ALARMSTATUS_NO);
                    alarmItem.setAlarmTime(new Date());
                    this.send("alarm-item-collection",deviceId, JSON.toJSONString(alarmItem));
                }
            }
        }
    }

    public void send(String topic,String key,String data){
        Producer<String, String> producer = null;
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + userName
                            + "\" password=\"" + password + "\";");

            producer = new KafkaProducer<>(props);
            producer.send(new ProducerRecord<>(topic, key, data), (recordMetadata, e) -> {
                if (e != null){
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
