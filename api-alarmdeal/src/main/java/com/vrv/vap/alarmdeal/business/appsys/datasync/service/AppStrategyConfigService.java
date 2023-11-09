package com.vrv.vap.alarmdeal.business.appsys.datasync.service;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.jpa.web.Result;

import java.util.List;


public interface AppStrategyConfigService {
    /**
     * 数据补全策略
     * @param appVerify
     * @return
     */
    public  Result<String> supplementData(AppSysManagerVerify appVerify, List<TbConf> tbConfs);

    /**
     * 更新策略配置
     * @param tbConfS
     */
    public void updateStrategyConfig(List<TbConf> tbConfS);

    /**
     * 获取资产策略配置
     */
    public List<TbConf> getStrategyConfigs();
}
