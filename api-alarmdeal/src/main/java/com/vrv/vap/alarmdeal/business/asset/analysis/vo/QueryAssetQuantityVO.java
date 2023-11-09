package com.vrv.vap.alarmdeal.business.asset.analysis.vo;

import lombok.Data;

/**
 * 资产分析中资产相关统计
 *
 *发现资产、在线资产、资产在线比例、台账资产、未处理告警数
 *
 */
@Data
public class QueryAssetQuantityVO {
    private String assetDiscoveryTotal; //发现资产总数：资产在线表所有资产总数

    private String assetOnLineTotal; //在线资产：在线资产中状态为在线的总数

    private String assetTotal; // 台账资产

    private String assetOnLinePercent; // 资产在线比例

    private String warmNum; // 未处理告警数据

}
