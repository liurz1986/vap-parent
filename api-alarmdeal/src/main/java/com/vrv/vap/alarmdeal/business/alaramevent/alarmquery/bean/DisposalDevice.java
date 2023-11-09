package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DisposalDevice{
	
//	设备类型	device_type
	@EsField("device_type")
	@ApiModelProperty(value = "设备类型")
	String deviceType;

//	设备密级	device_level
	@EsField("device_level")
	@ApiModelProperty(value = "设备密级")
	String deviceLevel;

//	联网情况	device_connect_network
	@EsField("device_connect_network")
	@ApiModelProperty(value = "联网情况")
	String deviceConnectNetwork;

//	三合一情况	device_3In1
	@EsField("device_3In1")
	@ApiModelProperty(value = "三合一情况")
	String device3In1;
}