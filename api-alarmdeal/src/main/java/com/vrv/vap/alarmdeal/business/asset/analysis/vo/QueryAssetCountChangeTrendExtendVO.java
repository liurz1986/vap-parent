package com.vrv.vap.alarmdeal.business.asset.analysis.vo;

import lombok.Data;

import java.util.List;

/**
 *  资产数量变化趋势的扩展属性
 */
@Data
public class QueryAssetCountChangeTrendExtendVO {
    private String typeName;  // 资产小类名称

    private List<Integer> datas;  // 资产数量汇总
}
