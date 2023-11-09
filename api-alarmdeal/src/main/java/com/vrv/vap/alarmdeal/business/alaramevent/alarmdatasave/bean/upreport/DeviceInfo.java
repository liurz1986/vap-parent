package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 设备有关情况
 */
@Data
public class DeviceInfo {
    /**
     * 涉事设备ip
     */
    @SerializedName(value = "device_ip",alternate = {"deviceIp"})
    private String device_ip;
    /**
     * 设备类型 数值  1 涉事专用机  2 涉密专用服务器 3 windows终端 4 通用服务器 5 其他
     */
    @SerializedName(value = "device_type",alternate = {"deviceType"})
    private String device_type;
    /**
     * 设备密级
     */
    @SerializedName(value = "device_level", alternate = {"deviceLevel"})
    private String device_level;
    /**
     * 设备责任人 personLiable，但是是一个对象，需要解析对象获取责任人名称
     */
    private String device_liable;
    /**
     * 设备所属部门
     */
    @SerializedName(value = "device_dept", alternate = {"orgName"})
    private String device_dept;

}
