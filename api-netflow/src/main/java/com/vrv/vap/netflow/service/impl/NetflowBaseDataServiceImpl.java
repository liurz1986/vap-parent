package com.vrv.vap.netflow.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.netflow.common.util.IPUtils;
import com.vrv.vap.netflow.model.AppSysManager;
import com.vrv.vap.netflow.model.BaseKoalOrg;
import com.vrv.vap.netflow.model.BasePersonZjg;
import com.vrv.vap.netflow.model.BaseSecurityDomain;
import com.vrv.vap.netflow.service.NetflowBaseDataService;
import com.vrv.vap.netflow.utils.LoggerPrintStrategy;
import com.vrv.vap.netflow.utils.LoggerUtil;
import com.vrv.vap.netflow.vo.AssetVo;
import com.vrv.vap.netflow.vo.BaseKoalOrgVO;
import com.vrv.vap.netflow.vo.BaseSecurityDomainRangeVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NetflowBaseDataServiceImpl implements InitializingBean, NetflowBaseDataService {
    private static final LoggerUtil loggerUtil = LoggerUtil.getLogger(NetflowBaseDataServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    // 组织机构
    private static final Map<String, Object> orgMap = new HashMap<>();
    /**
     * 安全域
     * 在redis中的格式
     * 15) "4"
     * 16) "{\"code\":\"cb99e054-ea3b-45c3-a43c-e1989bac5b03\",\"domainName\":\"\xe5\xba\x94\xe7\x94\xa8\xe6\x9c\x8d\xe5\x8a\xa1\xe5\x9f\x9f\",\"id\":4,\"parentCode\":\"bc19b8c2-034e-4e8f-8480-2eb7ffb518bd\",\"secretLevel\":4,\"sort\":3,\"subCode\":\"001003\"}"
     * <p>
     * 127.0.0.1:6379> hget  _BASEINFO:BASE_SECURITY_DOMAIN:ID 4
     * "{\"code\":\"cb99e054-ea3b-45c3-a43c-e1989bac5b03\",\"domainName\":\"\xe5\xba\x94\xe7\x94\xa8\xe6\x9c\x8d\xe5\x8a\xa1\xe5\x9f\x9f\",\"id\":4,\"parentCode\":\"bc19b8c2-034e-4e8f-8480-2eb7ffb518bd\",\"secretLevel\":4,\"sort\":3,\"subCode\":\"001003\"}"
     */
    private static final Map<String, Object> secMap = new HashMap<>();
    // 人员
    private static final Map<String, Object> personMap = new HashMap<>();
    // 应用
    private static final Map<String, Object> appMap = new HashMap<>();
    // 资产
    private static final Map<String, Object> assetMap = new HashMap<>();

    private BaseSecurityDomain defaultNullSec = new BaseSecurityDomain();

    private BaseKoalOrg defaultNullOrg = new BaseKoalOrg();

    private AppSysManager defaultNullApp = new AppSysManager();

    private BasePersonZjg defaultNullPerson = new BasePersonZjg();

    /**
     * 初始化缓存数据
     * 1.定时任务每小时执行一次： 人员、组织机构、资产等基础数据，
     * 2.系统初始化执行一次，人员、组织机构、资产等基础数据
     */
    @Override
    public void initBaseData() {
        loggerUtil.info(String.format("开始加载人员、组织机构、资产.. begin时间:%s", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date())));
        this.updateAppData();
        this.updateAssetData();
        this.updateOrgData();
        this.updatePersonData();
        this.updateSecData();
        loggerUtil.info(String.format("开始加载人员、组织机构、资产.. end时间:%s", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date())));
    }

    @Override
    public void updatePersonData() {
        loggerUtil.debug("更新人员信息缓存");
        // TODO 此处不能删除，否则会导致缓存数据丢失
//        personMap.clear();
        Set<String> keys = redisTemplate.keys(CACHE_PERSON_ZJG_KEY);
        DataType type = redisTemplate.type(CACHE_PERSON_ZJG_KEY);
        loggerUtil.debug(String.format("redis中存在的key: %s, type: %s", keys, type));
        String personStr = redisTemplate.opsForValue().get(CACHE_PERSON_ZJG_KEY);
        loggerUtil.debug("人员信息缓存:{}", personStr);
        personMap.put(CACHE_PERSON_ZJG_KEY, StringUtils.isNotEmpty(personStr) ? personStr : "");
    }

    @Override
    public void updateAssetData() {
        loggerUtil.debug("更新资产信息缓存");
        // TODO 此处不能删除，否则会导致缓存数据丢失
//        assetMap.clear();
        Map assetIpJson = redisTemplate.opsForHash().entries(CACHE_ASSET_IP_KEY);
        assetMap.put(CACHE_ASSET_IP_KEY, assetIpJson != null ? assetIpJson : new HashMap<>());
        loggerUtil.debug("资产ip,assetIpJson:{}", JSONObject.toJSONString(assetIpJson));

        Map appAssetJson = redisTemplate.opsForHash().entries(CACHE_ASSET_APPNO_KEY);
        assetMap.put(CACHE_ASSET_APPNO_KEY, appAssetJson != null ? appAssetJson : new HashMap<>());
        loggerUtil.debug("应用资产appAssetJson:{}", JSONObject.toJSONString(appAssetJson));
    }

    @Override
    public void updateAppData() {
        loggerUtil.debug("更新应用信息缓存");
        // TODO 此处不能删除，否则会导致缓存数据丢失
        // appMap.clear();
        Map appIpJson = redisTemplate.opsForHash().entries(CACHE_SYS_MANAGER_IP_KEY);
        appMap.put(CACHE_SYS_MANAGER_IP_KEY, appIpJson != null ? appIpJson : new HashMap<>());
        loggerUtil.debug("应用信息appIpJson:{}", JSONObject.toJSONString(appIpJson));

        String appUrlJson = redisTemplate.opsForValue().get(CACHE_SYS_MANAGER_KEY);
        appMap.put(CACHE_SYS_MANAGER_KEY, StringUtils.isNotEmpty(appUrlJson) ? appUrlJson : "");
        loggerUtil.debug("应用appUrlJson:{}", JSONObject.toJSONString(appUrlJson));
    }

    @Override
    public void updateOrgData() {
        loggerUtil.debug("更新组织机构缓存");
        // TODO 此处不能删除，否则会导致缓存数据丢失. 单例模式下，使用new就存在这个问题。
//        orgMap.clear();
        String orgIpJson = redisTemplate.opsForValue().get(CACHE_ORG_VO_KEY);
        orgMap.put(CACHE_ORG_VO_KEY, StringUtils.isNotEmpty(orgIpJson) ? orgIpJson : "");
    }

    @Override
    public void updateSecData() {
        loggerUtil.debug("更新安全域缓存");
        // TODO 此处不能删除，否则会导致缓存数据丢失
        secMap.clear();
        String secIpJson = redisTemplate.opsForValue().get(CACHE_SEC_RANGE_VO_KEY);
        secMap.put(CACHE_SEC_RANGE_VO_KEY, StringUtils.isNotEmpty(secIpJson) ? secIpJson : "");

        Map secIdJson = redisTemplate.opsForHash().entries(CACHE_SEC_ID_KEY);
        // TODO fixed bug: redis中的数据类型是hash，但是这里使用的是string，导致数据类型转换异常
        secMap.put(CACHE_SEC_ID_KEY, secIdJson != null ? secIdJson : new HashMap<>());
    }

    /**
     * 需要优化： 存入的string是全量资产json的数据
     *
     * @param ip
     * @return
     */
    @Override
    public BasePersonZjg fixPersonIpCache(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return null;
        }
        if (!personMap.containsKey(ip)) {
            String personStr = (String) personMap.get(CACHE_PERSON_ZJG_KEY);
            List<BasePersonZjg> personZjgList = JSON.parseArray(personStr, BasePersonZjg.class);
            if (CollectionUtils.isNotEmpty(personZjgList)) {
                Map assetIpMap = (Map) assetMap.get(CACHE_ASSET_IP_KEY);
                if (assetIpMap.containsKey(ip)) {
                    String jsonObject = (String) assetIpMap.get(ip);
                    AssetVo assetVo = JSON.parseObject(jsonObject, AssetVo.class);
                    Optional<BasePersonZjg> basePersonZjgOptional = personZjgList.stream().filter(basePersonZjg -> assetVo.getResponsibleCode().equals(basePersonZjg.getUserNo())).findFirst();
                    if (basePersonZjgOptional.isPresent()) {
                        personMap.put(ip, basePersonZjgOptional.get());
                    } else {
                        loggerUtil.debug(String.format("personZjgList中不存在此ip缓存:{}", ip), new LoggerPrintStrategy(loggerUtil, "fixPersonIpCache1" + ip));
                        personMap.put(ip, defaultNullPerson);
                    }

                } else {
                    loggerUtil.debug(String.format("assetIpMap中不存在此ip缓存:{}", ip), new LoggerPrintStrategy(loggerUtil, "fixPersonIpCache2" + ip));
                    personMap.put(ip, defaultNullPerson);
                }
            } else {
                personMap.put(ip, defaultNullPerson);
            }
        }
        BasePersonZjg basePersonZjg = (BasePersonZjg) personMap.get(ip);
        if (basePersonZjg != null && basePersonZjg.getUserNo() != null) {
            return basePersonZjg;
        }
        return null;
    }

    /**
     * 获取资产ip缓存map集合
     *
     * @return map集合
     */
    @Override
    public Map<String, Object> getLocalCache(String key) {
        Assert.notNull(key, "getLocalCache key不能为空！");
        Map assetIpMap = (Map) assetMap.get(key);
        return assetIpMap;
    }

    /**
     * @param ip
     * @return
     */
    @Override
    public AssetVo fixAssetIpCache(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return null;
        }
        Map assetIpMap = (Map) assetMap.get(CACHE_ASSET_IP_KEY);
        if (assetIpMap.containsKey(ip)) {
            String jsonObject = (String) assetIpMap.get(ip);
            AssetVo assetVo = JSON.parseObject(jsonObject, AssetVo.class);
            if (assetVo != null) {
                if (StringUtils.isEmpty(assetVo.getTerminalType())) {
                    String typeGuid = assetVo.getAssetType();
                    if ("82416cd327b74519a78667f2245693a9".equals(typeGuid) ||
                            "60f36b0370db4464a8b17e0d3347bdc9".equals(typeGuid) ||
                            "99e94620ed644647b53936df79d26684".equals(typeGuid) ||
                            "ced9117549ca4216b3012a4b4232d5e0".equals(typeGuid) ||
                            "67dce0923b3d455ab6a33dbabaed6556".equals(typeGuid) ||
                            "cfe79f917990438bb9c61b922b5d0638".equals(typeGuid)) {
                        assetVo.setTerminalType("3");
                    }
                }
                return assetVo;
            } else {
                loggerUtil.debug(String.format("转换assetVo的值: %s", jsonObject), new LoggerPrintStrategy(loggerUtil, "fixAssetIpCache" + ip));
            }
        } else {
            loggerUtil.debug(String.format("fixAssetIpCache 资产ip缓存中此ip缓存:%s， 原始数据：%s", ip, JSONObject.toJSONString(assetIpMap)), new LoggerPrintStrategy(loggerUtil, "fixAssetIpCache" + ip));
        }
        return null;
    }

    @Override
    public AppSysManager fixAppIpCache(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return null;
        }
        Map appIpMap = (Map) appMap.get(CACHE_SYS_MANAGER_IP_KEY);
        if (appIpMap.containsKey(ip)) {
            String jsonObject = (String) appIpMap.get(ip);
            List<AppSysManager> appSysManagerList = JSON.parseArray(jsonObject, AppSysManager.class);
            if (CollectionUtils.isNotEmpty(appSysManagerList)) {
                AppSysManager appSysManager = appSysManagerList.get(0);
                if (appSysManager.getAppNo() != null) {
                    return appSysManager;
                }
            }
        } else {
            loggerUtil.debug(String.format("AppSysManager， ip:%s， 原始数据：%s", ip, JSONObject.toJSONString(appIpMap)), new LoggerPrintStrategy(loggerUtil, "fixAppIpCache" + ip));
        }
        return null;
    }

    @Override
    public AppSysManager fixAppUrlCache(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (!appMap.containsKey(url)) {
            String sysManagerStr = (String) appMap.get(CACHE_SYS_MANAGER_KEY);
            List<AppSysManager> appSysManagerList = JSON.parseArray(sysManagerStr, AppSysManager.class);
            if (CollectionUtils.isNotEmpty(appSysManagerList)) {
                Optional<AppSysManager> optionalAppSysManager = appSysManagerList.stream().filter(appSysManager -> StringUtils.isNotEmpty(appSysManager.getDomainName()) && url.contains(appSysManager.getDomainName())).findFirst();
                appMap.put(url, optionalAppSysManager.isPresent() ? optionalAppSysManager.get() : defaultNullApp);
            } else {
                appMap.put(url, defaultNullApp);
            }
        } else {
            loggerUtil.debug(String.format("AppSysManager， fixAppUrlCache -> url: %s", url));
        }
        AppSysManager appSysManager = (AppSysManager) appMap.get(url);
        if (appSysManager.getAppNo() != null) {
            return appSysManager;
        }
        return null;
    }

    @Override
    public AssetVo fixAppAssetCache(String appNo) {
        if (StringUtils.isEmpty(appNo)) {
            return null;
        }
        Map appAssetMap = (Map) assetMap.get(CACHE_ASSET_APPNO_KEY);
        if (appAssetMap.containsKey(appNo)) {
            String jsonObject = (String) appAssetMap.get(appNo);
            List<AssetVo> assetVoList = JSON.parseArray(jsonObject, AssetVo.class);
            if (CollectionUtils.isNotEmpty(assetVoList)) {
                AssetVo assetVo = assetVoList.get(0);
                if (assetVo != null) {
                    return assetVo;
                }
            }
        } else {
            loggerUtil.debug(String.format("fixAppAssetCache， appNo:%s， 原始数据：%s", appNo, JSONObject.toJSONString(appAssetMap)), new LoggerPrintStrategy(loggerUtil, "fixAppAssetCache" + appNo));
        }
        return null;
    }

    @Override
    public BaseKoalOrg fixOrgIpCache(String ip) {
        if (!orgMap.containsKey(ip)) {
            Long num = IPUtils.ip2int(ip);
            if (num == 0) {
                orgMap.put(ip, defaultNullOrg);
            }
            String orgIpStr = (String) orgMap.get(CACHE_ORG_VO_KEY);
            List<BaseKoalOrgVO> orgVOList = JSON.parseArray(orgIpStr, BaseKoalOrgVO.class);
            if (CollectionUtils.isNotEmpty(orgVOList)) {
                Optional<BaseKoalOrgVO> orgVOOptional = orgVOList.stream().filter(baseKoalOrgVO -> baseKoalOrgVO.getStartIpNum() != null && baseKoalOrgVO.getEndIpNum() != null && baseKoalOrgVO.getStartIpNum() <= num && baseKoalOrgVO.getEndIpNum() >= num).findFirst();
                orgMap.put(ip, orgVOOptional.isPresent() ? orgVOOptional.get() : defaultNullOrg);
            } else {
                orgMap.put(ip, defaultNullOrg);
            }
        }
        BaseKoalOrg baseKoalOrg = (BaseKoalOrg) orgMap.get(ip);
        if (baseKoalOrg != null && baseKoalOrg.getUuId() != null) {
            return baseKoalOrg;
        }
        return null;
    }

    @Override
    public BaseSecurityDomain fixSecIpCache(String ip) {
        if (!secMap.containsKey(ip)) {
            Long num = IPUtils.ip2int(ip);
            if (num == 0) {
                secMap.put(ip, defaultNullSec);
            }
            String secIpStr = (String) secMap.get(CACHE_SEC_RANGE_VO_KEY);
            List<BaseSecurityDomainRangeVO> rangeVOList = JSON.parseArray(secIpStr, BaseSecurityDomainRangeVO.class);
            if (CollectionUtils.isNotEmpty(rangeVOList)) {
                Optional<BaseSecurityDomainRangeVO> rangeVoOptional = rangeVOList.stream().filter(rangeVO -> rangeVO.getStartIpNum() != null && rangeVO.getEndIpNum() != null && rangeVO.getStartIpNum() <= num && rangeVO.getEndIpNum() >= num).findFirst();
                secMap.put(ip, rangeVoOptional.isPresent() ? rangeVoOptional.get() : defaultNullSec);
            } else {
                secMap.put(ip, defaultNullSec);
            }
        }
        BaseSecurityDomain baseSecurityDomain = (BaseSecurityDomain) secMap.get(ip);
        if (baseSecurityDomain != null && baseSecurityDomain.getCode() != null) {
            return baseSecurityDomain;
        }
        return null;
    }

    /**
     * 在redis中的格式，key是id，value是json字符串
     * 15) "4"
     * 16) "{\"code\":\"cb99e054-ea3b-45c3-a43c-e1989bac5b03\",\"domainName\":\"\xe5\xba\x94\xe7\x94\xa8\xe6\x9c\x8d\xe5\x8a\xa1\xe5\x9f\x9f\",\"id\":4,\"parentCode\":\"bc19b8c2-034e-4e8f-8480-2eb7ffb518bd\",\"secretLevel\":4,\"sort\":3,\"subCode\":\"001003\"}"
     * <p>
     * 127.0.0.1:6379> hget  _BASEINFO:BASE_SECURITY_DOMAIN:ID 4
     * "{\"code\":\"cb99e054-ea3b-45c3-a43c-e1989bac5b03\",\"domainName\":\"\xe5\xba\x94\xe7\x94\xa8\xe6\x9c\x8d\xe5\x8a\xa1\xe5\x9f\x9f\",\"id\":4,\"parentCode\":\"bc19b8c2-034e-4e8f-8480-2eb7ffb518bd\",\"secretLevel\":4,\"sort\":3,\"subCode\":\"001003\"}"
     *
     * @param code 安全域的guid
     * @return BaseSecurityDomain
     */
    @Override
    public BaseSecurityDomain fixSecCodeCache(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        BaseSecurityDomain baseSecurityDomain = null;
        LinkedHashMap<String, String> secIdMap = (LinkedHashMap<String, String>) secMap.get(CACHE_SEC_ID_KEY);
        // secIdMap中的key是code，value是json字符串
        for (String json : secIdMap.values()) {
            baseSecurityDomain = JSON.parseObject(json, BaseSecurityDomain.class);
            if (baseSecurityDomain.getCode().equals(code)) {
                break;
            }
        }
        return baseSecurityDomain;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


}
