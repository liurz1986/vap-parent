package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/27 10:11
 * @description:
 */
@Data
public class EventLevelTotalRes {
    @ApiModelProperty("紧急事件总数")
    private long urgentCount;

    @ApiModelProperty("严重事件总数")
    private long seriousCount;

    @ApiModelProperty("重要事件总数")
    private long majorCount;

    @ApiModelProperty("一般事件总数")
    private long commonlyCount;

    @ApiModelProperty("今日事件总数")
    private long todayCount;

    @ApiModelProperty("已处置总数")
    private long disposedCount;

    @ApiModelProperty("未处置总数")
    private long notDisposedCount;
}
