package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util;

import com.vrv.vap.jpa.web.IResultCode;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 15:46
 */
public class ReportResult<T> implements IResultCode {
    private Integer code;
    private String message;
    private CommonResult data;

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommonResult getData() {
        return data;
    }

    public void setData(CommonResult data) {
        this.data = data;
    }
}
