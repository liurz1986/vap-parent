package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 应用信息监管
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationSupervise {
	/**
	 * 应用系统名称
	 */
	@SerializedName(value = "application_label",alternate = {"applicationLabel"})
	private String application_label;
	/**
	 * 所在服务器IP
	 */
	@SerializedName(value = "application_ip",alternate = {"applicationIp"})
	private String application_ip;
	/**
	 * 通讯协议名
	 */
//	@SerializedName(value = "application_protocal",alternate = {"applicationProtocal"})
//	private String application_protocal;
	/**
	 * 通信参数
	 */
//	@SerializedName(value = "application_arg",alternate = {"applicationArg"})
//	private String application_arg;
	/**
	 * 服务端口
	 */
	@SerializedName(value = "application_port",alternate = {"applicationPort"})
	private String application_port;
}
