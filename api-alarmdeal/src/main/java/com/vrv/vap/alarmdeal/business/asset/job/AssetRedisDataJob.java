package com.vrv.vap.alarmdeal.business.asset.job;

import com.vrv.vap.alarmdeal.business.asset.service.BaseDataRedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 基础数据redis缓存：资产、应用系统
 *
 * 2023-10-23
 */
@Configuration
@EnableScheduling
@Order(value = 8)
public class AssetRedisDataJob implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(AssetRedisDataJob.class);
    @Autowired
    private BaseDataRedisCacheService baseDataRedisCacheService;
    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("项目启动更新资产、应用相关redis缓存");
                baseDataRedisCacheService.initCache(true);
            }
        }).start();
    }

    /**
     * 定时更新基础数据redis缓存
     * 每10分钟更新一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void updateBaseDataRedisCache(){
        logger.warn("定时更新资产、应用相关redis缓存开始");
        baseDataRedisCacheService.initCache(true);
        logger.warn("定时更新资产、应用相关redis缓存结束");
    }
}
