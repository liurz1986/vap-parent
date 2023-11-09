package com.vrv.vap.alarmdeal.business.analysis.job;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.AlarmDataSaveUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Order(value = 4)
public class AlarmAnalysisEngineJob implements CommandLineRunner {

    private Logger logger= LoggerFactory.getLogger(AlarmAnalysisEngineJob.class);


    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    private static boolean flag = true;

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag) {
                    try {
                        logger.info("同步flink状态开始");
                        if(AlarmDataSaveUtil.runStatus){
                            AlarmDataSaveUtil.runStatus = false;
                            ruleFlinkTypeService.initRiskEventRule();
                            AlarmDataSaveUtil.runStatus = true;
                        }
                        logger.info("同步flink状态结束");
                        TimeUnit.MINUTES.sleep(5);
                    } catch (InterruptedException e) {
                    	flag = false;
                    	Thread.currentThread().interrupt();
                        logger.error("线程中断失败，请检查！", e);
                    }
                }
            }
        }).start();
    }
}
