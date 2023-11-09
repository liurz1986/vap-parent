package com.vrv.vap.admin;

import com.vrv.vap.admin.common.constant.ChangeMessageConstants;
import com.vrv.vap.admin.common.manager.TaskManager;
import com.vrv.vap.admin.common.properties.Site;
import com.vrv.vap.admin.common.task.SyncBaseDataTask;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.JobModel;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.common.service.RedirectService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InitRunner implements CommandLineRunner {


    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StringRedisTemplate redisTpl;

    @Autowired
    private RedirectService redirectService;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Autowired
    private BaseSecurityDomainService baseSecurityDomainService;

    @Autowired
    private BasePersonZjgService basePersonZjgService;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Autowired
    private NetflowBaseDataService netflowBaseDataService;

    @Autowired
    SyncBaseDataService syncBaseDataService;

    @Autowired
    AlarmItemGroupService alarmItemGroupService;

    @Autowired
    ChangeMessageService changeMessageService;

    private static String JOB_PRE = "syncBaseData-";


    @Autowired
    private Site site;


    private static String LOGIN_PAGE = "_LOGIN_PAGE";

    private static String SCAN_PATH = "com.vrv.vap";



    @Override
    public void run(String... strings) {

        //根据产品类型加载对应的转向信息
        SystemConfig productConfig = systemConfigService.findByConfId("versionInfo");
        if(productConfig !=null && !StringUtils.isEmpty(productConfig.getConfValue())) {
            String productId = productConfig.getConfValue();
            redirectService.initProduct(SCAN_PATH, productId);
        }

        //初始化redis的loginPage的配置信息
        SystemConfig loginPageConfig = systemConfigService.findByConfId("loginPage");
        if (loginPageConfig != null) {
            if (loginPageConfig.getConfValue() != null) {
                redisTpl.opsForValue().set(LOGIN_PAGE, loginPageConfig.getConfValue());
            }
        }


        SystemConfig systemVersion = systemConfigService.findByConfId("system_version");
        if(systemVersion!=null && !StringUtils.isEmpty(systemVersion.getConfValue())){
            //动态设置更节点
            site.setVersion(systemVersion.getConfValue());
        }

        List<BaseKoalOrg> baseKoalOrgs = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"type","1");
        if(baseKoalOrgs!=null && baseKoalOrgs.size()>0){
            site.setOrgRoot(baseKoalOrgs.get(0).getCode());
        }

        //缓存流量基础数据预处理数据
        //netflowBaseDataService.initBaseData();

        //重新加载基础数据同步任务
        List<SyncBaseData> baseDataList = syncBaseDataService.findAll();
        if (CollectionUtils.isNotEmpty(baseDataList)) {
            baseDataList.forEach(item -> {
                JobModel jobModel = new JobModel();
                jobModel.setJobName(JOB_PRE + item.getId());
                jobModel.setCronTime(item.getCron());
                jobModel.setJobClazz(SyncBaseDataTask.class);
                Map<String, String> param = new HashMap<>();
                param.put("id", item.getId().toString());
                param.put("type",item.getType());
                TaskManager.addJob(jobModel, param);
                if (item.getStatus() == 1) {
                    TaskManager.pauseJob(JOB_PRE + item.getId());
                }
            });
        }

        // 构建基础数据缓存
        baseKoalOrgService.cacheOrg();
        baseSecurityDomainService.cacheDomain();
        basePersonZjgService.cachePerson();
        baseDictAllService.cacheDict();
        // 系统告警主表数据初始化
        alarmItemGroupService.initGroupData();
        // 事件信息初始化
        redisTpl.delete(ChangeMessageConstants.VAP_SOURCE_MESSAGE);
        changeMessageService.initEventMessage();
    }

}
