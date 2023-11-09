package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.service.ServiceApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 同步各模块API接口任务
 */
@Component
public class SyncServiceApiTask {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceApiTask.class);

    @Resource
    private ServiceApiService serviceApiService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void sync() {
        logger.info("Sync service task started");
        serviceApiService.syncServiceApi();
        logger.info("sync service task finished");
    }


}
