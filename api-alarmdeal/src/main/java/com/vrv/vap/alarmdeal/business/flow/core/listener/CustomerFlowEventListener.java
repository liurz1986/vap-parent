package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.BusinessEvent;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 执行监听器：
 * 开始节点、结束节点、连线  是否发消息
 *
 */
@Service("customerFlowEventListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomerFlowEventListener implements ExecutionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7305890996814032771L;

	private static LoggerUtil logger = LoggerUtil.getLogger(CustomerFlowEventListener.class);

	private FixedValue eventCode;
	@Autowired
	private FlowService flowService;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		Object code = eventCode.getValue(execution);
		if(code == null) {
			logger.warn("定义的GlobalFlowEventListener没有定义eventCode，将无法发送消息。ProcessBusinessKey：" + execution.getProcessBusinessKey());
		} else {
			BusinessIntance intance = flowService.getFlowInstance(execution.getId());
			if(null !=  intance){
				String guid = intance.getGuid();
				sendEvent(code, guid);
			}
		}
	}

	private void sendEvent(Object code, Object busiArgs) {
		Map<String,Object> payloadBuilder = new HashMap<>();
		payloadBuilder.put("eventCode", code.toString());
		payloadBuilder.put("busiArgId", busiArgs);
		BusinessEvent businessEvent = new BusinessEvent();
		businessEvent.setBusinessType("flow-customer-event");
		businessEvent.setGuid(String.valueOf((int)(1+Math.random()*10)));
		businessEvent.setPayload(JSONObject.toJSONString(payloadBuilder));
		logger.info("customerFlowEventListener发送kafka消息："+JSONObject.toJSONString(businessEvent));
		kafkaTemplate.send("flow-customer-event", JSONObject.toJSONString(businessEvent));
	}

}
