package com.vrv.vap.alarmdeal.business.asset.analysis.vo;

import lombok.Data;

import java.util.List;

/**
 * 资产数量变化趋势
 */
@Data
public class QueryChangeTrendVO {

    private String typeName;  // 资产小类名称

    private List<QueryAssetLineTypeVO> datas;  // 资产数量汇总
}
