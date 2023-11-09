package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModelProperty;

public class PrintTimeModel extends PageModel{
    @ApiModelProperty("时间跨度 默认天 传1小时 3月")
    private String inteval = "2";

    public String getInteval() {
        return inteval;
    }

    public void setInteval(String inteval) {
        this.inteval = inteval;
    }
}
