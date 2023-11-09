package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2023/4/3 10:51
 * @description:
 */
@Data
public class AlarmCountRes {

    @ApiModelProperty("告警总数")
    private Long alarmCount;

    @ApiModelProperty("工单总数")
    private Long flowCount;

    @ApiModelProperty("已处置状态告警数")
    private Long dealCount;
}
