package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

public class QswsQuery {

    @ApiModelProperty("查询语句")
    private String qsws;

    public String getQsws() {
        return qsws;
    }

    public void setQsws(String qsws) {
        this.qsws = qsws;
    }
}
