package com.vrv.vap.admin.mapper;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.admin.vo.AlarmItemGroupVO;
import com.vrv.vap.admin.vo.AlarmItemVO;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;


public interface AlarmCollectionMapper extends BaseMapper<AlarmItem> {
    AlarmItemGroup getAlarmItemsByGroup(@Param("alarmItemVO") AlarmItemVO alarmItemVO);

    int updateAlarmItems(@Param("AlarmItemGroupVO") AlarmItemGroupVO alarmItemGroupVO);

    Page<Map> getAlarmTrend(@Param("alarmItemVO") AlarmItemVO alarmItemVO);
}