package com.vrv.vap.alarmdeal.business.threat.bean.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/7 14:28
 * @description:
 */
@Data
public class ThreatReq {
    @ApiModelProperty(value="开始时间")
    private String startTime;
    @ApiModelProperty(value="结束时间")
    private String endTime;
    @ApiModelProperty(value="部门编码")
    private String orgCode;
    @ApiModelProperty(value = "timeType")
    private String timeType;
}
