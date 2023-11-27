package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import lombok.Data;

import java.util.List;

/**
 * 监管事件type=2
 */
@Data
public class RegularEvent extends AbstractUpEvent{

    private List<AlertInfo> alert_info;



}
