package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 联通设备
 */
@Data
public class ConnectDevice {
    /**
     * 设备ip
     */
    @SerializedName(value = "device_ip",alternate = "deviceIp")
    private String device_ip;
    /**
     * 设备类型
     */
    @SerializedName(value = "device_type",alternate = "deviceType")
    private String device_type;

}
