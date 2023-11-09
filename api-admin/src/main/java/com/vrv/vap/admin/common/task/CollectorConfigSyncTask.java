package com.vrv.vap.admin.common.task;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.util.Md5Util;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.model.NetworkMonitor;
import com.vrv.vap.admin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author wangneng
 * @date 2021/8/11
 * @description
 */
@Component
@Slf4j
public class CollectorConfigSyncTask {

    private static final Logger logger = LoggerFactory.getLogger(CollectorConfigSyncTask.class);

    @Autowired
    BasePersonZjgService personZjgService;

    @Autowired
    BaseKoalOrgService baseKoalOrgService;

    @Autowired
    NetflowBaseDataService netflowBaseDataService;

    @Value("${collector.configPath}")
    private String collectorConfigPath;

    private String personMd5;

    private String orgMd5;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void configSync() {
        log.info("start refresh org & person config");
        String personInfo = personZjgService.sync();
        String orgInfo = baseKoalOrgService.sync();
        String localPersonMd5 = Md5Util.string2Md5(personInfo);
        String localOrgMd5 = Md5Util.string2Md5(orgInfo);
        boolean initFlag = false;
        if(StringUtils.isNotEmpty(personInfo) && StringUtils.isNotEmpty(localPersonMd5) && !localPersonMd5.equals(personMd5)){
            syncFlume("baseinfo_user.csv",personInfo);
            initFlag = true;
        }
        if(StringUtils.isNotEmpty(orgInfo) && StringUtils.isNotEmpty(localOrgMd5) && !localOrgMd5.equals(orgMd5)){
            syncFlume("baseinfo_org.csv",orgInfo);
            initFlag = true;
        }
        personMd5 = localPersonMd5;
        orgMd5 = localOrgMd5;
        if(initFlag) {
            netflowBaseDataService.initBaseData();
        }
        log.info("end refresh org & person config");
    }


    private void syncFlume(String fileName,String info){
        PrintStream stream = null;
        try {
            log.info("====同步至采集器路径地址===" + collectorConfigPath);
            File myPath = new File(collectorConfigPath);
            if (!myPath.exists()) {//
                myPath.mkdirs();
                System.out.println("创建文件夹路径为：" + collectorConfigPath);
            }
            String collectorConfigFile = collectorConfigPath + File.separator +fileName;
            File configFile = new File(collectorConfigFile);
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            stream = new PrintStream(collectorConfigFile);//写入的文件path
            stream.print(info);//写入的字符串
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

}
