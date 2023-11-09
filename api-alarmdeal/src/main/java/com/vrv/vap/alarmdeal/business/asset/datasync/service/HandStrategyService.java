package com.vrv.vap.alarmdeal.business.asset.datasync.service;

import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.jpa.web.Result;

import java.util.List;
import java.util.Map;

public interface HandStrategyService {
    public void updateAsset(List<TbConf> tbConfS);

    public List<TbConf>  queyConfigAssets();

    public Result<String> queryImportType();

    public Result<Map<String,Object>>  queyConfigAssets(String key);
}
