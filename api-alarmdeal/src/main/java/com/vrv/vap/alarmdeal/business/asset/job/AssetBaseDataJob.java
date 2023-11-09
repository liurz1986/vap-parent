package com.vrv.vap.alarmdeal.business.asset.job;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppCsvService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetCsvService;
import com.vrv.vap.alarmdeal.business.asset.service.BaseDataRedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

/**
 * 资产相关任务处理整合：  2022-08-15
 * 1.资产csv文件处理
 */
@Configuration
@EnableScheduling
@EnableAsync
@Order(value = 7)
public class AssetBaseDataJob implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(AssetBaseDataJob.class);
    @Autowired
    private AssetCsvService assetCsvService;
    @Autowired
    private AppCsvService appCsvService;
    @Autowired
    private AlarmEventManagementService alarmEventManagementService;




    @Override
    public void run(String... args) throws Exception {
        logger.info("项目启动更新资产csv文件");
        assetCsvService.initAssetToCsv();
        logger.info("项目启动更新应用系统csv文件");
        appCsvService.initAppToCsv();
    }


    /**
     * 定时更新csv文件
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateAssetCsv(){
        logger.warn("定时更新资产CSV文件");
        assetCsvService.initAssetToCsv();
        logger.info("定时更新应用系统csv文件");
        appCsvService.initAppToCsv();
    }



    /**
     * 计算资产窃泄密值  0 59 23 * * ?
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void culStealLeakValue(){
        logger.warn("定时计算资产窃泄密值 ");
        List<Map<String, Map<String, Long>>> list = alarmEventManagementService.culStealLeakValue();
    }
}
