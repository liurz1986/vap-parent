package com.vrv.vap.netflow.common.task;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.netflow.common.util.Md5Util;
import com.vrv.vap.netflow.model.BaseKoalOrg;
import com.vrv.vap.netflow.model.BasePersonZjg;
import com.vrv.vap.netflow.service.NetflowBaseDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

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
    NetflowBaseDataService netflowBaseDataService;

    @Value("${collector.configPath}")
    private String collectorConfigPath;

    private String personMd5;

    private String orgMd5;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String CACHE_PERSON_ZJG_KEY = "_BASEINFO:BASE_PERSON_ZJG:ALL";

    private static final String CACHE_ORG_KEY = "_BASEINFO:BASE_KOAL_ORG:ALL";

    @Scheduled(cron = "0 0 */1 * * ?")
    public void configSync() {
        log.info("start refresh org & person config");
        String personInfo = syncPersonInfo();
        String orgInfo = syncOrgInfo();
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
            log.info("刷新缓存信息");
            netflowBaseDataService.initBaseData();
        }
        log.info("end refresh org & person config");
    }

    public String syncPersonInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        String personStr = redisTemplate.opsForValue().get(CACHE_PERSON_ZJG_KEY);
        List<BasePersonZjg> basePersonZjgList = JSON.parseArray(personStr, BasePersonZjg.class);
        if (CollectionUtils.isNotEmpty(basePersonZjgList)) {
            stringBuilder.append("{\"table\":\"baseinfo_user\",\"join\":\"std_user_no\",\"add\":[\"std_username\",\"std_org_code\",\"std_user_level\",\"std_user_type\"],sep:\",\"}");
            stringBuilder.append("\n");
            stringBuilder.append("std_user_no,std_username,std_org_code,std_user_level,std_user_type");
            stringBuilder.append("\n");
            basePersonZjgList.stream().forEach(basePersonZjg -> {
                stringBuilder.append(basePersonZjg.getUserNo());
                stringBuilder.append(",");
                stringBuilder.append(basePersonZjg.getUserName());
                stringBuilder.append(",");
                stringBuilder.append(basePersonZjg.getOrgCode());
                stringBuilder.append(",");
                stringBuilder.append(basePersonZjg.getSecretLevel());
                stringBuilder.append(",");
                stringBuilder.append(StringUtils.isNotEmpty(basePersonZjg.getPersonType())?basePersonZjg.getPersonType():"-1");
                stringBuilder.append("\n");
            });
        }
        return stringBuilder.toString();
    }

    public String syncOrgInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        String orgStr = redisTemplate.opsForValue().get(CACHE_ORG_KEY);
        List<BaseKoalOrg> baseKoalOrgs = JSON.parseArray(orgStr, BaseKoalOrg.class);
        if (CollectionUtils.isNotEmpty(baseKoalOrgs)) {
            stringBuilder.append("{\"table\":\"baseinfo_org\",\"join\":\"std_org_code\",\"add\":[\"std_sys_port\",\"std_org_name\",\"std_org_type\",\"std_secret_qualifications\",\"std_secret_level\",\"std_protection_level\"],sep:\",\"}");
            stringBuilder.append("\n");
            stringBuilder.append("std_org_code,std_org_name,std_org_type,std_secret_qualifications,std_secret_level,std_protection_level");
            stringBuilder.append("\n");
            baseKoalOrgs.stream().forEach(baseKoalOrg -> {
                stringBuilder.append(baseKoalOrg.getCode());
                stringBuilder.append(",");
                stringBuilder.append(org.apache.commons.lang3.StringUtils.isNotEmpty(baseKoalOrg.getName())?baseKoalOrg.getName():"-1");
                stringBuilder.append(",");
                stringBuilder.append(baseKoalOrg.getOrgType()!=null?baseKoalOrg.getOrgType():"-1");
                stringBuilder.append(",");
                stringBuilder.append(baseKoalOrg.getSecretQualifications()!=null?baseKoalOrg.getSecretQualifications():"-1");
                stringBuilder.append(",");
                stringBuilder.append(baseKoalOrg.getSecretLevel()!=null?baseKoalOrg.getSecretLevel():"-1");
                stringBuilder.append(",");
                stringBuilder.append(baseKoalOrg.getProtectionLevel()!=null?baseKoalOrg.getProtectionLevel():"-1");
                stringBuilder.append("\n");
            });
        }

        return stringBuilder.toString();
    }

    public void syncFlume(String fileName,String info){
        PrintStream stream = null;
        try {
            log.info("====同步至采集器路径地址===" + collectorConfigPath);
            File myPath = new File(collectorConfigPath);
            if (!myPath.exists()) {
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
            log.error("同步flume文件异常！", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

}
