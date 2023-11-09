package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne;

import lombok.Data;

import java.util.List;

/**
 * @author wudi
 * @date 2022/4/20 16:49
 */
@Data
public class ThreeInOneEvent {

    private ThreeInOneEventData data;

    private List<ThreeInOneEventList> list;

}
