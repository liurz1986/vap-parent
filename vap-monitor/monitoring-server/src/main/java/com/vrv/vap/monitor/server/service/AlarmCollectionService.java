package com.vrv.vap.monitor.server.service;

import com.github.pagehelper.Page;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.monitor.server.model.AlarmItem;
import com.vrv.vap.monitor.server.model.AlarmItemGroup;
import com.vrv.vap.monitor.server.vo.AlarmItemGroupVO;
import com.vrv.vap.monitor.server.vo.AlarmItemVO;

import java.util.List;
import java.util.Map;

public interface AlarmCollectionService extends BaseService<AlarmItem>{
    Page<AlarmItemGroup> getAlarmItemsByGroup(AlarmItemVO alarmItemVO);

    boolean updateAlarmItems(AlarmItemGroupVO alarmItemGroupVO);

    List<Map> getAlarmTrend(AlarmItemVO alarmItemVO);

    void pushAlarm(String alarmType, String desc);

    void pushAlarmToKafka(String alarmType, String desc);
}
