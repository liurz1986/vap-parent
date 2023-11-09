package com.vrv.vap.alarmdeal.business.asset.analysis.vo;

import lombok.Data;

import java.util.List;

/**
 * 资产数量变化趋势
 */
@Data
public class QueryAssetCountChangeTrendVO {
    private List<String> dataX;    // x轴

    private List<QueryAssetCountChangeTrendExtendVO> dataY; // y轴数据

 }
