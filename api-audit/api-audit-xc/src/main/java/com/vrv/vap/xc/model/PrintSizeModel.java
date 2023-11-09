package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("PrintSizeModel")
public class PrintSizeModel extends PageModel{
    @ApiModelProperty("区间")
    private long[][] interval;

    public long[][] getInterval() {
        return interval;
    }

    public void setInterval(long[][] interval) {
        this.interval = interval;
    }
}
