package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppAlarmEventAttributeVO  {

    @ApiModelProperty(value = "事件ID")
    String eventId;
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "事件时间")
    Date eventCreattime;

    @ApiModelProperty(value = "应用名")
    String appName;

    @ApiModelProperty(value = "ip")
    String ip;

    @ApiModelProperty(value = "事件详情")
    String eventDetails;
    @ApiModelProperty(value = "事件类型")
    String eventTypeName;

    @ApiModelProperty(value = "事件编码")
    String eventCode;
    @ApiModelProperty(value = "事件编码")
    String staffName;
    @ApiModelProperty(value = "事件ID")
    String eventName;
}
