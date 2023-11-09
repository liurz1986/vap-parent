package com.vrv.vap.alarmdeal.business.asset.analysis.vo;

import lombok.Data;


@Data
public class AssetAnalysisTypeStatisticVO {

    private String createTime;  // 统计时间

    private int num;   //数量

    private String name;   //二级资产类型名称
}
