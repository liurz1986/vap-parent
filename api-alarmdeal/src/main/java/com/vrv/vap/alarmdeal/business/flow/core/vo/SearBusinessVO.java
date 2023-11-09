package com.vrv.vap.alarmdeal.business.flow.core.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearBusinessVO {
    @ApiModelProperty(value="用户userId")
    private String userId;

    @ApiModelProperty(value="事件id") // 对应business_intance表中guid
    private String eventId;
}
