package com.vrv.vap.alarmdeal.business.kafkadeal.alarm;

import com.vrv.vap.alarmdeal.business.kafkadeal.AlarmDealKafkaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author: 梁国露
 * @since: 2023/3/31 15:18
 * @description:
 */
@Component
public class AlarmDealKafkaJob {
    // 日志
    private static Logger logger = LoggerFactory.getLogger(AlarmDealKafkaJob.class);

    @Autowired
    private AlarmDealKafkaService alarmDealKafkaService;

    /**
     * 数据变动监听
     * @param record
     */
    @KafkaListener(topics = "vap_source_change_message")
    public void sourcechange(ConsumerRecord<String,String> record){
        String message = record.value();
        logger.warn("基线数据-sourcechange msg ={}",message);
        if(StringUtils.isNotBlank(message)){
            alarmDealKafkaService.sourceChange(message);
        }
    }

    /**
     * 基础数据变动监听
     * @param record
     */
    @KafkaListener(topics = "vap_base_data_change_message")
    public void basedatachange(ConsumerRecord<String,String> record){
        String message = record.value();
        logger.warn("基础数据-basedatachange msg ={}",message);
        if(StringUtils.isNotBlank(message)){
            alarmDealKafkaService.baseDataChannel(message);
        }
    }

    /**
     * 告警监听
     * @param record
     */
    @KafkaListener(topics = "flink-wiki-demo")
    public void comsumerFlinkAlarmData(ConsumerRecord<String,String> record){
        String message = record.value();
        logger.warn("comsumerFlinkAlarmData msg ={}",message);
        if(StringUtils.isNotBlank(message)){
            alarmDealKafkaService.comsumerFlinkAlarmData(message);
        }
    }
    /**
     * 告警事件对象监听
     * @param record
     */
    @KafkaListener(topics = "event-type-topic")
    public void comsumerEventTypeData(ConsumerRecord<String,String> record){
        String message = record.value();
        logger.warn("comsumerEventTypeData msg ={}",message);
        if(StringUtils.isNotBlank(message)){
            alarmDealKafkaService.comsumerEventTypeData(message);
        }
    }
}
