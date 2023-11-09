package com.vrv.vap.alarmdeal.business.threat.bean.fegin;

import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/8 16:15
 * @description:
 */
@Data
public class ThreatVulRes {
    // 威胁个数
    private double threatCount;

    // 漏洞个数
    private double vulCount;

    // 设备数
    private double ipCount;
}
