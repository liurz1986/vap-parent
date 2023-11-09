package com.vrv.vap.admin.vo.supervise.putdata;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.admin.config.PutField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EventBaseAttribute {


//	事件ID
	@PutField("event_id")
	@ApiModelProperty(value = "事件ID")
	String eventId;

//	事件名称
	@PutField("event_name")
	@ApiModelProperty(value = "事件名称")
	String eventName;

//	事件种类
	@PutField("event_kind")
	@ApiModelProperty(value = "事件种类")
	Integer eventKind;

//	事件类型
	@PutField("event_type")
	@ApiModelProperty(value = "事件类型")
	Integer eventType;

//	事件时间
	@PutField("event_creattime")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "事件时间")
	Date eventCreattime;

//	事件版本号
	@PutField("event_version")
	@ApiModelProperty(value = "事件版本号")
	String eventVersion;

	// 单位信息
	@PutField("unitInfo")
	@ApiModelProperty(value = "单位信息")
	UnitInfo unitInfo;

	// 人员数量
	@PutField("staff_num")
	@ApiModelProperty(value = "人员数量")
	Integer staffNum;

	// 人员信息
	@PutField("staffInfos")
	@ApiModelProperty(value = "人员信息")
	List<StaffInfo> staffInfos;

	// 设备数量
	@PutField("device_count")
	@ApiModelProperty(value = "设备数量")
	Integer deviceCount;

	// 设备信息
	@PutField("deviceInfos")
	@ApiModelProperty(value = "设备信息")
	List<DeviceInfo> deviceInfos;

	// 应用数量
	@PutField("device_app_count")
	@ApiModelProperty(value = "应用数量")
	Integer deviceAppCount;

	@PutField("applicationInfos")
	@ApiModelProperty(value = "应用信息")
	List<ApplicationInfo> applicationInfos;

	// 文件数量
	@PutField("file_count")
	@ApiModelProperty(value = "文件数量")
	Integer fileCount;

	@PutField("fileInfos")
	@ApiModelProperty(value = "文件信息")
	List<FileInfo> fileInfos;

	// 触发条件
	@PutField("event_triggers")
	@ApiModelProperty(value = "触发条件")
	String eventTriggers;

	// 事件详情
	@PutField("event_details")
	@ApiModelProperty(value = "事件详情")
	String eventDetails;

	// 三合一版本号
	@PutField("3in1_version")
	@ApiModelProperty(value = "三合一版本号")
	String ThreeinOneVersion;

	// 三合一生产厂家编号
	@PutField("3in1_factory_num")
	@ApiModelProperty(value = "三合一生产厂家编号")
	String ThreeinOneNum;


}
