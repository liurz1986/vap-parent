package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 监管事件data数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRegular extends AbstractUpData {
	/**
	 * 事件id
	 */
	@SerializedName(value = "event_id",alternate = {"eventId"})
	private String event_id;
	/**
	 * 事件总类
	 */
	@SerializedName(value = "event_kind",alternate = {"eventKind"})
	private Integer event_kind;
	/**
	 * 事件时间
	 */
	@SerializedName(value = "event_createtime",alternate = {"eventCreattime"})
	private String event_createtime;
	/**
	 * 事件版本号
	 */
	@SerializedName(value = "event_version",alternate = {"eventVersion"})
	private String event_version;
	/**
	 * 上报单位集合
	 */
	private List<UnitInfo> unit_list=new ArrayList<>();
	/**
	 * 单位数量
	 */
	private Integer unit_num;
	/**
	 * 责任人数量
	 */
	@SerializedName(value = "staff_num",alternate = {"staffNum"})
	private Integer staff_num;
	/**
	 * 责任人列表
	 */
	@SerializedName(value = "staff_list",alternate = {"staffInfos"})
	private List<StaffInfoSupervise> staff_list=new ArrayList<>();
	/**
	 * 设备数量
	 */
	@SerializedName(value = "device_count",alternate = {"deviceCount"})
	private Integer device_count;
	/**
	 * 设备列表
	 */
	@SerializedName(value = "device_list",alternate = {"deviceInfos"})
	private List<DeviceInoSupervise> device_list=new ArrayList<>();
	/**
	 * 应用数量
	 */
	@SerializedName(value = "device_app_count",alternate = {"deviceAppCount"})
	private Integer device_app_count;
	/**
	 * 应用列表applicationInfos
	 */
	@SerializedName(value = "application_list",alternate = {"applicationInfos"})
	private List<ApplicationSupervise> application_list=new ArrayList<>();
	/**
	 * 文件数量
	 */
	@SerializedName(value = "file_count",alternate = {"fileCount"})
	private Integer file_count;
	/**
	 * 文件列表
	 */
	@SerializedName(value = "file_list",alternate = {"fileInfos"})
	private List<FileInfoSupervise> file_list=new ArrayList<>();
	/**
	 * 触发条件
	 */
	@SerializedName(value = "event_triggers",alternate = {"eventTriggers"})
	private String event_triggers;
	/**
	 * 事件详情
	 */
	@SerializedName(value = "event_details",alternate = {"eventDetails"})
	private String event_details;
	/**
	 * 不确认的字段 todo 这里需要进行补全，补充三合一的有关信息
	 */
	private List<ThreeinOneInfo> extention = new ArrayList<ThreeinOneInfo>();

}
