package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm;

import com.vrv.vap.jpa.web.page.PageReqVap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="告警查询条件存储VO")
public class AlarmQueryVO extends PageReqVap {

    @ApiModelProperty(value="主键guid")
    private String guid;  //主键guid
    @ApiModelProperty(value="条件名称")
    private String queryName;
    @ApiModelProperty(value="查询条件")
    private String queryCondition;  //查询条件
}
