package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetBookDetailService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetHandSyncService;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.stream.Collectors;

/**
 * 外部资产同步数据处理--手动处理
 *
 * 2023-04
 */
@Service
@Transactional
public class AssetHandSyncServiceImpl implements AssetHandSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetHandSyncServiceImpl.class);
    @Autowired
    private AssetBookDetailService assetBookDetailService;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private AssetBaseDataService assetBaseDataCacheService;
    /**
     *  批量处理kafka资产数据入口
     *  1.获取数据中数据源及批次
     *  2.数据过滤处理，获取当前数据源及批次数据：主要目的是为了多批次数据的处理
     *  3.清除历史数据(非当前数据源及批次数据)
     *  4.保存同步数据
     *
     * @param assetSyncVOs
     */
    @Override
    public void excAssetDataSync(List<AssetSyncVO> assetSyncVOs) {
        try{
            LOGGER.warn("同步外部资产数据开始,手动入库方式开始处理的数量："+assetSyncVOs.size());
            // 获取数据中数据源及批次
            Map<String,String> curBatchNo = getCurrentBatchNo(assetSyncVOs);
            // 数据过滤处理，获取当前数据源及批次数据：主要目的是为了多批次数据的处理
            List<AssetSyncVO> handleDatas = getCurData(curBatchNo,assetSyncVOs);
            // 清除历史数据:当前数据源非当前批次数据清除
            assetBookDetailService.deleteByBatchNo(curBatchNo);
            // 根据导入数据组装新的数据
            List<AssetBookDetail> newDatas = getAssetBookDetails(handleDatas);
            // 保存数据
            assetBookDetailService.save(newDatas);
            LOGGER.warn("=========同步外部资产数据结束,手动入库方式=======");
        }catch (Exception e){
            LOGGER.error("同步外部资产数据,手动入库方式,出现异常",e);
        }
        assetSyncVOs.clear();
    }

    private List<AssetSyncVO> getCurData(Map<String, String> curBatchNo, List<AssetSyncVO> assetSyncVOs) {
        List<AssetSyncVO> datas = new ArrayList<>();
        for(AssetSyncVO data : assetSyncVOs){
           String syncSource =  data.getSyncSource();
           String batchNo =  data.getBatchNo();
           String curBatchNoMap = curBatchNo.get(syncSource);
           if(curBatchNoMap.equals(batchNo)){
               datas.add(data);
           }
        }
        return datas;
    }

    /**
     * 获取数据中数据源及批次
     *
     * @param assetSyncVOs
     * @return
     */
    private Map<String,String> getCurrentBatchNo(List<AssetSyncVO> assetSyncVOs) {
        Map<String,String> batchResouce = new HashMap<>();
        for(AssetSyncVO asset : assetSyncVOs){
            String batchNo= asset.getBatchNo();
            String syncSource = asset.getSyncSource();
            if(StringUtils.isNotEmpty(batchNo)&&StringUtils.isNotEmpty(syncSource)){
                batchResouce.put(syncSource,batchNo);
            }
        }
        if(batchResouce.size() == 0){
            LOGGER.error("当前没有当前批次及数据源");
            throw new RuntimeException("当前没有当前批次及数据源不执行数据同步");
        }
        LOGGER.info("当前批次及数据源:"+ JSON.toJSONString(batchResouce));
        return batchResouce;
    }





    /**
     * 组装数据
     * @param assetSyncVOs
     * @return
     */
    private List<AssetBookDetail> getAssetBookDetails(List<AssetSyncVO> assetSyncVOs) {
        List<AssetBookDetail> list = new ArrayList<>();
        for(AssetSyncVO data : assetSyncVOs){
            AssetBookDetail assetBookDetail  = getNewData(data);
            list.add(assetBookDetail);
        }
        return list;
    }

    /**
     * 组装数据：
     * @param data
     * @return
     */
    private AssetBookDetail getNewData(AssetSyncVO data) {
        AssetBookDetail assetBookDetail = mapperUtil.map(data,AssetBookDetail.class);
        String ip = data.getIp()==null?"":data.getIp();
        String serialNumer = data.getSerialNumber()==null?"":data.getSerialNumber();
        String sysnSource = data.getSyncSource()==null?"":data.getSyncSource();
        assetBookDetail.setSyncSource(sysnSource);
        assetBookDetail.setSerialNumber(serialNumer);
        assetBookDetail.setIp(ip);
        assetBookDetail.setCreateTime(new Date());
        assetBookDetail.setGuid(UUIDUtils.get32UUID());
        return assetBookDetail;
    }

}
