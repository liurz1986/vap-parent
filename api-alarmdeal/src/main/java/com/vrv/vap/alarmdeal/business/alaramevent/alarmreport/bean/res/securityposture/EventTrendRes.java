package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/27 10:14
 * @description:
 */
@Data
public class EventTrendRes {
    @ApiModelProperty("事件时间")
    private String eventTime;

    @ApiModelProperty("事件总数")
    private long eventCount;
}
