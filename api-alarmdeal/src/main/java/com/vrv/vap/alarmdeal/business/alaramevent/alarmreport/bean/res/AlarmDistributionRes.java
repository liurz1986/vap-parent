package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/13 10:46
 * @description:
 */
@Data
public class AlarmDistributionRes {
    // 设备IP
    private String ip;

    // 告警总数
    private long alarmCount;

    // 高危数 // 1-2 低  3 中 4-5 高
    private long highCount;

    // 中危数  // 1-2 低  3 中 4-5 高
    private long mediumCount;

    // 低危数  // 1-2 低  3 中 4-5 高
    private long lowCount;
}
