package com.vrv.vap.alarmdeal.business.threat.bean.fegin;

import lombok.Data;

/**
 * @author: Administrator
 * @since: 2022/8/29 14:44
 * @description:
 */
@Data
public class ThreatGBVo {
    // ip
    private String ip;

    // 威胁分类id
    private String categoryId;

    // 威胁时间
    private String time;

    // 资产类型
    private String assetType;

    // 危险程度
    private Integer weight;

    // 威胁名称
    private String ThreatName;
}
