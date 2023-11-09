package com.vrv.vap.alarmdeal.business.threat.bean.response;

import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/13 14:31
 * @description:
 */
@Data
public class AssetRiskRes {
    // 威胁值
    private double threatTotal;

    // 资产权重值
    private double assetWorth;

    // 漏洞值
    private double vulTotal;

    // 威胁值
    private long maxThreatTotal;

    // 资产权重值
    private long maxAssetWorth;

    // 漏洞值
    private long maxVulTotal;
}
