package com.vrv.vap.alarmdeal.business.asset.analysis.service;

public interface AssetAnalysisService {

    public void excStatistic() ;
    /**
     * 只统计台账中探针入库的资产总数  2023-1-4
     * @return
     */
    public Long getAssetTotal();
}
