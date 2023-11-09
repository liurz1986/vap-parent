package com.vrv.vap.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Result", description = "Api 响应体")
public class Result {

    @ApiModelProperty(value="错误码，正常情况下返回 \"0\" 当不为 \"0\" 时，说明接口发生了问题而不能完成执行，具体原因参考消息说明")
    private String code;

    @ApiModelProperty(value="错误消息,当错误码不为 \"0\" 时，描述错误产生的原因")
    private String message;

    public Result() { }

    public Result(String code) {
        this.code = code;
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
