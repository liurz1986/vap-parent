package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 设备信息监管
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInoSupervise {
    /**
     * 设备类型
     */
//	@SerializedName(value = "device_type",alternate = {"deviceType"})
//	private String device_type;
    /**
     * 设备名称
     */
    @SerializedName(value = "device_name", alternate = {"deviceName"})
    private String device_name;
    /**
     * 设备密级
     */
    @SerializedName(value = "device_level", alternate = {"deviceLevel"})
    private String device_level;
    /**
     * ip
     */
    @SerializedName(value = "device_ip", alternate = {"deviceIp"})
    private String device_ip;
    /**
     * 软件系统版本号
     */
//	@SerializedName(value = "device_version",alternate = {"deviceVersion"})
//	private String device_version;
    /**
     * 硬件设备型号
     */
//	@SerializedName(value = "device_model",alternate = {"deviceModel"})
//	private String device_model;
    /**
     * 设备入网时间
     */
//	@SerializedName(value = "device_net_time",alternate = {"deviceNetTime"})
//	private String device_net_time;
    /**
     * MAC
     */
    @SerializedName(value = "device_mac", alternate = {"deviceMac"})
    private String device_mac;
    /**
     * 存储信息，todo 待完善，这个值如何来，需要后续进行完善
     */
    private String info_storage="0";
    /**
     * 硬盘序列号/设备ID
     */
//	@SerializedName(value = "device_disk_seq",alternate = {"deviceDiskSeq"})
//	private String device_disk_seq;
//	/**
//	 * 所属安全域
//	 */
//	@SerializedName(value = "device_security_domain",alternate = {"deviceSecurityDomain"})
//	private String device_security_domain;
}
