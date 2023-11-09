package com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.impl;

import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.BusinessInteractionMode;
import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.BusinessParamVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * KAFKA与第三方交互方式的具体实现
 * @author wudi
 * @date 2022/11/16 14:20
 */
@Service
public class KafkaInteractionModeImpl implements BusinessInteractionMode {

      private static Logger logger = LoggerFactory.getLogger(KafkaInteractionModeImpl.class);

      @Resource
      private KafkaTemplate<String, String> kafkaTemplate;


      @Override
      public void interationModeImpl(BusinessParamVO businessParamVO) {
            String topic = businessParamVO.getExtraParamTopic();
            String requestParam = businessParamVO.getRequestParam();
            logger.info("topic:{},requestParam:{}",topic,requestParam);
            kafkaTemplate.send(topic,requestParam);
      }
}
