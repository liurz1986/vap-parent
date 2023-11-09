package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

public class BaseAreaQuery {

    @ApiModelProperty("ip值")
    private String ip;

    @ApiModelProperty("区域码")
    private String areaCode;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
