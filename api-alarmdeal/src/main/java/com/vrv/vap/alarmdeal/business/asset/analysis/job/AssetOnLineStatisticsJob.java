package com.vrv.vap.alarmdeal.business.asset.analysis.job;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 资产在线数据统计：资产分析用
 *
 * 2022-9-19
 */
@Configuration
@EnableScheduling
@EnableAsync
@Order(value = 15)
public class AssetOnLineStatisticsJob implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(AssetOnLineStatisticsJob.class);

    @Autowired
    private AssetAnalysisService assetAnalysisService;

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                assetAnalysisService.excStatistic();
            }
        }).start();

    }

    // 5分钟执行一次
    @Scheduled(cron = "${asset.analysis.statistics.cron:0 0/5 * * * ?}")
    public void excStatistics(){
        logger.warn("定时执行资产分析页面数据统计开始");
        assetAnalysisService.excStatistic();
        logger.warn("执行资产分析页面数据统计结束");
    }


}
