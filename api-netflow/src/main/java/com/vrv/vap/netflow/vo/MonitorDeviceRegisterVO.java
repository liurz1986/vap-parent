package com.vrv.vap.netflow.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wh1107066
 * @date 2023/9/7
 */

@Data
@NoArgsConstructor
public class MonitorDeviceRegisterVO {
    /**
     * 设备id
     */
    @JSONField(name="device_id")
    @JsonProperty("device_id")
    private String deviceId;
    /**
     * 设备软件版本号
     */
    @JSONField(name="device_soft_version")
    @JsonProperty("device_soft_version")
    private String deviceSoftVersion;

    /**
     * 设备配置信息
     */
    @JSONField(name="interface")
    @JsonProperty("interface")
    private JSONArray interfaces;
    /**
     * 内存总数
     */
    @JSONField(name="mem_total")
    @JsonProperty("mem_total")
    private String memTotal;
    /**
     * cpu信息
     */
    @JSONField(name="cpu_info")
    @JsonProperty("cpu_info")
    private JSONArray cpuInfo;
    /**
     * 磁盘信息
     */
    @JSONField(name="disk_info")
    @JsonProperty("disk_info")
    private JSONArray diskInfo;
    /**
     * 设备所属单位
     */
    @JSONField(name="device_belong")
    @JsonProperty("device_belong")
    private String deviceBelong;
    /**
     * 设备部署位置
     */
    @JSONField(name="device_location")
    @JsonProperty("device_location")
    private String deviceLocation;
    /**
     * 行政区域编码
     */
    @JSONField(name="address_code")
    @JsonProperty("address_code")
    private String addressCode;
    /**
     * 客户单位联系人信息
     */
    private JSONArray contact;
    /**
     * 备注信息
     */
    private String memo;
}
