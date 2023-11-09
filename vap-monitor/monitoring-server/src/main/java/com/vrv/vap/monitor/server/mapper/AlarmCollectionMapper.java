package com.vrv.vap.monitor.server.mapper;

import com.github.pagehelper.Page;
import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.monitor.server.model.AlarmItem;
import com.vrv.vap.monitor.server.model.AlarmItemGroup;
import com.vrv.vap.monitor.server.vo.AlarmItemGroupVO;
import com.vrv.vap.monitor.server.vo.AlarmItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AlarmCollectionMapper extends BaseMapper<AlarmItem> {
    AlarmItemGroup getAlarmItemsByGroup(@Param("alarmItemVO") AlarmItemVO alarmItemVO);

    int updateAlarmItems(@Param("AlarmItemGroupVO") AlarmItemGroupVO alarmItemGroupVO);

    Page<Map> getAlarmTrend(@Param("alarmItemVO") AlarmItemVO alarmItemVO);
}