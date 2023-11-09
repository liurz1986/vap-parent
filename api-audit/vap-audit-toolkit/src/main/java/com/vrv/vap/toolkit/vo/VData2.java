package com.vrv.vap.toolkit.vo;

import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.ValidateTools.RetMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("无分页返回实体")
public class VData2<T> extends Result {

    @ApiModelProperty("查询结果")
    private T data;

    /**
     * 是否成功
     */
    @ApiModelProperty("是否成功")
    private boolean success;

    public VData2() {
    }

    public VData2(T data) {
        this(data, RetMsgEnum.SUCCESS);
    }

    public VData2(T data, RetMsgEnum rm) {
        super(rm);
        this.data = data;
    }

    /**VData2*/
    public VData2(T data, RetMsgEnum rm, boolean success) {
        super(rm);
        this.data = data;
        this.success = success;
    }

    public VData2(T data, RetMsg rm) {
        super(rm);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
