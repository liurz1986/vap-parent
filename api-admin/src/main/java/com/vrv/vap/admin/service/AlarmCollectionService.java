package com.vrv.vap.admin.service;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.admin.vo.AlarmItemGroupVO;
import com.vrv.vap.admin.vo.AlarmItemVO;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by CodeGenerator on 2018/03/20.
 */
public interface AlarmCollectionService extends BaseService<AlarmItem> {
    Page<AlarmItemGroup> getAlarmItemsByGroup(AlarmItemVO alarmItemVO);

    boolean updateAlarmItems(AlarmItemGroupVO alarmItemGroupVO);

    List<Map> getAlarmTrend(AlarmItemVO alarmItemVO);

    void deleteDealedGroup(Object object);
}
