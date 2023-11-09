package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;


import java.util.List;

public interface AlarmEventAttributeService {
    String getIndexName();

    List<AlarmEventAttribute> getPageQueryResult(String startTime, String endTime);

}
