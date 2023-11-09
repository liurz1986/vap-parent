package com.vrv.vap.alarmdeal.business.asset.vo.query;

import lombok.Data;

/**
 * 资产总数统计对象
 */
@Data
public class AssetTotalStatisticsVO extends AssetLevelStatisticsVO{
    private int assetTotal; // 总设备数量
    private int termTypeCN ; // 国产数量
    private int termTypeEN ;// 非国产数量

}
