package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.AssetExtendBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmBaseDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AppSysManagerCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetTypeCacheVo;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.service.AssetExtendService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月21日 9:24
 */
@Service
public class AlarmBaseDataServiceImpl implements AlarmBaseDataService {
    /**
     * gson对象
     */
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

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
    private JdbcTemplate jdbcTemplate;

    /**
     * 初始化告警初始数据  资产
     * @param assets
     */
    @Override
    public void initAlarmBaseForAsset(List<AssetCacheVo> assets) {
        CommomLocalCache.put("Alarm-Asset", assets, 2, TimeUnit.HOURS);
    }

    /**
     * 初始化告警初始数据   资产类型
     * @param assetTypes
     */
    @Override
    public void initAlarmBaseForAssetType(List<AssetTypeCacheVo> assetTypes) {
        CommomLocalCache.put("Alarm-AssetType", assetTypes, 2, TimeUnit.HOURS);
    }

    /**
     * 初始化告警初始数据   资产扩展
     * @param assetExtends
     */
    @Override
    public void initAlarmBaseForAssetExtend(List<AssetExtend> assetExtends) {
        CommomLocalCache.put("Alarm-AssetExtend", assetExtends, 2, TimeUnit.HOURS);
    }

    /**
     * 初始化告警初始数据   人员
     * @param basePersonZjgs
     */
    @Override
    public void initAlarmBaseForBasePersonZjg(List<BasePersonZjg> basePersonZjgs) {
        CommomLocalCache.put("Alarm-BasePersonZjg", basePersonZjgs, 2, TimeUnit.HOURS);
    }

    /**
     * 初始化告警初始数据   安全域
     * @param domains
     */
    @Override
    public void initAlarmBaseForBaseSecurityDomain(List<BaseSecurityDomain> domains) {
        CommomLocalCache.put("Alarm-BaseSecurityDomain", domains, 2, TimeUnit.HOURS);
    }

    /**
     * 初始化告警初始数据   系统
     * @param appSysManagers
     */
    @Override
    public void initAlarmBaseForAppSysManager(List<AppSysManager> appSysManagers) {
        CommomLocalCache.put("Alarm-AppSysManager", appSysManagers, 2, TimeUnit.HOURS);
    }

    /**
     * 通过ip查询资产
     * @param ip
     * @return com.vrv.vap.alarmdeal.business.asset.model.Asset
     */
    @Override
    public AssetCacheVo queryAssetByIp(String ip) {
        if(StringUtils.isBlank(ip)){
            return null;
        }
        List<AssetCacheVo> assets = null;
        if(CommomLocalCache.containsKey("Alarm-Asset")){
            assets = CommomLocalCache.get("Alarm-Asset");
        }else{
            assets = assetService.queryAllAsset();
            CommomLocalCache.put("Alarm-Asset", assets, 2, TimeUnit.HOURS);
        }
        List<AssetCacheVo> asset = assets.stream().filter(item -> ip.equals(item.getIp())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(asset)){
            return asset.get(0);
        }else{
            List<AssetCacheVo> assets1 = assetService.queryAssetCacheVoByIp(ip);
            if(CollectionUtils.isNotEmpty(assets1)){
                assets.addAll(assets1);
                CommomLocalCache.put("Alarm-Asset", assets, 2, TimeUnit.HOURS);
                return assets1.get(0);
            }
        }
        return null;
    }

    /**
     * 通过id查询资产类型
     * @param id
     * @return com.vrv.vap.alarmdeal.business.asset.model.AssetType
     */
    @Override
    public String queryAssetByAssetId(String id) {
        List<AssetTypeCacheVo> assetTypes = null;
        if(CommomLocalCache.containsKey("Alarm-AssetType")){
            assetTypes = CommomLocalCache.get("Alarm-AssetType");
        }else{
            assetTypes = assetTypeService.getAssetTypeList();
            CommomLocalCache.put("Alarm-AssetType", assetTypes, 2, TimeUnit.HOURS);
        }
        List<AssetTypeCacheVo> assetType = assetTypes.stream().filter(item -> id.equals(item.getId())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(assetType)){
            return assetType.get(0).getName();
        }
        return null;
    }

