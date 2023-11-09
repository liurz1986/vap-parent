package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util;

import com.vrv.vap.jpa.web.IResultCode;
import com.vrv.vap.jpa.web.ResultCodeEnum;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 15:44
 */
public class ReportResultUtil {
    private ReportResultUtil() {
    }

    public static <T> ReportResult<T> successList(T data) {
        ReportResult<T> result = new ReportResult();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        result.getData().setList(data);
        return result;
    }

    public static <T> ReportResult<T> success(T data) {
        ReportResult<T> result = new ReportResult();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        result.getData().setMap(data);
        return result;
    }

    public static <T> ReportResult<T> error(IResultCode result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> ReportResult<T> error(Integer code, String msg) {
        ReportResult<T> res = new ReportResult();
        res.setCode(code);
        res.setMessage(msg);
        return res;
    }
}
