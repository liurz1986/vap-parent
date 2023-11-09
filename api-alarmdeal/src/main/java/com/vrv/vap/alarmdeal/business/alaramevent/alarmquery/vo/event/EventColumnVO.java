package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "事件列名")
public class EventColumnVO {
	@ApiModelProperty(value = "事件列ID")
	private String id;
	@ApiModelProperty(value = "列名")
	private String name;
	@ApiModelProperty(value = "标签")
	private String label;
	@ApiModelProperty(value = "类型")
	private String type;
	@ApiModelProperty(value = "长度")
	private String len;
	@ApiModelProperty(value = "主键key")
	private Boolean primaryKey;
	@ApiModelProperty(value = "是否为空")
	private Boolean notNull;
	@ApiModelProperty(value = "事件表主键ID")
	private String eventTableId; // 关联eventTable
	@ApiModelProperty(value = "是否是源IP")
	private Boolean srcIp; // 源IP
	@ApiModelProperty(value = "是否是目的IP")
	private Boolean dstIp; // 源IP
	@ApiModelProperty(value = "是否是关联IP")
	private Boolean relateIp; // 关联IP
	@ApiModelProperty(value = "是否是时间字段")
	private Boolean timeLine; // 是否是时间字段
	@ApiModelProperty(value = "字表id")
	private  String dataHint;
	@ApiModelProperty(value = "字表id")
	private  String order;
	@ApiModelProperty(value = "是否展示")
	private Boolean isShow;
	@ApiModelProperty(value = "是否是事件时间")
	private boolean eventTime;



}
