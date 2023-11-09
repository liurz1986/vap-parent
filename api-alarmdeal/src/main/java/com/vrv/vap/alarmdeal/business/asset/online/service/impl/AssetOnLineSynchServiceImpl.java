package com.vrv.vap.alarmdeal.business.asset.online.service.impl;

import com.vrv.vap.alarmdeal.business.asset.online.constant.AssetChangeConstant;
import com.vrv.vap.alarmdeal.business.asset.online.constant.AssetOnlineConstant;
import com.vrv.vap.alarmdeal.business.asset.online.model.AssetChange;
import com.vrv.vap.alarmdeal.business.asset.online.model.AssetOnLine;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetChangeService;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineService;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineSynchService;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetQueryVO;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 执行数据处理
 */
@Service
@Transactional
public class AssetOnLineSynchServiceImpl implements AssetOnLineSynchService {
    private static Logger logger = LoggerFactory.getLogger(AssetOnLineSynchServiceImpl.class);
    @Autowired
    private AssetOnLineService  assetOnLineService;
    @Autowired
    private AssetChangeService assetChangeService;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    // 资产在线同步资产变更表处理方式：1.对接入的数据，只要变化就做新增处理，2.变更表数据做删除更新处理
    @Value("${asset.online.change.setting:1}")
    private String assetChangeHandle;
    // 所有资产
    private List<AssetQueryVO>  allAssets= null;

    // 所有在线数据
    private List<AssetOnLine> assetOnLines=null;

    // 所有变更数据
    private List<AssetChange>  assetChanges= null;

    private List<String> assetChangeDelGuids = null;  // 删除变更表数据

    private List<AssetChange> newAssetChanges = null; // 新增变更表记录

    private List<AssetChange> updateAssetChanges = null; // 修改变更表记录

    private List<AssetOnLine> saveAssetOnLines = null; // 保存的资产在线数据

    /**
     * 实时处理kafka资产在线数据
     *
     * @param assetOnLineVos
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public void excSynchData(List<AssetOnLineVO> assetOnLineVos)  {
        try{
            // ip、资产类型过滤：
            logger.info("开始处理kafka资产在线数据,处理数据size："+assetOnLineVos.size());
            logger.debug("ip、资产类型guid为空过滤");
            List<AssetOnLineVO> filterDatas =  assetOnLineVos.stream().filter(item -> StringUtils.isNotEmpty(item.getIp())&&StringUtils.isNotEmpty(item.getTypeGuid())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(filterDatas )){
                logger.warn("没有要处理的数据，其中数据ip或资产类型为空");
                return;
            }
            // 数据处理
            logger.debug("数据处理");
            assetOnlineDatasHandle(filterDatas);
            // 数据存储
            logger.debug("数据存储");
            saveDatas();
            // 数据清理
            datasClear();
            logger.info("结束处理kafka资产在线数据");
        }catch (Exception e){
            datasClear();
            logger.error("kafka资产在线数据处理异常",e);
        }
    }


    /**
     * 数据处理
     * 1. 资产在线表存在场景
     * 2. 资产在线表不存在场景
     *
     * @param fliterDatas
     */
    private void assetOnlineDatasHandle(List<AssetOnLineVO> fliterDatas) throws ExecutionException, InterruptedException {
        // 初始化数据
        initData();
        // 循环处理
       for(AssetOnLineVO data : fliterDatas){
           assetOnLineHandle(data);
       }
    }

    /**
     * 初始化数据
     */
    private void initData() throws ExecutionException, InterruptedException {
        // 所有在线数据
        assetOnLines =assetOnLineService.findAll();
        // 所有资产数据
        allAssets = assetOnLineService.getAllAssetsFuture().get();
        // 所有变更表数据
        assetChanges = assetChangeService.getAllAssetChangesFuture().get();
        assetChangeDelGuids = new ArrayList<>();
        newAssetChanges = new ArrayList<>();
        updateAssetChanges = new ArrayList<>();
        saveAssetOnLines = new ArrayList<>();
    }

