package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.ApplicationInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.DeviceInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.FileInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfo;
import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import com.vrv.vap.es.model.PrimaryKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@PrimaryKey(value="eventId")
public class EventBaseAttribute {

//	事件ID
	@EsField("event_id")
	@ApiModelProperty(value = "事件ID")
	String eventId;

//	事件名称
	@EsField("event_name")
	@ApiModelProperty(value = "事件名称")
	String eventName;

//	事件种类
	@EsField("event_kind")
	@ApiModelProperty(value = "事件种类")
	Integer eventKind;

//	事件类型
	@EsField("event_type")
	@ApiModelProperty(value = "事件类型")
	Integer eventType;

//	事件时间
	@EsField("event_creattime")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "事件时间")
	Date eventCreattime;

//	事件版本号
	@EsField("event_version")
	@ApiModelProperty(value = "事件版本号")
	String eventVersion;

	// 单位信息 todo 20230714:这里原来是对象，现在改为了数组，需要清楚影响
	@EsField("unit_list")
	@ApiModelProperty(value = "单位信息")
//	UnitInfo unitList;
	List<UnitInfo> unitList;


	// 人员数量
	@EsField("staff_num")
	@ApiModelProperty(value = "人员数量")
	Integer staffNum;

	// 人员信息
	@EsField("staff_list")
	@ApiModelProperty(value = "人员信息")
	List<StaffInfo> staffInfos;

	// 人员信息
	@EsField("relatedStaffInfos")
	@ApiModelProperty(value = "人员信息")
	List<StaffInfo> relatedStaffInfos;

	// 设备数量
	@EsField("device_count")
	@ApiModelProperty(value = "设备数量")
	Integer deviceCount;

	// 设备信息
	@EsField("device_list")
	@ApiModelProperty(value = "设备信息")
	List<DeviceInfo> deviceInfos;

	// 应用数量
	@EsField("device_app_count")
	@ApiModelProperty(value = "应用数量")
	Integer deviceAppCount;

	@EsField("application_list")
	@ApiModelProperty(value = "应用信息")
	List<ApplicationInfo> applicationInfos;

	// 文件数量
	@EsField("file_count")
	@ApiModelProperty(value = "文件数量")
	Integer fileCount;

	@EsField("file_list")
	@ApiModelProperty(value = "文件信息")
	List<FileInfo> fileInfos;

	// 触发条件
	@EsField("event_triggers")
	@ApiModelProperty(value = "触发条件")
	String eventTriggers;

	// 事件详情
	@EsField("event_details")
	@ApiModelProperty(value = "事件详情")
	String eventDetails;


	@EsField("principle")
	@ApiModelProperty(value = "原理")
	String principle;

	// 三合一版本号
	@EsField("3in1_version")
	@ApiModelProperty(value = "三合一版本号")
	String threeinOneVersion;

	// 三合一生产厂家编号
	@EsField("3in1_factory_num")
	@ApiModelProperty(value = "三合一生产厂家编号")
	String threeinOneNum;

	// 三合一生产厂家编号
	@EsField("tag")
	@ApiModelProperty(value = "tag")
	String tag;




}



