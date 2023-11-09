package com.vrv.vap.netflow.service;

import com.vrv.vap.netflow.model.AppSysManager;
import com.vrv.vap.netflow.model.BaseKoalOrg;
import com.vrv.vap.netflow.model.BasePersonZjg;
import com.vrv.vap.netflow.model.BaseSecurityDomain;
import com.vrv.vap.netflow.vo.AssetVo;

import java.util.Map;

public interface NetflowBaseDataService {

    String CACHE_ORG_VO_KEY = "_BASEINFO:BASE_KOAL_ORG_VO:ALL";

    String CACHE_SEC_RANGE_VO_KEY = "_BASEINFO:BASE_SECURITY_DOMAIN_RANGE_VO:ALL";

    String CACHE_SEC_ID_KEY = "_BASEINFO:BASE_SECURITY_DOMAIN:ID";

    String CACHE_PERSON_ZJG_KEY = "_BASEINFO:BASE_PERSON_ZJG:ALL";

    String CACHE_SYS_MANAGER_KEY = "_BASEINFO:APP_SYS_MANAGER:ALL";

    String CACHE_SYS_MANAGER_IP_KEY = "_BASEINFO:APP_SYS_MANAGER:IP";

    String CACHE_ASSET_APPNO_KEY = "_BASEINFO:ASSET:APPNO";

    String CACHE_ASSET_IP_KEY = "_BASEINFO:ASSET:IP";

    void initBaseData();

    void updatePersonData();

    void updateAssetData();

    void updateAppData();

    void updateOrgData();

    void updateSecData();

    BasePersonZjg fixPersonIpCache(String ip);

    AssetVo fixAssetIpCache(String ip);

    /**
     * 获取本地缓存接口
     *
     * @param key 键
     * @return map集合
     */
    Map<String, Object> getLocalCache(String key);

    AppSysManager fixAppIpCache(String ip);

    AppSysManager fixAppUrlCache(String url);

    AssetVo fixAppAssetCache(String appNo);

    BaseKoalOrg fixOrgIpCache(String ip);

    BaseSecurityDomain fixSecIpCache(String ip);

    BaseSecurityDomain fixSecCodeCache(String id);
}
