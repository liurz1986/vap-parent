package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.BaseDataRedisCacheService;
import com.vrv.vap.alarmdeal.business.asset.util.RedisCacheUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetRedisCacheVO;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础数据redis缓存：资产、应用系统
 * 资产
 * 1. 全量缓存
 * 2. ip键值缓存(ip键、资产信息为值)
 * 3. 应用编号键值缓存(应用编号键、资产信息为值)、 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
 *
 * 应用系统：
 * 1. 全量缓存
 * 2. 应用编号键值缓存(应用编号键、资产信息为值)、 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
 */
@Service
public class BaseDataRedisCacheServiceImpl implements BaseDataRedisCacheService {
    Logger logger = LoggerFactory.getLogger(BaseDataRedisCacheServiceImpl.class);
    @Autowired
    private AssetService assetService;
    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private MessageService messageService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public static final String serverTreeCode="asset-service-";  //服务器的treeCode

    public static boolean isExcCompensate = false;

    /**
     * 初始化所有的缓存
     * 1.项目启动、定时任务会用到
     * 资产、应用系统
     */
    @Override
    public void initCache(boolean isSendMsg){
        try{
            allAssetCaches();
            allAppCaches();
            allAppAndRefCache();
            if(isSendMsg){
                messageService.sendKafkaMsg("app");
                messageService.sendKafkaMsg("asset");
            }
        }catch (Exception e){
            logger.error("资产、应用相关redis全量缓存异常",e);
        }
    }

