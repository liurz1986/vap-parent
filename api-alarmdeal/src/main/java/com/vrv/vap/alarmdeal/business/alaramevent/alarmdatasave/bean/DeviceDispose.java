package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 设备信息处置
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDispose {
	/**
	 * 设备类型
	 */
	@SerializedName(value = "device_type",alternate = {"deviceType"})
	private String device_type;//设备类型
	/**
	 * 设备密级
	 */
	@SerializedName(value = "device_level",alternate = {"deviceLevel"})
	private String device_level;//设备密级
	/**
	 * ???
	 */
	private String device_connect_network;//联网情况
	/**
	 * ?
	 */
	private String device_3In1;//三合一情况
}