    // 单个数据处理
    private void assetOnLineHandle(AssetOnLineVO data) {
        String ip  = data.getIp();
        AssetOnLine  assetOnlineOld  = getAssetOnlineByIp(ip);
        if(null != assetOnlineOld){
            // 取消(存在的数据，已经执行导入台账的，数据不处理 2022-11-24)还原到以前逻辑 2022-12-2
            // 存在更新处理
             updateAssetOnLine(data,assetOnlineOld);
        }else{
            // 不存在新增处理
            newsaveAssetOnLine(data);
        }
    }



    /**
     * 存在更新处理
     * 1. 保留导入状态
     * 2. 删除状态更改为非删除状态
     * @param data
     * @param assetOnlineOld
     */
    private void updateAssetOnLine(AssetOnLineVO data, AssetOnLine assetOnlineOld) {
         AssetOnLine assetOnLine =  getUpdateAssetOnline(data,assetOnlineOld);
         saveAssetOnLines.add(assetOnLine);
         AssetQueryVO asset  = getAssetByIp(data.getIp());
         // 存在变更数据，与历史数据进行比较，根据逻辑进行修改、删除、新增(老逻辑)
         if("2".equals(assetChangeHandle)){
             complexAssetChangeHandle(asset,data);
         }else{
           // 存在变更数据，不做任务判断，新增一条数据(新的逻辑)
            simpleAssetChangeHandle(asset,data);
         }
    }



    /**
     * 存在变更数据，与历史数据进行比较，根据逻辑进行修改、删除、新增
     *
     * @param asset
     * @param data
     */
    private void complexAssetChangeHandle(AssetQueryVO asset, AssetOnLineVO data) {
        if(null != asset ){  // 在资产表中存在
            String typeGuidAsset = asset.getTypeGuid()== null?"": asset.getTypeGuid();
            String typeGuid = data.getTypeGuid()== null?"": data.getTypeGuid();
            if (typeGuid.equals(typeGuidAsset)){ // 在资产表中，资产类型没变化
                assetChangeAndAssetNoChange(data);
            }else{ // 在资产表中，资产类型变化了
                assetChangeAndAssetIsChange(data,asset);
            }
        }else{  // 资产表中不存在
            assetChangeAndAssetNoChange(data);
        }
    }

    /**
     * 存在变更数据，不做任务判断，新增一条变更数据
     *
     * @param asset
     * @param data
     */
    private void simpleAssetChangeHandle(AssetQueryVO asset, AssetOnLineVO data) {
        if(null == asset){
            return;
        }
        // 在资产表中存在
        String typeGuidAsset = asset.getTypeGuid()== null?"": asset.getTypeGuid();
        String typeGuid = data.getTypeGuid()== null?"": data.getTypeGuid();
        if (!typeGuid.equals(typeGuidAsset)){ // 在资产表中，资产类型变化新增一条数据
            newAssetChanges.add(getNewAssetChange(data,asset));
        }
    }


    private AssetOnLine getNewAssetOnline(AssetOnLineVO data) {
        data.setGuid(UUIDUtils.get32UUID());
        data.setCreateTime(new Date());
        data.setIsImport(AssetOnlineConstant.NOIMPORTASSET);
        data.setIsDelete(AssetOnlineConstant.NODELETE);
        return  mapper.map(data,AssetOnLine.class);
    }

    /**
     * ip在在线表更新数据
     * @param data
     * @param assetOnlineOld
     * @return
     */
    private AssetOnLine getUpdateAssetOnline(AssetOnLineVO data, AssetOnLine assetOnlineOld) {
        data.setGuid(assetOnlineOld.getGuid());
        data.setCreateTime(assetOnlineOld.getCreateTime()); // 更新时保留以前录入时间
        data.setIsImport(assetOnlineOld.getIsImport());  // 修改时保留历史导入状态
        data.setIsDelete(AssetOnlineConstant.NODELETE);  // 更新时数据改为非删除状态
        return  mapper.map(data,AssetOnLine.class);
    }


