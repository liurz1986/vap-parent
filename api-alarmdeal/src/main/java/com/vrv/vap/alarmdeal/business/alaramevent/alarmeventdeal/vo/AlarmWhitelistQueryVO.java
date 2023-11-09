package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="告警白名单入参对象")
public class AlarmWhitelistQueryVO {

    @ApiModelProperty(value="告警事件类型id")
    private String eventCategoryId;
    @ApiModelProperty(value="告警事件类型名称")
    private String title;
    @ApiModelProperty(value="源ip")
    private String srcIp;
    @ApiModelProperty(value="目的ip")
    private String destIp;

    @ApiModelProperty(value="起始行")
    private Integer start_;//
    @ApiModelProperty(value="每页个数")
    private Integer count_;

}
