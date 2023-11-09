package com.vrv.vap.monitor.server.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class NacosListener implements ApplicationListener<EnvironmentChangeEvent> {
    @Autowired
    private ConfigurableEnvironment environment1;
    @Autowired
    AgentManager agentManager;
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent  environment) {
        for (String key : environment.getKeys()) {
            log.info("[onApplicationEvent][key({}) 最新 value 为 {}]", key, environment1.getProperty(key));
        }
        //agent任务重启
        agentManager.restartAllTask();
    }
}
