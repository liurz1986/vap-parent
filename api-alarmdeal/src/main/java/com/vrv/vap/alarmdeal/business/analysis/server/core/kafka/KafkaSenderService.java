package com.vrv.vap.alarmdeal.business.analysis.server.core.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;


@Component
public class KafkaSenderService {

	private static Logger logger = LoggerFactory.getLogger(KafkaSenderService.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	
	/**
	 * 消息生产者
	 */
	public void send(String topic, String data) {
		ListenableFuture<SendResult<String,String>> listenableFuture = kafkaTemplate.send(topic, data);
		listenableFuture.addCallback(success -> {},
                fail -> logger.error("KafkaMessageProducer 发送消息失败！"));
	}
	
	

}