    /**
     * ip不在资产表中存在或ip在资产表中资产类型相同处理
     *
     * 1.ip地址在变更表存在、没有处理过，删除ip对应的变更数据
     * 2.ip地址在变更表存在、已经处理过，不处理
     * 3.ip地址不在变更表中:不处理
     *
     * @param data
     */
    private void assetChangeAndAssetNoChange(AssetOnLineVO data) {
        List<AssetChange> assetChanges = getAsetChangeByIp(data.getIp());
        // 在资产变更表不存在：不处理
        if(CollectionUtils.isEmpty(assetChanges)){
            return;
        }
        for(AssetChange assetChange : assetChanges){
            // 在资产变更表存在已经处理了，不处理
            if(AssetChangeConstant.FINISH.equals(assetChange.getHandleStatus())){
                continue;
            }
            //  在资产变更表存在没有处理：删除变更表记录
            if(!assetChangeDelGuids.contains(assetChange.getGuid())){
                assetChangeDelGuids.add(assetChange.getGuid());
            }
        }
    }


    /**
     * 一个ip在变更表中会存在多条记录
     * @param ip
     * @return
     */
    private List<AssetChange> getAsetChangeByIp(String ip) {
        if(CollectionUtils.isEmpty(assetChanges)){
            return null;
        }
        List<AssetChange> filterdatas =assetChanges.stream().filter(item -> ip.equals(item.getIp())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(filterdatas)){
            return null;
        }
        return filterdatas;
    }

    /**
     * ip在资产表中资产类型不相同处理
     * 1. 在资产变更表不存在：新增处理
     * 2. 在资产变更表存在已经处理了，新增一条记录
     * 3. 在资产变更表存在没有处理：删除变更表记录，新增一条记录
     * @param data
     * @param asset
     */
    private void assetChangeAndAssetIsChange(AssetOnLineVO data, AssetQueryVO asset) {
        List<AssetChange> assetChanges = getAsetChangeByIp(data.getIp());
        // 在资产变更表不存在：新增处理
        if(CollectionUtils.isEmpty(assetChanges)){
            newAssetChanges.add(getNewAssetChange(data,asset));
            return;
        }
        int totalSize = assetChanges.size();
        List<AssetChange> finishDatas = getFinishHandle(assetChanges);
        int finishDatasSize = finishDatas.size();
        // 在资产变更表存在都已经处理了，如果当前资产类型在处理过的数据中不存在，新增一条记录
        if(totalSize == finishDatasSize){
            String scanTypeName = data.getTypeName();
            String assetTypeName= asset.getTypeName();
            boolean result = isExistTypeName(finishDatas,scanTypeName,assetTypeName);
            if(!result){
                newAssetChanges.add(getNewAssetChange(data,asset));
            }
            return;
        }
        // 在资产变更表存在没有处理的，删除未处理的，新增一条记录
        if(finishDatas.size() > 0){
            assetChanges.removeAll(finishDatas);
        }
        for(AssetChange assetChange : assetChanges){
            if(!assetChangeDelGuids.contains(assetChange.getGuid())){
                assetChangeDelGuids.add(assetChange.getGuid());
            }
        }
        newAssetChanges.add(getNewAssetChange(data,asset));
        return;
    }

    private boolean isExistTypeName(List<AssetChange> finishDatas, String scanTypeName, String assetTypeName) {
        for(AssetChange data : finishDatas){
            if(scanTypeName.equals(data.getScanTypeName())&&assetTypeName.equals(data.getAssetTypeName())){
              return true;
            }
        }
        return false;
    }

