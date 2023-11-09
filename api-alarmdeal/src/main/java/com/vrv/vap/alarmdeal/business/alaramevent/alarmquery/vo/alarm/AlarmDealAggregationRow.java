package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmDealAggregationRow {
	
	
	   @ApiModelProperty(value="告警处置事件类型")
       private Integer eventType;  
	   
	   
	   @ApiModelProperty(value="告警处置事件名称")
       private String eventName; 
	   
	   
	   @ApiModelProperty(value="合并数量")
       private Integer mergeCount; //告警处置Id
	   
	   @ApiModelProperty(value="告警事件最大风险等级")
       private Integer alarmRiskLevel;  
	   
	   
	   @ApiModelProperty(value="告警事件处理状态")
       private Integer alarmDealState;  
	   
	   @ApiModelProperty(value="告警事件时间")
	   @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
       private Date eventCreatetime;  
	   
	   @ApiModelProperty(value="标签集合")
       private String labels; 
	   
	   @ApiModelProperty(value="dstIP")
       private String dstIp; 
	   
	   @ApiModelProperty(value="dstIP")
       private String srcIp; 
}
