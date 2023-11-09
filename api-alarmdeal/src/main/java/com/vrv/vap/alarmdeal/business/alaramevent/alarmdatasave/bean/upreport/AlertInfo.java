package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import lombok.Data;

import java.util.List;

/**
 *监管事件上报
 */
@Data
public class AlertInfo {
    private Alert alert;
    //引发事件触发的原始日志
    private List<String> logs;
}
