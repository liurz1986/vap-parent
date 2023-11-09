package com.vrv.vap.data.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "通用请求参数")
public class RequestParam {

    @ApiModelProperty("关键字")
    private String q;

    @ApiModelProperty("返回字段")
    private String[] fields;

    @ApiModelProperty("必须满足条件")
    private RequestParamItem[] must;

    @ApiModelProperty("必须不满足条件")
    private RequestParamItem[] must_not;

    @ApiModelProperty("should 满足条件")
    private RequestParamItem[] should;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public RequestParamItem[] getMust() {
        return must;
    }

    public void setMust(RequestParamItem[] must) {
        this.must = must;
    }

    public RequestParamItem[] getMust_not() {
        return must_not;
    }

    public void setMust_not(RequestParamItem[] must_not) {
        this.must_not = must_not;
    }

    public RequestParamItem[] getShould() {
        return should;
    }

    public void setShould(RequestParamItem[] should) {
        this.should = should;
    }
}
