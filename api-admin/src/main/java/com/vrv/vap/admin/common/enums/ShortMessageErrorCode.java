package com.vrv.vap.admin.common.enums;

import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.Result;

/**
 * 短信发送状态错误码
 */
public enum ShortMessageErrorCode implements ResultAble {

    NO_PHONE("1","发送目标没有配置手机号"),
    NO_TEMPLATE("2","发送模板不存在"),
    PART_NO_PHONE("3","部分用户发送成功"),
    ERROR("500","内部错误"),
    ;

    private Result result;

    @Override
    public Result getResult() {
        return this.result;
    }

    ShortMessageErrorCode(String code, String message){
        this.result = new Result(code,message);
    }
}
