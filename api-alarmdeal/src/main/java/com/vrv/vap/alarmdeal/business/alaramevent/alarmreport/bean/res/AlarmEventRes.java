package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/13 10:49
 * @description:
 */
@Data
public class AlarmEventRes {
    // 事件事件
    private String eventName;

    // 告警总数
    private long alarmCount;

    // 告警等级
    private String alarmRiskLevel;  // 1-2 低  3 中 4-5 高

    // 事件发生最新时间
    private String updateTime;
}
