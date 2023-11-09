package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;


import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface EventAlarmSettingService extends BaseService<EventAlarmSetting, String> {
    public List<EventAlarmSetting> queryAllEventAlarmSetting();

}
