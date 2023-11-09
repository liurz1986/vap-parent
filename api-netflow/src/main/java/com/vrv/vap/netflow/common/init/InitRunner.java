package com.vrv.vap.netflow.common.init;

import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.netflow.common.task.CollectorConfigSyncTask;
import com.vrv.vap.netflow.service.NetflowBaseDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Order(value = 100)
public class InitRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(InitRunner.class);

    @Autowired
    private NetflowBaseDataService netflowBaseDataService;

    @Autowired
    CollectorConfigSyncTask collectorConfigSyncTask;

    @Override
    public void run(String... strings) {
        logger.info("系统初始化缓存数据，开始加载...人员、组织机构、资产等基础数据。时间：{}", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()));
        //缓存流量基础数据预处理数据
        netflowBaseDataService.initBaseData();
        //组织机构、人员csv文件同步
        collectorConfigSyncTask.configSync();
        logger.info("系统初始化缓存数据，开始完毕...人员、组织机构、资产等基础数据");
    }
}