    /**
     * 通过设备id查询资产扩展信息
     * @param guid
     * @return AssetExtendBean
     */
    @Override
    public AssetExtendBean queryAssetExtendData(String guid) {
        List<AssetExtend> assetExtends = null;
        if(CommomLocalCache.containsKey("Alarm-AssetExtend")){
            assetExtends = CommomLocalCache.get("Alarm-AssetExtend");
        }else{
            assetExtends = assetExtendService.findAll();
            CommomLocalCache.put("Alarm-AssetExtend",assetExtends,2,TimeUnit.HOURS);
        }
        List<AssetExtend> assetExtendOptional = assetExtends.stream().filter(item->guid.equals(item.getAssetGuid())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(assetExtendOptional)){
            AssetExtend assetExtend = assetExtendOptional.get(0);
            if(StringUtils.isNotBlank(assetExtend.getExtendInfos())){
                AssetExtendBean result = gson.fromJson(assetExtend.getExtendInfos(),AssetExtendBean.class) ;
                return result;
            }
        }
        return null;
    }

    /**
     * 通过用户编号查询用户信息
     * @param userNo
     * @return com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg
     */
    @Override
    public BasePersonZjg queryPersonByUserNo(String userNo) {
        List<BasePersonZjg> basePersonZjgs = null;
        if(CommomLocalCache.containsKey("Alarm-BasePersonZjg")){
            basePersonZjgs = CommomLocalCache.get("Alarm-BasePersonZjg");
        }else{
            basePersonZjgs = adminFeign.getAllPerson().getData();
            CommomLocalCache.put("Alarm-BasePersonZjg",basePersonZjgs,2,TimeUnit.HOURS);
        }
        List<BasePersonZjg> basePersonZjg = basePersonZjgs.stream().filter(item->userNo.equals(item.getUserNo())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(basePersonZjg)){
            return basePersonZjg.get(0);
        }
        return null;
    }

    /**
     * 通过id查询安全域信息
     * @param id
     * @return com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain
     */
    @Override
    public BaseSecurityDomain queryDomianById(String id) {
        List<BaseSecurityDomain> baseSecurityDomains = null;
        if(CommomLocalCache.containsKey("Alarm-BaseSecurityDomain")){
            baseSecurityDomains = CommomLocalCache.get("Alarm-BaseSecurityDomain");
        }else{
            baseSecurityDomains = adminFeign.getRootDomains().getData();
            CommomLocalCache.put("Alarm-BaseSecurityDomain",baseSecurityDomains,2,TimeUnit.HOURS);
        }
        List<BaseSecurityDomain> baseSecurityDomain = baseSecurityDomains.stream().filter(item->id.equals(item.getCode())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(baseSecurityDomain)){
            return baseSecurityDomain.get(0);
        }
        return null;
    }

    /**
     * 通过app id查询应用信息
     * @param id
     * @return com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager
     */
    @Override
    public AppSysManagerCacheVo qyeryAppSysManagerByAppId(String id) {
        if(StringUtils.isBlank(id)){
            return null;
        }
        List<AppSysManagerCacheVo> appSysManagers = null;
        if(CommomLocalCache.containsKey("Alarm-AppSysManager")){
            appSysManagers = CommomLocalCache.get("Alarm-AppSysManager");
        }else{
            appSysManagers = appSysManagerService.getAppSysManagerList();
            CommomLocalCache.put("Alarm-AppSysManager",appSysManagers,2,TimeUnit.HOURS);
        }
        List<AppSysManagerCacheVo> appSysManagerCacheVos = new ArrayList<>();
        // appSysManagers.stream().forEach(item->{
        //     if(StringUtils.isNotBlank(item.getServiceId())){
        //         appSysManagerCacheVos.add(item);
        //     }
        // });
        List<AppSysManagerCacheVo> appSysManager = appSysManagerCacheVos.stream().filter(item->item.getServiceId().contains(id)).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(appSysManager)){
            return appSysManager.get(0);
        }
        return null;
    }
}
