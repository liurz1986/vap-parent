package com.vrv.vap.alarmdeal.business.flow.core.config;

import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessInstanceStatEnum;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.activiti.engine.EngineServices;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalEventListener implements ActivitiEventListener {


	
	
	private LoggerUtil logger = LoggerUtil.getLogger(GlobalEventListener.class);
	List<InstanceEndListener> listeners = new ArrayList<>();
	
	public void register(InstanceEndListener listener) {
		listeners.add(listener);
	}
	
	private void listen(String processInstanceId, BusinessInstanceStatEnum endcanceled) {
		for (InstanceEndListener listener : listeners) {
			listener.end(processInstanceId, endcanceled);
		}
	}
	
	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
		case PROCESS_CANCELLED:
			logger.info(event.getType().name() + " ---- " + event.getProcessInstanceId());
			listen(event.getProcessInstanceId(), BusinessInstanceStatEnum.endCanceled);
			break;
		case PROCESS_COMPLETED:
			// 完成的时候，在从流程实例中获取process_result变量
			logger.info(event.getType().name() + " ---- " + event.getProcessInstanceId());
			EngineServices engineServices = event.getEngineServices();
			Object process_result = engineServices.getRuntimeService().getVariable(event.getExecutionId(), FlowConstant.PROCESSRESULT);
			if(process_result == null || (Boolean) process_result) {
				listen(event.getProcessInstanceId(), BusinessInstanceStatEnum.end);
			} else {
				listen(event.getProcessInstanceId(), BusinessInstanceStatEnum.endFalse);
			}
			break;
		case PROCESS_COMPLETED_WITH_ERROR_END_EVENT:
			logger.info(event.getType().name() + " ---- " + event.getProcessInstanceId());
			listen(event.getProcessInstanceId(), BusinessInstanceStatEnum.endError);
			break;
		case PROCESS_STARTED:
			break;

		default:
			break;
		}
	} 

	@Override
	public boolean isFailOnException() {
		return false;
	}

}
