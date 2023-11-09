package com.vrv.vap.alarmdeal.business.analysis.server.core.kafka;

import java.util.HashMap;
import java.util.Map;

import com.vrv.vap.alarmdeal.business.analysis.vo.AlarmEventVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.BusinessEvent;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mq.AlarmDealPubRabbitConfiguration;
import com.vrv.vap.jpa.common.UUIDUtils;


/**
 * 告警发送mq
 * @author wd-pc
 *
 */
@Service
public class AlarmDealSenderMQ {
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	private static Gson gson = new Gson();
	
	/**
	 * 发送消息
	 * @param alarmEventVO
	 */
	public void push(AlarmEventVO alarmEventVO) {
		Map<String, Object> payloadBuilder = new HashMap<>();
		payloadBuilder.put("eventType", alarmEventVO.getEventType());
		payloadBuilder.put("ticketNum", alarmEventVO.getTicketNum());
		payloadBuilder.put("name", alarmEventVO.getName());
		payloadBuilder.put("alarmIp", alarmEventVO.getAlarmIp());
		payloadBuilder.put("alarmId",alarmEventVO.getAlarmId());
		BusinessEvent businessEvent = new BusinessEvent();
		businessEvent.setBusinessType(AlarmDealPubRabbitConfiguration.AutoFlowEvent);
		businessEvent.setGuid(UUIDUtils.get32UUID());
		businessEvent.setPayload(gson.toJson(payloadBuilder));
		kafkaTemplate.send(AlarmDealPubRabbitConfiguration.AutoFlowEvent, gson.toJson(businessEvent));
	}
	
	/**
	 * 发送威胁guid
	 * @param map
	 */
	public void push(Map<String,Object> map) {
		Map<String, Object> payloadBuilder = new HashMap<>();
		payloadBuilder.put("threat_Id", map.get("threat_Id").toString()); //威胁guid
		payloadBuilder.put("opt", map.get("opt").toString()); //威胁操作
		BusinessEvent businessEvent = new BusinessEvent();
		businessEvent.setBusinessType(AlarmDealPubRabbitConfiguration.AutoFlowEvent);
		businessEvent.setGuid(UUIDUtils.get32UUID());
		businessEvent.setPayload(gson.toJson(payloadBuilder));
		kafkaTemplate.send(AlarmDealPubRabbitConfiguration.AutoFlowEvent, gson.toJson(businessEvent));
	}
	
	
}
