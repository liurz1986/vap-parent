package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("查询检测器")
public class NetworkMonitorQuery extends Query {

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("设备所属单位")
    @QueryLike
    private String deviceBelong;

    @ApiModelProperty("设备部署位置")
    @QueryLike
    private String deviceLocation;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceBelong() {
        return deviceBelong;
    }

    public void setDeviceBelong(String deviceBelong) {
        this.deviceBelong = deviceBelong;
    }

    public String getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(String deviceLocation) {
        this.deviceLocation = deviceLocation;
    }
}
