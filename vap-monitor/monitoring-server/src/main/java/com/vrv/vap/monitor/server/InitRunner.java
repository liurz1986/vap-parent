package com.vrv.vap.monitor.server;


import com.vrv.vap.monitor.server.config.MonitorProperties;
import com.vrv.vap.monitor.server.manager.AgentManager;
import com.vrv.vap.monitor.server.task.TaskManager;
import com.vrv.vap.monitor.server.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class InitRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(InitRunner.class);
    @Resource
    AgentManager agentManager;
    @Resource
    MonitorProperties monitorProperties;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        log.info("[SERVER-MONITOR-CONFIG] agent base conf:{}", JsonUtil.objToJson(monitorProperties));
        agentManager.startAgentStatus();
        //开启监控
        TaskManager.loadTask();
    }
}
