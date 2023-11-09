package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *  趋势对象
 */
@ApiModel("趋势对象")
public class TrendModel {

    @ApiModelProperty("日期")
    private String dateFlag;

    @ApiModelProperty("数量")
    private Long count;

    public String getDateFlag() {
        return dateFlag;
    }

    public void setDateFlag(String dateFlag) {
        this.dateFlag = dateFlag;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}