    /**
     * 获取已经处理的数据
     * @param assetChanges
     * @return
     */
    private List<AssetChange>  getFinishHandle(List<AssetChange> assetChanges) {
        List<AssetChange> changes  = new ArrayList<>();
        for(AssetChange data : assetChanges){
            if(AssetChangeConstant.FINISH.equals(data.getHandleStatus())){
                changes.add(data);
            }
        }
        return changes;
    }

    private AssetChange getNewAssetChange(AssetOnLineVO data, AssetQueryVO asset) {
        AssetChange assetChange = new AssetChange();
        assetChange.setGuid(UUIDUtils.get32UUID());
        assetChange.setIp(data.getIp());
        assetChange.setScanTypeName(data.getTypeName());
        assetChange.setAssetTypeName(asset.getTypeName());
        assetChange.setStatus(data.getStatus());
        assetChange.setCreateTime(new Date());
        assetChange.setHandleStatus(AssetChangeConstant.NOFINISH);
        return assetChange;
    }

    private AssetQueryVO getAssetByIp(String ip) {
        if(CollectionUtils.isEmpty(allAssets)){
            return null;
        }
        List<AssetQueryVO> assets = allAssets.stream().filter(item -> ip.equals(item.getIp())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(assets)){
            return assets.get(0);
        }
        return null;
    }

    /**
     * ip不在资产在线表存在的场景
     * 1.新增资产在线记录数据
     * 2.存在资产表中，并且资产类型不一样，新增变更记录表
     * @param data
     */
    private void newsaveAssetOnLine(AssetOnLineVO data) {
        AssetOnLine assetOnLine =  getNewAssetOnline(data);
        saveAssetOnLines.add(assetOnLine);
        AssetQueryVO asset  = getAssetByIp(data.getIp());
        if(null != asset ) {  // 在资产表中存在
            String typeGuidAsset = asset.getTypeGuid() == null ? "" : asset.getTypeGuid();
            String typeGuid = data.getTypeGuid() == null ? "" : data.getTypeGuid();
            if (!typeGuid.equals(typeGuidAsset)) { // 在资产表中，资产类型不同
                newAssetChanges.add(getNewAssetChange(data,asset));
            }
        }
    }


    private AssetOnLine getAssetOnlineByIp(String ip) {
        if(CollectionUtils.isEmpty(assetOnLines)){
            return null;
        }
        List<AssetOnLine> datas = assetOnLines.stream().filter(item -> ip.equals(item.getIp())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(datas)){
           return datas.get(0);
        }
        return  null;
    }


    /**
     * 数据保存
     * 1. 保存资产在线表
     * 2. 删除资产变更表数据
     * 3. 保存资产变更数据
     */
    private void saveDatas() {
        if(CollectionUtils.isEmpty(saveAssetOnLines)){
            return;
        }
        assetOnLineService.batchSaveDatas(saveAssetOnLines);
        // 删除变更数据
        if(CollectionUtils.isNotEmpty(assetChangeDelGuids)){
            batchDeleteAssetChanges(assetChangeDelGuids);
        }
        // 新增资产变更表数据
        List<AssetChange> assetChanges = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(newAssetChanges)){
            assetChanges.addAll(newAssetChanges);
        }
        if(CollectionUtils.isNotEmpty(updateAssetChanges)){
            assetChanges.addAll(updateAssetChanges);
        }
        if(CollectionUtils.isNotEmpty(assetChanges)){
            // 保存数据
            assetChangeService.batchSave(assetChanges);
        }
    }

    /**
     * 数据清理
     */
    private void datasClear() {
        assetChangeDelGuids = new ArrayList<>();
        newAssetChanges = new ArrayList<>();
        updateAssetChanges = new ArrayList<>();
        saveAssetOnLines = new ArrayList<>();
    }


    private void batchDeleteAssetChanges(List<String> assetChangeDelGuids) {
        String sql = "delete from asset_change where guid in  ('" + StringUtils.join(assetChangeDelGuids, "','") + "')";
        jdbcTemplate.execute(sql);
    }

}
