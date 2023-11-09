package com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.DeviceInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.PersonLiable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class EventSearchCache {

    private static AssetService assetService;

    private static AdminFeign authFeign;

    private static AssetTypeService assetTypeService;

    private static AssetTypeSnoService assetTypeSnoService;

    static {
        if (authFeign == null) {
            assetService = SpringUtil.getBean(AssetService.class);
            authFeign = SpringUtil.getBean(AdminFeign.class);
            assetTypeService = SpringUtil.getBean(AssetTypeService.class);
            assetTypeSnoService = SpringUtil.getBean(AssetTypeSnoService.class);
        }
    }

    private static Logger logger = LoggerFactory.getLogger(EventSearchCache.class);

    private static Asset getAsset(DeviceInfo device) {
        Asset asset = null;
        if (CommomLocalCache.containsKey(device.getDeviceId())) {
            asset = (Asset) CommomLocalCache.get(device.getDeviceId());
        } else {
            asset = assetService.getOne(device.getDeviceId());
            CommomLocalCache.put(device.getDeviceId(), asset, 2, TimeUnit.HOURS);
        }
        return asset;
    }

    public static void updateUnitInfo(UnitInfo unitInfoTmp) {
        try {
            // 通过Std_org_code 查询缓存数据
            BaseKoalOrg orgResult = getBaseKoalOrg(unitInfoTmp.getUnitGeoIdent());
            if (orgResult != null) {
                unitInfoTmp.setUnitDepartSubCode(orgResult.getSubCode());
                String parentCode = orgResult.getParentCode();
                BaseKoalOrg parentResult = getBaseKoalOrg(parentCode);
                if (parentResult != null) {
                    unitInfoTmp.setUnitName(parentResult.getName());
                    unitInfoTmp.setUnitIdent(parentResult.getCode());
                } else {
                    logger.debug("找不到机构信息：" + unitInfoTmp.getUnitGeoIdent());
                }
            } else {
                logger.debug("找不到机构信息：" + unitInfoTmp.getUnitGeoIdent());
            }
        } catch (Exception e) {
            logger.error("query interface orgByCode error,std_org_code = {}, errorMsg= {}",
                    unitInfoTmp.getUnitGeoIdent(), e.getMessage());
        }
    }

    /**
     * 查询BaseKoalOrg数据，先通过缓存，再通过接口
     *
     * @param code std_org_code 或者 parentCode
     * @return ResultObjVO<BaseKoalOrg>
     */
    private static BaseKoalOrg getBaseKoalOrg(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        // 通过parentCode 查询缓存
        BaseKoalOrg result = null;
        if (!CommomLocalCache.containsKey(code)) {
            // 缓存为空时，查询接口，并且更新缓存，2小时过期
            try {
                if (authFeign == null) {
                    logger.debug("authFeign 注入失败");
                }
                VData<BaseKoalOrg> orgByCode = authFeign.orgByCode(code);
                if (orgByCode != null && "0".equals(orgByCode.getCode())) {
                    result = orgByCode.getData();
                    if (result != null) {
                        CommomLocalCache.put(code, result, 2, TimeUnit.HOURS);
                    }
                } else if (orgByCode != null) {
                    logger.info(" authFeign.orgByCode 接口调用失败" + orgByCode.getMessage());
                }
            } catch (Exception e) {
                logger.error(" authFeign.orgByCode 接口调用失败", e);
            }
        } else {
            result = CommomLocalCache.get(code);
        }
        return result;
    }

    private static BaseSecurityDomain getBaseSecurityDomain(Asset asset) {
        BaseSecurityDomain domain = null;
        if (CommomLocalCache.containsKey(asset.getSecurityGuid())) {
            domain = (BaseSecurityDomain) CommomLocalCache.get(asset.getSecurityGuid());
        } else {
            try {
                VData<BaseSecurityDomain> singleDomain = authFeign.getOneDomainByCode(asset.getSecurityGuid());
                if ("0".equals(singleDomain.getCode()) && singleDomain.getData() != null) {
                    domain = singleDomain.getData();
                    CommomLocalCache.put(asset.getSecurityGuid(), domain, 12, TimeUnit.HOURS);
                }
            } catch (Exception e) {
                logger.error("getSingleDomain数据查询失败");
            }
        }
        return domain;
    }

    private static AssetTypeSno getAssetTypeSno(Asset asset) {
        AssetTypeSno assetTypeSno = null;

        if (CommomLocalCache.containsKey(asset.getAssetTypeSnoGuid())) {
            assetTypeSno = (AssetTypeSno) CommomLocalCache.get(asset.getAssetTypeSnoGuid());
        } else {
            assetTypeSno = assetTypeSnoService.getOne(asset.getAssetTypeSnoGuid());
            CommomLocalCache.put(asset.getAssetTypeSnoGuid(), assetTypeSno, 2, TimeUnit.HOURS);
        }
        return assetTypeSno;
    }

    private static AssetType getAssetType(Asset asset) {
        AssetType assetType = null;
        if (CommomLocalCache.containsKey(asset.getAssetType())) {
            assetType = (AssetType) CommomLocalCache.get(asset.getAssetType());
        } else {
            assetType = assetTypeService.getOne(asset.getAssetType());
            CommomLocalCache.put(asset.getAssetType(), assetType, 2, TimeUnit.HOURS);
        }
        return assetType;
    }

    public static void setDeviceInfoForAsset(DeviceInfo device) {
        try {
            if (StringUtils.isEmpty(device.getDeviceId())) {

                device.setDeviceName("未知");

                return;
            }

            Asset asset = getAsset(device);

            if (asset != null) {

                device.setDeviceIp(asset.getIp());
                device.setDeviceName(asset.getName());
                device.setDeviceMac(asset.getMac());

                device.setOrgCode(asset.getOrgCode());
                device.setOrgName(asset.getOrgName());

                PersonLiable personLiable = new PersonLiable();
                personLiable.setPersonLiableName(asset.getResponsibleName());
                personLiable.setPersonLiableCode(asset.getResponsibleCode());

                device.setPersonLiable(personLiable);

                if (StringUtils.isNotEmpty(asset.getSecurityGuid())) {
                    BaseSecurityDomain domain = getBaseSecurityDomain(asset);

                    if (domain != null) {
                        device.setDomainInfo(domain);
                        device.setDeviceSecurityDomain(domain.getDomainName());
                    }
                }
                AssetType assetType = getAssetType(asset);

                if (assetType != null) {
                    device.setDeviceType(assetType.getName());
                }

                AssetTypeSno assetTypeSno = getAssetTypeSno(asset);

                if (assetTypeSno != null) {
                    device.setDeviceBrand(assetTypeSno.getName());
                }

            } else {
                logger.error("getAssetDetail数据查询失败");
                return;
            }

//		
//		Result<AssetDetailVO> assetResult = getAssetDetail(device.getDeviceId());
//		if (assetResult.getCode() == 0) {
//			
//		} else {
//			logger.error("assetFegin  error:" + assetResult.getMsg()+"  \t"+device.getDeviceId());
//		}
        } catch (Exception e) {
            logger.error("assetFegin  error:", e);
        }
    }
}