    private void initCacheThread(boolean isSendMsg){
        // 异步执行更新缓存，全量更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCache(isSendMsg);
            }
        }).start();
    }
    /**
     * 资产(全量)：<数据同步自动入库、数据同步收入入库-批量同步>
     * 全量缓存
     * ip键值缓存(ip键、资产信息为值)
     * 应用编号键值缓存(应用编号键、资产信息为值)、
     * 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
     */
    @Override
    public void updateAllAssetCache(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.debug("资产变动全量缓存开始");
                allAssetCaches();
                allAppAndRefCache();
                logger.debug("资产变动全量缓存结束");
            }
        }).start();
    }

    /**
     * 应用系统(全量)：<数据同步自动入库、数据同步收入入库-批量同步>
     *  1. 全量
     *  2. 应用编号键值缓存(应用编号键、资产信息为值)、
     *  3. 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
     */
    @Override
    public void updateAllAppCache(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.debug("应用系统变动全量缓存开始");
                allAppCaches();
                allAppAndRefCache();
                logger.debug("应用系统变动全量缓存结束");
            }
        }).start();
    }


    /**
     * 应用系统全量数据缓存更新：
     * 1. 全量缓存
     * 2 .应用编号键值缓存(应用编号键、资产信息为值)、 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
     */
    private void initAllAppCache(){
        // 异步执行更新缓存，全量更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.debug("异步更新资产相关缓存开始");
                allAppCaches();
                allAppAndRefCache();
                logger.debug("异步更新资产相关缓存完成");
            }
        }).start();
    }



    /**
     * 资产变动
     *
     * 全量缓存资产
     * ip键值缓存(ip键、资产信息为值)、
     */

    private void allAssetCaches() {
        List<AssetRedisCacheVO> allAssets = null;
        try{
            allAssets = getAllAssets();
            if(null == allAssets || allAssets.size() == 0){  // 对缓存有的资产进行清空
                logger.debug("当前资产为空不执行缓存,同时清除redis缓存中全量缓存资产，ip键值缓存数据");
                redisCacheUtil.delete(RedisCacheUtil.asset_all_redis_key);
                redisCacheUtil.deleteHash(RedisCacheUtil.asset_ip_redis_key);
                return;
            }
            // 全量缓存
            redisCacheUtil.delete(RedisCacheUtil.asset_all_redis_key);  // 清楚历史数据
            redisCacheUtil.save(RedisCacheUtil.asset_all_redis_key, JSON.toJSONString(allAssets));
            logger.debug("全量缓存资产完成共计："+allAssets.size()+"条");
            // ip键值缓存
            List<AssetRedisCacheVO> ipassets = allAssets.stream().filter(item->StringUtils.isNotEmpty(item.getIp())==true).collect(Collectors.toList());
            if(ipassets.size() == 0){ // 对缓存有的资产进行清空
                logger.debug("当前ip键值缓存数据没有查到");
                redisCacheUtil.deleteHash(RedisCacheUtil.asset_ip_redis_key);
                return;
            }
            redisCacheUtil.deleteHash(RedisCacheUtil.asset_ip_redis_key);  // 清楚历史数据
            Map<String,String> assetIps = new HashMap<>();
            for(AssetRedisCacheVO asset : ipassets){
                assetIps.put(asset.getIp(),JSON.toJSONString(asset));
            }
            redisCacheUtil.saveBatchHash(RedisCacheUtil.asset_ip_redis_key,assetIps);
            logger.debug("ip键值缓存数据完成共计："+assetIps.size()+"条");
        }catch(Exception e){
            logger.error("全量缓存资产、ip键值缓存(ip键、资产信息为值)异常",e);
            isExcCompensate = true;
        }finally {
            if(null != allAssets){
                allAssets.clear();
            }
        }
    }


    private List<AssetRedisCacheVO> getAssets(List<AssetRedisCacheVO> allAssets, String[] assetGuids) {
        List<AssetRedisCacheVO> assets = new ArrayList<>();
        for(String assetGuid : assetGuids){
            List<AssetRedisCacheVO> assetLists = allAssets.stream().filter(item->assetGuid.equals(item.getGuid())).collect(Collectors.toList());
            if(null == assetLists || assetLists.size() == 0){
                logger.debug("没有查到对应的资产信息，"+assetGuid);
                continue;
            }
            assets.add(assetLists.get(0));
        }
        return  assets;
    }

    /**
     * 应用系统全量数据缓存
     */
    private void allAppCaches() {
        List<AppSysManager> appSysManagers = null;
        try{
            appSysManagers = appSysManagerService.findAll();
            if(null == appSysManagers || appSysManagers.size() == 0){
                logger.debug("应用系统数据为空");
                redisCacheUtil.delete(RedisCacheUtil.app_all_redis_key);   // 没有数据，删除redis缓存中数据
                return;
            }
            redisCacheUtil.delete(RedisCacheUtil.app_all_redis_key);   // 清空历史数据
            redisCacheUtil.save(RedisCacheUtil.app_all_redis_key,JSON.toJSONString(appSysManagers));
            logger.debug("应用系统全量数据缓存数据完成共计："+appSysManagers.size()+"条");
        }catch(Exception e){
            logger.error("应用系统全量数据异常",e);
            isExcCompensate = true;
        }finally {
            if(null != appSysManagers){
                appSysManagers.clear();
            }
        }
    }

    /**
     * 根据ip组装数据
     * @param list
     * @param ipToApps
     * @param app
     */
    private void appAndIpDataHandle(List<AssetRedisCacheVO> list, Map<String, List<AppSysManager>> ipToApps, AppSysManager app) {
        for(AssetRedisCacheVO asset : list){
            String ip = asset.getIp();
            List<AppSysManager> apps = ipToApps.get(ip);
            if(null != apps && apps.size() > 0){
                if(apps.contains(app)){
                    continue;
                }
                apps.add(app);
                ipToApps.put(ip,apps);
            }else{
                apps = new ArrayList<>();
                apps.add(app);
                ipToApps.put(ip,apps);
            }
        }
    }
    /**
     * 应用编号键值缓、设备ip键值缓存数据
     * 涉及场景：
     * 1.应用系统新增、删除服务器；
     * 2.资产界面删除、编辑(资产涉及到服务器)；
     * 3.应用系统修改、删除
     *
     * 设备ip键值缓存(设备ip作为键，应用系统数据作为值)
     * 应用编号键值缓存(应用编号键、资产信息为值)
     */
    private void allAppAndRefCache(){
        List<AppSysManager> appSysManagers = null;
        List<AssetRedisCacheVO> allAssets = null;
        try{
            // 获取里面含有资产的应用系统
            List<QueryCondition> conditionList=new ArrayList<>();
            conditionList.add(QueryCondition.notNull("serviceId"));
            appSysManagers = appSysManagerService.findAll(conditionList);
            if(null == appSysManagers || appSysManagers.size() == 0){
                logger.debug("应用系统数据为空，清除redis中设备ip键值缓存、应用编号键值缓存");
                clearRedisCache(RedisCacheUtil.app_ip_redis_key);
                clearRedisCache(RedisCacheUtil.app_appno_redis_key);
                return;
            }
            // 获取服务类型的资产
            allAssets = getServerAssets();
            if(null == allAssets || allAssets.size() == 0){
                logger.debug("获取服务类型的资产数据为空，清除redis中设备ip键值缓存、应用编号键值缓存");
                clearRedisCache(RedisCacheUtil.app_ip_redis_key);
                clearRedisCache(RedisCacheUtil.app_appno_redis_key);
                return;
            }
            // 获取应用系统下对应的资产信息
            Map<String,List<AppSysManager>> ipToApps = new HashMap<>();  // ip对应的应用系统
            Map<String,List<AssetRedisCacheVO>> datas = queryAssetByAppSysManager(appSysManagers,ipToApps,allAssets);
            // 没有数据删除redis缓存中信息
            if(datas.size()  == 0){
                logger.debug("应用系统下没有找到资产信息，清除redis中设备ip键值缓存、应用编号键值缓存");
                clearRedisCache(RedisCacheUtil.app_ip_redis_key);
                clearRedisCache(RedisCacheUtil.app_appno_redis_key);
                return;
            }
            // 应用编号键值缓存
            allAppNoAndAssetUpdateCache(datas);
            // 设备ip键值应用系统缓存
            allIpAndAppUpdateCache(ipToApps);
        }catch(Exception e){
            logger.error("应用编号键值缓、设备ip键值缓存数据异常",e);
            isExcCompensate = true;
        }finally {
            if(null != allAssets){
                allAssets.clear();
            }
            if(null != appSysManagers){
                appSysManagers.clear();
            }
        }
    }

    /**
     * 应用编号键值资产全量缓存
     * @param datas
     */
    private void allAppNoAndAssetUpdateCache(Map<String, List<AssetRedisCacheVO>> datas) {
        redisCacheUtil.deleteHash(RedisCacheUtil.app_appno_redis_key); // 清楚历史数据缓存
        Map<String,String> appnoRedisDatas = new HashMap<>();
        for(Map.Entry<String,List<AssetRedisCacheVO>> entry : datas.entrySet()){
            List<AssetRedisCacheVO> assets=entry.getValue();
            String appNo = entry.getKey();
            appnoRedisDatas.put(appNo,JSON.toJSONString(assets));
        }
        redisCacheUtil.saveBatchHash(RedisCacheUtil.app_appno_redis_key,appnoRedisDatas); // 更新缓存
    }

    /**
     * ip键值应用系统缓存全量更新
     * @param ipToApps
     */
    private void allIpAndAppUpdateCache(Map<String, List<AppSysManager>> ipToApps) {
        redisCacheUtil.deleteHash(RedisCacheUtil.app_ip_redis_key); // 清楚历史数据缓存
        Map<String,String> ipRedisDatas = new HashMap<>();
        for(Map.Entry<String,List<AppSysManager>> entry : ipToApps.entrySet()){
            List<AppSysManager> apps=entry.getValue();
            String ip = entry.getKey();
            ipRedisDatas.put(ip,JSON.toJSONString(apps));
        }
        redisCacheUtil.saveBatchHash(RedisCacheUtil.app_ip_redis_key,ipRedisDatas); // 更新缓存
    }

    private void clearRedisCache(String redisKey) {
        if(StringUtils.isNotEmpty(redisKey)){
            redisCacheUtil.deleteHash(redisKey);
        }
    }



    private Map<String, List<AssetRedisCacheVO>> queryAssetByAppSysManager(List<AppSysManager> appSysManagers, Map<String, List<AppSysManager>> ipToApps, List<AssetRedisCacheVO> allAssets) {
        Map<String,List<AssetRedisCacheVO>> datas = new HashMap<>(); // 应用系统对应的资产
        for(AppSysManager app : appSysManagers){
            String appNo = app.getAppNo();
            String serviceId = app.getServiceId();
            if(StringUtils.isEmpty(serviceId)){
                continue;
            }
            String[] assetGuids = serviceId.split(",");
            List<AssetRedisCacheVO> list = getAssets(allAssets,assetGuids);
            if(list.size() > 0){
                datas.put(appNo,list);
                // 根据ip组装数据
                appAndIpDataHandle(list,ipToApps,app);
            }
        }
        return datas;
    }


    /**
     * 单个新增资产
     */
    public void addAsset(AssetRedisCacheVO asset){
        if(null == asset){
            return;
        }
        List<AssetRedisCacheVO> assets = new ArrayList<>();
        assets.add(asset);
        addAssets(assets);
    }
    /**
     * 新增资产
     */
    @Override
    public void addAssets(List<AssetRedisCacheVO> assets) {
        try{
            if(null == assets || assets.size() == 0){
                return;
            }
            // 获取历史全量资产数据
            Object data = redisCacheUtil.get(RedisCacheUtil.asset_all_redis_key);
            if(null != data) {
                List<AssetRedisCacheVO> oldAssets = JSONArray.parseArray(data.toString(), AssetRedisCacheVO.class);
                oldAssets.addAll(assets);
                redisCacheUtil.save(RedisCacheUtil.asset_all_redis_key, JSON.toJSONString(oldAssets));
            }else{
                redisCacheUtil.save(RedisCacheUtil.asset_all_redis_key, JSON.toJSONString(assets));
            }
            Map<String,String> assetIps = new HashMap<>();
            // 增加ip键值缓存
            for(AssetRedisCacheVO asset : assets){
                if(StringUtils.isEmpty(asset.getIp())){ // ip为空的不处理
                    continue;
                }
                assetIps.put(asset.getIp(),JSON.toJSONString(asset));
            }
            redisCacheUtil.saveBatchHash(RedisCacheUtil.asset_ip_redis_key,assetIps);
        }catch(Exception e){
            logger.error("新增资产时，执行缓存异常",e);
            isExcCompensate = true;
        }


    }


    /**
     * 编辑资产：资产全量缓存、ip键值资产，应用编号键值资产，ip键值应用系统
     * 涉及到服务器的：需要改动应用编号键值资产，ip键值应用系统
     */
    @Override
    public void editAsset(AssetRedisCacheVO asset,String typeTreeCode,String oldIp){
        try{
            if(null == asset){
                return;
            };
            // 获取历史全量资产数据
            Object data = redisCacheUtil.get(RedisCacheUtil.asset_all_redis_key);
            if(null == data){
                logger.info("资产编辑缓存时，没有查到对应资产，重新初始化缓存：资产全量，ip键值资产缓存");
                updateAllAssetCache(); // 重新更新全量缓存数据
                return;
            }
            List<AssetRedisCacheVO> oldDatas = JSONArray.parseArray(data.toString(), AssetRedisCacheVO.class);
            List<AssetRedisCacheVO> assetOld = getEiditAssetByGuid(oldDatas,asset.getGuid());
            if(assetOld.size() > 0){
                oldDatas.removeAll(assetOld);
                oldDatas.add(asset);
                redisCacheUtil.save(RedisCacheUtil.asset_all_redis_key, JSON.toJSONString(oldDatas));
            }
            // 获取ip对应资产
            String ip = asset.getIp();
            // ip键值资产处理
            ipKeyValueAsset(ip,oldIp,asset);
            // 应用系统涉及缓存修改：正对服务类型资产
            if(typeTreeCode.contains(serverTreeCode)){
                appSysManagerCacheUpdate(asset,oldIp);
            }
        }catch(Exception e) {
            logger.error("资产编辑缓存异常", e);
            // 重新更新全量缓存数据
            isExcCompensate = true;

        }
    }


    /**
     * ip键值资产处理
     * @param ip
     * @param oldIp
     * @param asset
     */
    private void ipKeyValueAsset(String ip, String oldIp, AssetRedisCacheVO asset) {
        // 修改前ip和当前ip都为空不处理
        if(StringUtils.isEmpty(oldIp) && StringUtils.isEmpty(ip)){
            return;
        }
        // 修改前ip为空和当前ip不为空:直接更新
        if(StringUtils.isEmpty(oldIp) && !StringUtils.isEmpty(ip)){
            redisCacheUtil.saveHash(RedisCacheUtil.asset_ip_redis_key,ip,JSON.toJSONString(asset));  // 更新ip对应的资产
        }
        // 修改前ip不为空和当前ip为空：删除历史ip数据
        if(!StringUtils.isEmpty(oldIp) && StringUtils.isEmpty(ip)){
            redisCacheUtil.deleteHash(RedisCacheUtil.asset_ip_redis_key,oldIp); // 刪除历史ip数据
        }
        // 修改前后ip都不为空
        // 刪除历史ip数据
        redisCacheUtil.deleteHash(RedisCacheUtil.asset_ip_redis_key,oldIp);
        // 新增新ip对应的资产
        redisCacheUtil.saveHash(RedisCacheUtil.asset_ip_redis_key,ip,JSON.toJSONString(asset));

    }

    /**
     * 单个资产修改：涉及应用系统修改,ip键值应用系统，应用编号键值资产
     * @param asset
     */
    private void appSysManagerCacheUpdate(AssetRedisCacheVO asset ,String oldIp) {
        List<QueryCondition> conditionList=new ArrayList<>();
        conditionList.add(QueryCondition.like("serviceId",asset.getGuid()));
        List<AppSysManager> appSysManagerList=appSysManagerService.findAll(conditionList);
        if(null == appSysManagerList || appSysManagerList.size() == 0){  // 没有资产下没有应用系统
            return;
        }
        // ip键值应用系统
        if(!StringUtils.isEmpty(oldIp)){
            redisCacheUtil.deleteHash(RedisCacheUtil.app_ip_redis_key,oldIp); // 刪除历史ip数据
        }
        redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(appSysManagerList));
        // 应用编号键值资产
        appNoKeyValueAsset(appSysManagerList,asset);
    }

    private void appNoKeyValueAsset(List<AppSysManager> appSysManagerList,AssetRedisCacheVO asset) {
        String guid = asset.getGuid();
        for(AppSysManager app : appSysManagerList){
            Object data = redisCacheUtil.getHash(RedisCacheUtil.app_appno_redis_key,app.getAppNo());
            List<AssetRedisCacheVO> assets = JSONArray.parseArray(data.toString(), AssetRedisCacheVO.class);
            AssetRedisCacheVO assetOld =getAssetByGuid(assets,guid);
            assets.remove(assetOld);
            assets.add(asset);
            redisCacheUtil.saveHash(RedisCacheUtil.app_appno_redis_key,app.getAppNo(),JSON.toJSONString(assets));
        }
    }

    private  List<AssetRedisCacheVO> getEiditAssetByGuid(List<AssetRedisCacheVO> oldDatas, String guid) {
        List<AssetRedisCacheVO> assets = new ArrayList<>();
        for(AssetRedisCacheVO asset : oldDatas){
            if(guid.equalsIgnoreCase(asset.getGuid())){
                assets.add(asset);
            }
        }
        return assets;
    }

    private AssetRedisCacheVO getAssetByGuid(List<AssetRedisCacheVO> oldDatas, String guid) {
        for(AssetRedisCacheVO asset : oldDatas){
            if(guid.equalsIgnoreCase(asset.getGuid())){
                return  asset;
            }
        }
        return null;
    }

    /**
     * 删除资产：
     * 资产全量缓存、ip键值资产，
     * 应用编号键值资产，ip键值应用系统，应用编号全量
     */
    @Override
    public void delAsset(String guid,String typeTreeCode){
        try{
            if(StringUtils.isEmpty(guid)){
                return;
            }
            // 获取历史全量资产数据
            Object data = redisCacheUtil.get(RedisCacheUtil.asset_all_redis_key);
            if(null == data){ // 如果没有数据，说明缓存有问题，初始化缓存：资产全量，ip键值资产
                initCacheThread(false);  // 重新更新全量缓存数据
                return;
            }
            List<AssetRedisCacheVO> oldDatas = JSONArray.parseArray(data.toString(), AssetRedisCacheVO.class);
            AssetRedisCacheVO assetOld = getAssetByGuid(oldDatas,guid);
            if(null != assetOld){
                oldDatas.remove(assetOld);
                redisCacheUtil.save(RedisCacheUtil.asset_all_redis_key,JSON.toJSONString(oldDatas));
                String oldIp = assetOld.getIp();
                if(!StringUtils.isEmpty(oldIp)){
                    redisCacheUtil.deleteHash(RedisCacheUtil.asset_ip_redis_key,oldIp);
                }
            }
            // 服务器资产需要进行应用编号键值资产，ip键值应用系统缓存处理、应用系统全量
            if(typeTreeCode.contains(serverTreeCode)){
                updateAllAppCache();
            }
        }catch(Exception e){
            logger.error("资产删除缓存异常",e);
            // 重新更新全量缓存数据
            isExcCompensate = true;
        }
    }
    /**
     * 新增应用系统场景
     * 1. 全量更新应用系统表
     * @param appSysManager
     */
    @Override
    public void addAppSysManager(AppSysManager appSysManager) {
        if(null == appSysManager){
            return;
        }
        List<AppSysManager> apps = new ArrayList<>();
        apps.add(appSysManager);
        addAppSysManagers(apps);
    }

    /**
     * 批量新增应用系统
     * 1. 全量更新应用系统表
     * @param appSysManagers
     */
    @Override
    public void addAppSysManagers(List<AppSysManager> appSysManagers) {
        try{
            if(null == appSysManagers|| appSysManagers.size() == 0 ){
                return;
            }
            // 获取历史全量数据
            Object data = redisCacheUtil.get(RedisCacheUtil.app_all_redis_key);
            if(null != data) {
                List<AppSysManager> oldAssets = JSONArray.parseArray(data.toString(), AppSysManager.class);
                oldAssets.addAll(appSysManagers);
                redisCacheUtil.save(RedisCacheUtil.app_all_redis_key, JSON.toJSONString(oldAssets));
                return;
            }
            redisCacheUtil.save(RedisCacheUtil.app_all_redis_key, JSON.toJSONString(appSysManagers));
        }catch(Exception e){
            logger.error("新增应用系统时，执行缓存异常",e);
            isExcCompensate = true;
        }
    }

    /**
     * 应用系统编辑；
     * 1.应用全量更新
     * 2.ip键值应用系统更新(应用下有资产)
     * 3.应用编号键值资产（应用下有资产）
     * @param appSysManager
     */
    @Override
    public void editAppSysManager(AppSysManager appSysManager,String oldAppNo) {
        try{
            // 获取历史全量数据
            Object data = redisCacheUtil.get(RedisCacheUtil.app_all_redis_key);
            if(null == data) {
                initAllAppCache();  // 如果没有数据，应用系统相关的数据重新全部缓存
                return;
            }
            List<AppSysManager> oldAssets = JSONArray.parseArray(data.toString(), AppSysManager.class);
            if(oldAssets.size() == 0){
                initAllAppCache();  // 如果没有数据，应用系统相关的数据重新全部缓存
                return;
            }
            List<AppSysManager> queryAsset = oldAssets.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            if(queryAsset.size() == 0){
                initAllAppCache();  // 如果没有数据，应用系统相关的数据重新全部缓存
                return;
            }
            oldAssets.removeAll(queryAsset);
            oldAssets.add(appSysManager);
            redisCacheUtil.save(RedisCacheUtil.app_all_redis_key, JSON.toJSONString(oldAssets));
            // 应用系统没有资产，关联缓存不执行
            if(StringUtils.isEmpty(appSysManager.getServiceId())){
                return;
            }
            // ip键值应用系统缓存
            String[] guids =appSysManager.getServiceId().split(","); // 应用系统下所有的关联资产
            List<AssetRedisCacheVO> allAssets =getAssetByGuids(Arrays.asList(guids));
            if(null == allAssets || allAssets.size() == 0){
                initAllAppCache();
                return;
            }
            updateIpAndAppSysManagerCache(allAssets,guids,appSysManager);
            // 应用编号键值资产:应用编号修改了更新应用编号键值资产
            if (oldAppNo.equals(appSysManager.getAppNo())){
                return;
            }
            redisCacheUtil.deleteHash(RedisCacheUtil.app_appno_redis_key,oldAppNo);  //删除缓存中修改前的应用编号数据
            redisCacheUtil.saveHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo(),JSON.toJSONString(allAssets)); // 保存当前资产
        }catch(Exception e){
            logger.error(" 应用系统编辑时，执行缓存异常",e);
            isExcCompensate = true;
        }
    }



    /**
     * 应用系统删除
     * @param appSysManager
     *
     * 1.应用全量更新
     * 2.ip键值应用系统更新(应用下有资产)
     * 3.应用编号键值资产（应用下有资产）
     */
    @Override
    public void delAppSysManager(AppSysManager appSysManager){
        try{
            // 获取历史全量数据
            Object data = redisCacheUtil.get(RedisCacheUtil.app_all_redis_key);
            if(null == data){
                initAllAppCache(); // 如果没有数据，应用系统相关的数据重新全部缓存
                return;
            }
            List<AppSysManager> oldApps = JSONArray.parseArray(data.toString(), AppSysManager.class);
            List<AppSysManager> queryApp = oldApps.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            oldApps.removeAll(queryApp);
            if(oldApps.size() > 0){
                redisCacheUtil.save(RedisCacheUtil.app_all_redis_key, JSON.toJSONString(oldApps));
            }else{
                redisCacheUtil.delete(RedisCacheUtil.app_all_redis_key); // 没有了直接删除
            }
            // 应用系统没有资产，关联缓存不执行
            if(StringUtils.isEmpty(appSysManager.getServiceId())){
                return;
            }
            // ip键值应用系统缓存删除
            String[] guids =appSysManager.getServiceId().split(",");
            deleteAppByAssetGuids(guids,appSysManager);
            // 应用系统键值资产缓存删除
            redisCacheUtil.deleteHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo());
        }catch(Exception e){
            logger.error("应用系统删除时，执行缓存异常",e);
            isExcCompensate = true;
        }
    }

    /**
     * 应用系统新增服务器
     * 1. 应用系统全量更新
     * 2. ip键值应用系统更新
     * 3. 应用编号键值资产
     * @param appSysManager
     * @param serverIds 应用下所有服务器
     */
    @Override
    public void addServer(AppSysManager appSysManager, String serverIds){
        try{
            // 获取历史全量数据
            Object data = redisCacheUtil.get(RedisCacheUtil.app_all_redis_key);
            if(null ==  data){
                // 如果没有数据，应用系统相关的数据重新全部缓存
                initAllAppCache();
                return;
            }
            List<AppSysManager> oldAssets = JSONArray.parseArray(data.toString(), AppSysManager.class);
            List<AppSysManager> queryAsset = oldAssets.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            if(queryAsset.size() > 0){
                oldAssets.removeAll(queryAsset);
            }
            oldAssets.add(appSysManager);
            redisCacheUtil.save(RedisCacheUtil.app_all_redis_key, JSON.toJSONString(oldAssets));
            if(StringUtils.isEmpty(serverIds)){
                return;
            }
            String[] guids =serverIds.split(",");
            List<AssetRedisCacheVO> allAssets = getAssetByGuids(Arrays.asList(guids));
            if(null == allAssets || allAssets.size() == 0){
                initAllAppCache();
                return;
            }
            // 应用编号键值资产
            redisCacheUtil.deleteHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo()); // 删除当前应用编号下的资产缓存
            redisCacheUtil.saveHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo(),JSON.toJSONString(allAssets)); //更新应用编号系资产
            // ip键值应用系统更新
            addIpAndAppSysManagerCache(allAssets,guids,appSysManager);
        }catch(Exception e){
            logger.error("应用系统新增服务器时，执行缓存异常",e);
            isExcCompensate = true;
        }
    }

    /**
     * 更新ip键值应用系统更新
     * @param allAssets
     * @param guids
     * @param appSysManager
     */
    private void addIpAndAppSysManagerCache(List<AssetRedisCacheVO> allAssets, String[] guids, AppSysManager appSysManager) {
        for(String guid : guids){
            AssetRedisCacheVO asset = getAssetByGuid(allAssets,guid);
            if(null == asset){
                continue;
            }
            Object ipDatas = redisCacheUtil.getHash(RedisCacheUtil.app_ip_redis_key,asset.getIp());
            if(null == ipDatas){
                List<AppSysManager> apps = new ArrayList<>();
                apps.add(appSysManager);
                redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(apps));
                return;
            }
            List<AppSysManager> ipApps = JSONArray.parseArray(ipDatas.toString(), AppSysManager.class);
            if(null == ipApps || ipApps.size() == 0){
                List<AppSysManager> apps = new ArrayList<>();
                apps.add(appSysManager);
                redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(apps));
                return;
            }
            List<AppSysManager> oldApps = ipApps.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            ipApps.removeAll(oldApps);
            ipApps.add(appSysManager);
            redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(ipApps));
        }
    }

    /**
     * 应用系统编辑；ip键值应用系统更新缓存
     * @param allAssets
     * @param guids
     * @param appSysManager
     */
    private void updateIpAndAppSysManagerCache(List<AssetRedisCacheVO> allAssets, String[] guids, AppSysManager appSysManager) {
        for(String guid : guids){
            AssetRedisCacheVO asset = getAssetByGuid(allAssets,guid);
            if(null == asset){
                continue;
            }
            Object ipAppObjet = redisCacheUtil.getHash(RedisCacheUtil.app_ip_redis_key,asset.getIp());
            if(null == ipAppObjet){
                List<AppSysManager> apps = new ArrayList<>();
                apps.add(appSysManager);
                redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(apps));
                return;
            }
            List<AppSysManager> ipApps = JSONArray.parseArray(ipAppObjet.toString(), AppSysManager.class);
            List<AppSysManager> queryApp = ipApps.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            ipApps.removeAll(queryApp);
            ipApps.add(appSysManager);
            redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(ipApps));
        }
    }


    /**
     * 应用系统删除服务器
     * 涉及场景：
     * 1.应用系统全量缓存
     * 2.ip键值应用系统缓存
     * 3.应用编号键值资产缓存
     * @param appSysManager
     * @param delServerIds 应用下删除的服务器
     */
    @Override
    public void delServer(AppSysManager appSysManager, String delServerIds){
        try{
            // 获取历史全量数据
            Object data = redisCacheUtil.get(RedisCacheUtil.app_all_redis_key);
            if(null ==  data){
                initAllAppCache(); // 如果没有数据，应用系统相关的数据重新全部缓存
                return;
            }
            List<AppSysManager> oldAssets = JSONArray.parseArray(data.toString(), AppSysManager.class);
            List<AppSysManager> queryAsset = oldAssets.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            if(queryAsset.size() > 0){
                oldAssets.removeAll(queryAsset);
            }
            oldAssets.add(appSysManager);
            redisCacheUtil.save(RedisCacheUtil.app_all_redis_key, JSON.toJSONString(oldAssets));
            //应用系统当前的服务器
            String serverIds = appSysManager.getServiceId();
            String[] delGuids =delServerIds.split(",");
            // 当前应用下面没有服务器了，直接删除应用编号键值资产缓存;
            if(StringUtils.isEmpty(serverIds)){
                redisCacheUtil.deleteHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo());
                deleteAppByAssetGuids(delGuids,appSysManager); // 删除服务器：更新ip键值应用系统缓存
                return;
            }
            Object appNoDatas = redisCacheUtil.getHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo());
            if(null != appNoDatas){
                List<AssetRedisCacheVO> appNoAssets = JSONArray.parseArray(appNoDatas.toString(), AssetRedisCacheVO.class);
                List<AssetRedisCacheVO> removeAssets =  getDelAssets(appNoAssets,delGuids);
                appNoAssets.removeAll(removeAssets);
                redisCacheUtil.saveHash(RedisCacheUtil.app_appno_redis_key,appSysManager.getAppNo(),JSON.toJSONString(appNoAssets));
            }
            deleteAppByAssetGuids(delGuids,appSysManager); // 删除服务器：更新ip键值应用系统缓存
        }catch(Exception e){
            logger.error("应用系统删除服务器时，执行缓存异常",e);
            isExcCompensate = true;
        }
    }


    /**
     * 删除应用系统、删除服务器：更新ip键值应用系统缓存
     * @param delGuids
     * @param appSysManager
     */
    private void deleteAppByAssetGuids(String[] delGuids, AppSysManager appSysManager) {
        if(null == delGuids || delGuids.length == 0){
            return;
        }
        List<AssetRedisCacheVO> allAssets = getAssetByGuids(Arrays.asList(delGuids));
        if(null == allAssets || allAssets.size() == 0){
            initAllAppCache();
            return;
        }
        for(String guid : delGuids){
            AssetRedisCacheVO asset = getAssetByGuid(allAssets,guid);
            Object ipAppObjet = redisCacheUtil.getHash(RedisCacheUtil.app_ip_redis_key,asset.getIp());
            if(null == ipAppObjet){
                continue;
            }
            List<AppSysManager> ipApps = JSONArray.parseArray(ipAppObjet.toString(), AppSysManager.class);
            List<AppSysManager> queryApp = ipApps.stream().filter(item -> item.getId().equals(appSysManager.getId())).collect(Collectors.toList());
            ipApps.removeAll(queryApp);
            if(ipApps.size() > 0){
                redisCacheUtil.saveHash(RedisCacheUtil.app_ip_redis_key,asset.getIp(),JSON.toJSONString(ipApps));
            }else{
                redisCacheUtil.deleteHash(RedisCacheUtil.app_ip_redis_key,asset.getIp());  // 没有了直接删除
            }
        }
    }

    private List<AssetRedisCacheVO> getDelAssets(List<AssetRedisCacheVO> appNoAssets, String[] delGuids) {
        List<AssetRedisCacheVO> deles = new ArrayList<>();
        for(String guid : delGuids){
            AssetRedisCacheVO asset = getAssetByGuid(appNoAssets,guid);
            if(null == asset){
                continue;
            }
            deles.add(asset);
        }
        return deles;
    }

    private String queryColume(){
        String sql ="select asset.Guid as guid,asset.Name as name,asset.Type_Guid as assetType,asset.ip,asset.securityGuid,asset.CreateTime as createTime,asset.employee_guid as employeeGuid," +
                " asset.domain_sub_code as domainSubCode,asset.equipment_intensive as equipmentIntensive,asset.serial_number as serialNumber,asset.org_name as orgName,asset.org_code as orgCode," +
                " asset.responsible_name as responsibleName,asset.responsible_code as responsibleCode,asset.term_type as termType,asset.ismonitor_agent as isMonitorAgent," +
                " asset.os_setup_time as osSetuptime,asset.os_list as osList ,asset.terminal_Type as terminalType ,asset.domain_name as domainName," +
                " asset.clinet_time_difference as clinetTimeDifference,asset.data_source_type as dataSourceType,asset.sync_source as syncSource,asset.sync_uid as syncUid,asset.pid as pid," +
                " asset.vid as vid,asset.operation_url as operationUrl,atype.name as typeName, agroup.name as groupName ";
        return sql;
    }
    /**
     * 获取所有的资产信息
     * @return
     */
    private List<AssetRedisCacheVO> getAllAssets() {
        String sql= queryColume()+
                " from asset " +
                " left join asset_type as atype on asset.Type_Guid=atype.Guid" +
                " left join asset_type_group as agroup on atype.TreeCode LIKE CONCAT(agroup.`TreeCode`,'-%') ";
        List<AssetRedisCacheVO> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetRedisCacheVO>(AssetRedisCacheVO.class));
        return list;
    }
    /**
     * 获取服务器类型资产
     * @return
     */
    private List<AssetRedisCacheVO> getServerAssets() {
        String sql=queryColume() +
                " from asset " +
                " left join asset_type as atype on asset.Type_Guid=atype.Guid" +
                " left join asset_type_group as agroup on atype.TreeCode LIKE CONCAT(agroup.`TreeCode`,'-%') "+
                " where agroup.TreeCode='asset-service'";
        List<AssetRedisCacheVO> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetRedisCacheVO>(AssetRedisCacheVO.class));
        return list;
    }

    /**
     * 获取资产信息
     * @return
     */
    private List<AssetRedisCacheVO> getAssetByGuids(List<String> guids) {
        String sql=queryColume() +
                " from asset " +
                " left join asset_type as atype on asset.Type_Guid=atype.Guid" +
                " left join asset_type_group as agroup on atype.TreeCode LIKE CONCAT(agroup.`TreeCode`,'-%') "+
                " and asset.Guid in ('" + StringUtils.join(guids, "','") + "')";
        List<AssetRedisCacheVO> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetRedisCacheVO>(AssetRedisCacheVO.class));
        return list;
    }
}
