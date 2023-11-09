package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警执行对象")
public class AlarmCommandVO {
	   @ApiModelProperty(value="告警处置Id")
       private String alarmDealId; //告警处置Id
	   @ApiModelProperty(value="告警处置方式Id")
	   private String alarmItemId; //告警ItemId
	   @ApiModelProperty(value="当前登陆用户Id")
	   private String userId; //当前登陆用户Id
	   @ApiModelProperty(value="当前登陆用户名称")
	   private String userName; //当前登陆用户名称
}
