package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import lombok.Data;

/**
 * 告警关联查询结果VO
 *
 * 2023-3-23
 */
@Data
public class AlarmUniteResultVO {
    private String alarmGuid; // 告警id

    private String key ; // 用户名称,对应日志汇总username 三员

    private String remarks; // 填写情况说 对应告警中告警描述(event_details)

    private String content; // 审计内容，对应成因分析

    private String info; // 审计情况及分析，对应技术整改措施
}
