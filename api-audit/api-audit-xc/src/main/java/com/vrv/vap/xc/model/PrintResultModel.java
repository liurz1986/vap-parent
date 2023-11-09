package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("PrintResultModel")
public class PrintResultModel extends PageModel{
    @ApiModelProperty("结果状态，1成功 0失败")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
