package com.vrv.vap.monitor.agent;


import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.BeatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class InitRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InitRunner.class);


    @Resource
    BeatManager beatManager;

    @Resource
    BaseProperties baseProperties;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        log.info("[AGENT-CONFIG] agent base conf:{}",baseProperties);
        beatManager.startBeat();
    }
}
