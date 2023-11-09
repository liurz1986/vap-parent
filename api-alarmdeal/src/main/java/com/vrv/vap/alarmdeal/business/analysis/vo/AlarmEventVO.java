package com.vrv.vap.alarmdeal.business.analysis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月15日 下午2:22:51 
* 类说明    告警事件VO
*/
@ApiModel(value="告警业务入参对象")
@Data
public class AlarmEventVO {
	@ApiModelProperty(value="工单编号")
	private String ticketNum;
	@ApiModelProperty(value="工单名称")
    private String name;
	@ApiModelProperty(value="事件类型")
    private String eventType;
	@ApiModelProperty(value="告警ip")
    private String alarmIp;
	@ApiModelProperty(value="告警guid")
	private String alarmId;
}
