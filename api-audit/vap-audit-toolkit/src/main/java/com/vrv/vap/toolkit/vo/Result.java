package com.vrv.vap.toolkit.vo;

import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.ValidateTools.RetMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 返回信息
 */
@ApiModel("Result")
public class Result implements Serializable {

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("信息")
    private String message;

    public Result() {
        this(RetMsgEnum.SUCCESS);
    }

    public Result(RetMsgEnum rm) {
        this(rm.getCode(), rm.getMsg());
    }

    public Result(RetMsg rm) {
        this(rm.getCode(), rm.getMsg());
    }

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
