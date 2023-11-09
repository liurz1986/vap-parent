package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.AssetCountModel;
import com.vrv.vap.xc.model.ReportParam;

public interface BaseInfoIndicatorsService {
    /**
     * 2.1.资产变更审批情况
     * 统计新增资产类型和数量
     *
     * @param model
     * @return
     */
    VList<AssetCountModel> statisticsAssetTypeNum(ReportParam model);

    /**
     * 2.1.资产变更审批情况
     * 按类型统计SM网资产总数、新增数量
     *
     * @param model
     * @return
     */
    VList<AssetCountModel> statisticsAssetTypeTotal(ReportParam model);
}
