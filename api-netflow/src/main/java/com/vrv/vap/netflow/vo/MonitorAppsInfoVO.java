package com.vrv.vap.netflow.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wh1107066
 * @date 2023/9/8
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorAppsInfoVO implements Serializable {
    @JSONField(name = "device_id")
    @JsonProperty("device_id")
    private String deviceId;

    @JSONField(name = "data_type")
    @JsonProperty("data_type")
    private String dataType;

    @JSONField(name = "app_type")
    @JsonProperty("app_type")
    private String appType;

    @JSONField(name = "app_middleware")
    @JsonProperty("app_middleware")
    private String appMiddleware;

    @JSONField(name = "middleware_version")
    @JsonProperty("middleware_version")
    private String middlewareVersion;

    private String ip;
    private String host;

    @JSONField(name = "updated_at")
    @JsonProperty("updated_at")
    private String updateAt;

    @JSONField(name = "extended_fields")
    @JsonProperty("extended_fields")
    public JSONArray extendedFields;

}
