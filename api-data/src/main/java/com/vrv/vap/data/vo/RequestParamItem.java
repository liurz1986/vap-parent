package com.vrv.vap.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "通用请求条件")
public class RequestParamItem {

    @ApiModelProperty("字段名称")
    private String field;
    @ApiModelProperty("对比条件，支持 > >= < <= = like prefix_like suffix_like exist in")
    private String operation;
    @ApiModelProperty("对比值")
    private String value;

    public RequestParamItem() {
    }

    public RequestParamItem(String field, String operation, String value) {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
