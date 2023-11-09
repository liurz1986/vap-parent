package com.vrv.vap.alarmdeal.business.kafkadeal.sys;

import com.vrv.vap.alarmdeal.business.kafkadeal.SysAssetKafkaService;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author: 梁国露
 * @since: 2023/4/3 14:19
 * @description:
 */
@Component
public class SysAssetKafkaJob {
    private Logger logger = LoggerFactory.getLogger(SysAssetKafkaJob.class);

    @Autowired
    private SysAssetKafkaService sysAssetKafkaService;


    @KafkaListener(topics = "third-trigger-flow")
    public void thirdTriggerFlowExecute(ConsumerRecord<String,String> record){
        String message = record.value();
        if(StringUtils.isNotBlank(message)){
            sysAssetKafkaService.thirdTriggerFlowExecute(message);
        }

    }

    @KafkaListener(topics = "SuperviseAnnounce")
    public void superviseTaskReceive(ConsumerRecord<String,String> record){
        String message = record.value();
        if(StringUtils.isNotBlank(message)){
            sysAssetKafkaService.superviseTaskReceive(message);
        }
    }

    @KafkaListener(topics = {"sync-base-data-file","sync-base-data-asset","sync-base-data-app"})
    public void assetListen(ConsumerRecord<String,String> record){
        String message = record.value();
        if(StringUtils.isNotBlank(message)){
            sysAssetKafkaService.assetListen(message);
        }
    }


}
