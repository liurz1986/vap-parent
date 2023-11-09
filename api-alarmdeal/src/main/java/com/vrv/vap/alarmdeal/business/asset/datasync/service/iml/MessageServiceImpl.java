package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetCsvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据变更发kafka消息：针对资产刷新csv文件
 *
 * 2022-06-01
 */
@Service
public class MessageServiceImpl implements MessageService {
    private static Logger logger= LoggerFactory.getLogger(MessageServiceImpl.class);
    // 发送消息的topic
    public static String topic= "vap_base_data_change_message";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private AssetCsvService assetCsvService;
    @Override
    public void sendKafkaMsg(String item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                 excSendKafkaMsg(item);
            }
        }).start();

    }

    private void excSendKafkaMsg(String item) {
        Map<String,Object> message = new HashMap<String,Object>();
        message.put("item",item);
        message.put("time",System.currentTimeMillis());
        String msg = JSON.toJSONString(message);
        kafkaTemplate.send(topic,msg);
        // 资产的话刷新csv文件
        if("asset".equals(item)){
            assetCsvService.initAssetToCsv();
        }
    }
}
