package com.vrv.vap.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wh1107066
 */
@ApiModel(value = "VData", description = "Api 响应体：响应指定数据")
public class VData<T> extends Result {

    @ApiModelProperty(value = "响应的数据实体")
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public VData(String code) {
        super(code);
    }

    public VData() {
    }

    public VData(T t) {
        this.data = t;
    }

    public VData(String code, String message) {
        super(code, message);
    }

    public VData(String code, String message, T data) {
        super(code, message);
        this.data = data;
    }
}
