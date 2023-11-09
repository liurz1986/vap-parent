package com.vrv.vap.alarmdeal.business.asset.datasync.service;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.jpa.web.Result;

import java.util.List;


public interface AssetStrategyConfigService {
    /**
     * 数据补全策略-新增
     * @param assetVerify
     * @return
     */
    public  Result<String> supplementData(AssetVerify assetVerify, List<TbConf> tbConfs);

    /**
     * 数据补全策略-修改
     * @param assetVerify
     * @return
     */
    public  Result<String> supplementDataUpdate(AssetVerify assetVerify, List<TbConf> tbConfs, AssetVerify assetVerifyOld,String dataRepairOrder);

    /**
     * 数据来源优选级
     * @param currentDataSourceType 当前的数据来源
     * @param oldDataSourceType 历史数据的数据来源
     * @return
     */
    public Result<String> dataSourcePriorityStrategy(int currentDataSourceType, int oldDataSourceType, List<TbConf> tbConfs);

    /**
     * 外部数据同步优选级
     * @param currentSyncSource 当前外部来源信息
     * @param oldtSyncSource  历史外部来源信息
     * @return
     */
    public Result<String> outSourcePriorityStrategy(String currentSyncSource, String oldtSyncSource,List<TbConf> tbConfs);


    /**
     *  补全优先级:现有库信息补全、统一补全
     * 配置为：现有库信息补全,统一补全。先按现有库信息补全进行处理，为空的再按照统一补全进行补全
     * 配置为：统一补全,现有库信息补全。先按统一补全进行处理，为空的再按现有库信息补全进行补全
     * @param tbConfs
     * @return
     */
    public String syncAssetDataRepairOrder(List<TbConf> tbConfs);
    /**
     * 更新策略配置
     * @param tbConfS
     */
    public void updateStrategyConfig(List<TbConf> tbConfS);

    /**
     * 获取资产策略配置
     */
    public List<TbConf> getAssetStrategyConfigs();
}
