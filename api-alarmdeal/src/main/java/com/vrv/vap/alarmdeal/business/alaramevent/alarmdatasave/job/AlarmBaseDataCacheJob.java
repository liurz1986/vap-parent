package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmBaseDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetTypeCacheVo;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.AlarmInfoMergerHandler;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.service.AssetExtendService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月21日 9:34
 */
@Configuration
@EnableScheduling
@Component
public class AlarmBaseDataCacheJob {
    private Logger logger = LoggerFactory.getLogger(AlarmBaseDataCacheJob.class);
    @Autowired
    private AlarmBaseDataService alarmBaseDataService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetTypeService assetTypeService;

    @Autowired
    private AssetExtendService assetExtendService;

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private AppSysManagerService appSysManagerService;

    @Autowired
    private AlarmInfoMergerHandler alarmInfoMergerHandler;

    @Scheduled(cron = "0 */1 * * * ?")
    public void initBaseDataCache(){
        logger.warn("定时初始化告警初始数据");
        // 初始化资产数据
        List<AssetCacheVo> assets= assetService.queryAllAsset();
        alarmBaseDataService.initAlarmBaseForAsset(assets);

        // 初始化资产类型数据
        List<AssetTypeCacheVo> assetTypes =assetTypeService.getAssetTypeList();
        alarmBaseDataService.initAlarmBaseForAssetType(assetTypes);

        // 初始化资产扩展数据
        List<AssetExtend> assetExtends =assetExtendService.findAll();
        alarmBaseDataService.initAlarmBaseForAssetExtend(assetExtends);

        // 初始化人员信息
        VData<List<BasePersonZjg>> basePersonZjgsVdata =adminFeign.getAllPerson();
        List<BasePersonZjg> basePersonZjgs = basePersonZjgsVdata.getData();
        alarmBaseDataService.initAlarmBaseForBasePersonZjg(basePersonZjgs);

        // 初始化安全域信息
        VData<List<BaseSecurityDomain>> baseSecurityDomainVdatas = adminFeign.getAllDomain();
        List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainVdatas.getData();
        alarmBaseDataService.initAlarmBaseForBaseSecurityDomain(baseSecurityDomains);

        // 初始化系统信息
        List<AppSysManager> appSysManagers = appSysManagerService.findAll();
        alarmBaseDataService.initAlarmBaseForAppSysManager(appSysManagers);

        // 刷新缓存（本地）
        alarmInfoMergerHandler.clearCache();
    }
}
