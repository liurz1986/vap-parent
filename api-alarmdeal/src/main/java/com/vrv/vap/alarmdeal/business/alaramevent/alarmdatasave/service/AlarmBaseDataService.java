package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.AssetExtendBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AppSysManagerCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetTypeCacheVo;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月21日 9:24
 */
public interface AlarmBaseDataService {
    /**
     * 初始化告警初始数据  资产
     * @param assets
     */
    public void initAlarmBaseForAsset(List<AssetCacheVo> assets);

    /**
     * 初始化告警初始数据   资产类型
     * @param assetTypes
     */
    public void initAlarmBaseForAssetType(List<AssetTypeCacheVo> assetTypes);

    /**
     * 初始化告警初始数据   资产扩展
     * @param assetExtends
     */
    public void initAlarmBaseForAssetExtend(List<AssetExtend> assetExtends);

    /**
     * 初始化告警初始数据   人员
     * @param basePersonZjgs
     */
    public void initAlarmBaseForBasePersonZjg(List<BasePersonZjg> basePersonZjgs);

    /**
     * 初始化告警初始数据   安全域
     * @param domains
     */
    public void initAlarmBaseForBaseSecurityDomain(List<BaseSecurityDomain> domains);

    /**
     * 初始化告警初始数据   系统
     * @param appSysManagers
     */
    public void initAlarmBaseForAppSysManager(List<AppSysManager> appSysManagers);

    /**
     * 通过ip查询资产
     * @param ip
     * @return com.vrv.vap.alarmdeal.business.asset.model.Asset
     */
    public AssetCacheVo queryAssetByIp(String ip);

    /**
     * 通过id查询资产类型
     * @param id
     * @return com.vrv.vap.alarmdeal.business.asset.model.AssetType
     */
    public String queryAssetByAssetId(String id);

    /**
     * 通过设备id查询资产扩展信息
     * @param guid
     * @return com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.AssetExtendBean
     */
    public AssetExtendBean queryAssetExtendData(String guid);

    /**
     * 通过用户编号查询用户信息
     * @param userNo
     * @return com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg
     */
    public BasePersonZjg queryPersonByUserNo(String userNo);

    /**
     * 通过id查询安全域信息
     * @param id
     * @return com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain
     */
    public BaseSecurityDomain queryDomianById(String id);

    /**
     * 通过app id查询应用信息
     * @param id
     * @return com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager
     */
    public AppSysManagerCacheVo qyeryAppSysManagerByAppId(String id);
}
