package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/27 10:09
 * @description:
 */
@Data
public class AlarmEventRankRes {
    @ApiModelProperty("告警事件")
    private String eventName;

    @ApiModelProperty("告警总数")
    private long alarmCount;
}
