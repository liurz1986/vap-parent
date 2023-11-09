package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmEventAttributeVO extends AlarmEventAttribute {

	/**
	 * 是否可以查看
	 */
	@ApiModelProperty(value = "是否可以查看")
	Boolean canRead;
	
	
	/**
	 * 是否可以审核
	 */
	@ApiModelProperty(value = "是否可以审核")
	Boolean canExamined;
	
 
	@ApiModelProperty(value = "事件类型名称")
	String eventTypeName;
	
	/**
	 * 是否可以处置
	 */
	@ApiModelProperty(value = "是否可以处置")
	Boolean canDeal;
	
	
	@ApiModelProperty(value = "危害")
	private String harm;	//危害

	@ApiModelProperty(value = "原理")
	private  String principle;	//原理

	@ApiModelProperty(value = "处置建议")
	private  String dealAdvice;	//处置建议

}
