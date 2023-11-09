package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("PrintBurnModel")
public class PrintBurnModel extends PageModel{
    @ApiModelProperty("类型，1打印 0刻录")
    private String opType;
    @ApiModelProperty("时间跨度 默认天 传1小时")
    private String intervl = "2";

    public String getInteval() {
        return intervl;
    }

    public void setIntervl(String intervl) {
        this.intervl = intervl;
    }
    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }
}
