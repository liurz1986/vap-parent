package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class DeviceInfoModel extends BaseDeviceInfo{

    /**
     *   查询字段,ip地址
     */
    @ApiModelProperty("精确查询字段:ip地址")
    private String _ip;

    @ApiModelProperty("设备类型查询条件")
    private String devTypeName;

    public String get_ip() {
        return _ip;
    }

    public void set_ip(String _ip) {
        this._ip = _ip;
    }

    public String getDevTypeName() {
        return devTypeName;
    }

    public void setDevTypeName(String devTypeName) {
        this.devTypeName = devTypeName;
    }
}