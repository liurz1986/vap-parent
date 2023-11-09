package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import lombok.Data;

/**
 * 告警关联查询VO
 *
 * 2023-3-23
 */
@Data
public class AlarmUniteSearchVO {

    private String startTime;  // 开始时间

    private String endTime;  // 结束时间

    private String reportDevType; // 上报类型
}
