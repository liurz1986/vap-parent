package com.vrv.vap.alarmdeal.business.flow.core.config;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

	@Autowired
	private GlobalEventListener globalEventListener;

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		List<ActivitiEventListener> activitiEventListener = new ArrayList<>();
		activitiEventListener.add(globalEventListener);// 配置全局监听器
		processEngineConfiguration.setEventListeners(activitiEventListener);
	}

}
