package com.vrv.vap.alarmdeal.business.asset.vo.query;

import lombok.Data;

/**
 * 资产按涉及等级统计
 */
@Data
public class AssetLevelStatisticsVO {

    private int level5; // 绝密资产数量
    private int level4; // 机密资产数量
    private int level3; // 秘密资产数量
    private int level2; // 内部资产数量
    private int level1; // 非密资产数量

    public static AssetLevelStatisticsVO getInit(){
        AssetLevelStatisticsVO vo = new AssetLevelStatisticsVO();
        vo.setLevel1(0);
        vo.setLevel2(0);
        vo.setLevel3(0);
        vo.setLevel4(0);
        vo.setLevel5(0);
        return vo;
    }
}
