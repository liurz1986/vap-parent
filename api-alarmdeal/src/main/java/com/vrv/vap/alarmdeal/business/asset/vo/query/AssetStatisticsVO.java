package com.vrv.vap.alarmdeal.business.asset.vo.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 资产统计对象
 */
@Data
public class AssetStatisticsVO {

    private String name; // 名称

    private int count; // 数量

    // 初始化涉密等级数据
    public static List<AssetStatisticsVO > getInitLevels(){
        String[] levels = {"绝密","机密","秘密","内部","非密"};
        List<AssetStatisticsVO> datas = new ArrayList<>();
        for(int i =0 ;i < levels.length;i++){
            AssetStatisticsVO vo = new AssetStatisticsVO();
            vo.setName(levels[i]);
            vo.setCount(0);
            datas.add(vo);
        }
        return datas;
    }
}
