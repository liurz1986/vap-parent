package com.vrv.vap.toolkit.vo;

import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.ValidateTools.RetMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 无分页返回实体
 */
@ApiModel("VData")
public class VData<T> extends Result {

    @ApiModelProperty("查询结果")
    private T data;

    public VData() {
    }

    public VData(T data) {
        this(data, RetMsgEnum.SUCCESS);
    }

    public VData(T data, RetMsgEnum rm) {
        super(rm);
        this.data = data;
    }

    public VData(T data, RetMsg rm) {
        super(rm);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
