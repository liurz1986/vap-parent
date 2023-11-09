package com.vrv.vap.server.push.enums;

import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.Result;

public enum  ErrorCode implements ResultAble {


    MUST_HAVE_CONTENT("1000","title content url 至少传一个"),
    MUST_HAVE_ROLEID("1001","roleid 需要传"),
    EMPTY_USERS("1002","用户组里面没有用户"),
    SEND_ERROR("1003","发送失败"),

    ;



    private Result result;

    public Result getResult() {
        return this.result;
    }

    ErrorCode(String code, String message){
        this.result = new Result(code,message);
    }
}
