package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import lombok.Data;

/**
 * 日志信息
 * 2023-03-23
 */
@Data
public class AbnormalEventVo {
    //	策略名称
    String ruleName;
    //	事件等级
    String alarmRiskLevel;
    //	事件数量
    Integer eventCount;
    //人员数量
    Integer staffInfoCount;
}
