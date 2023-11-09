package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import lombok.Data;

/**
 * 监管事件type=2
 */
@Data
public class RegularEvent extends AbstractUpEvent{
    private AlertInfo alert_info;

}
