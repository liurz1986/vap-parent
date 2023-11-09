package com.vrv.vap.netflow.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wh1107066
 * @date 2023/9/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorDevicesUploadVO {
    @JSONField(name = "device_id")
    @JsonProperty("device_id")
    private String deviceIp;

    @JSONField(name = "data_type")
    @JsonProperty("data_type")
    private String dataType;

    @JSONField(name = "device_type")
    @JsonProperty("device_type")
    private String deviceType;

    @JSONField(name = "device_model")
    @JsonProperty("device_model")
    private String deviceModel;

    private String os;

    @JSONField(name = "os_version")
    @JsonProperty("os_version")
    private String osVersion;

    private String ip;

    private String mac;

    @JSONField(name = "updated_at")
    @JsonProperty("updated_at")
    private String updateAt;

    @JSONField(name = "extended_fields")
    @JsonProperty("extended_fields")
    public JSONObject extendedFields;

}
