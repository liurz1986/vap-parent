package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetRedisCacheVO;

import java.util.List;

/**
 * 基础数据redis缓存：资产、应用系统
 * 资产
 * 1. 全量缓存
 * 2. ip键值缓存(ip键、资产信息为值)
 * 3. 应用编号键值缓存(应用编号键、资产信息为值)
 *
 * 应用系统：
 * 1. 全量缓存
 * 2. 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
 * 3. 应用编号键值缓存(应用编号键、资产信息为值)
 */
public interface BaseDataRedisCacheService {

    /**
     * 初始化所有的缓存
     * 资产、应用系统
     */
    public void initCache(boolean isSendMsg);

    /**
     * 资产(全量)：<数据同步自动入库、数据同步收入入库-批量同步、NTD数据同步>
     * 全量缓存
     * ip键值缓存(ip键、资产信息为值)
     * 应用编号键值缓存(应用编号键、资产信息为值)、
     * 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
     */
    public void updateAllAssetCache();

    /**
     * 应用系统(全量)：<数据同步自动入库、数据同步收入入库-批量同步>
     *  1. 全量
     *  2. 应用编号键值缓存(应用编号键、资产信息为值)、
     *  3. 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
     */
    public void updateAllAppCache();

    /**
     * 单个新增资产
     */
    public void addAsset(AssetRedisCacheVO asset);

    /**
     * 多个新增资产
     */
    public void addAssets(List<AssetRedisCacheVO> assets);

    /**
     * 编辑资产：资产全量缓存、ip键值资产，应用编号键值资产，ip键值应用系统
     */
    public void editAsset(AssetRedisCacheVO asset,String typeTreeCode,String oldIp);
    /**
     * 删除资产：资产全量缓存、ip键值资产，应用编号键值资产，ip键值应用系统
     */
    public void delAsset(String guid,String typeTreeCode);


    /**
     * 单个应用系统新增
     */
    public void addAppSysManager(AppSysManager appSysManager);

    /**
     * 批量应用系统新增
     */
    public void addAppSysManagers(List<AppSysManager> appSysManagers);

    /**
     * 应用系统编辑
     * @param appSysManager
     */
    public void editAppSysManager(AppSysManager appSysManager,String oldAppNo);

    /**
     * 应用系统删除
     * @param appSysManager
     */
    public void delAppSysManager( AppSysManager appSysManager);

    /**
     * 应用系统新增服务
     * @param appSysManager
     * @param serverIds 应用下新增的服务器
     */
    public void addServer(AppSysManager appSysManager, String serverIds);

    /**
     * 应用系统删除服务
     * @param appSysManager
     * @param serverIds 应用下删除的服务器
     */
    public void delServer(AppSysManager appSysManager, String serverIds);
}
