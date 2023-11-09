package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *@author lilang
 *@date 2022/10/14
 *@description
 */
public interface AlarmItemGroupMapper extends BaseMapper<AlarmItemGroup> {

    @Select("SELECT alarm_type,alarm_level,alarm_source,alarm_desc,count(*) alarmCount,MAX(alarm_time) lastTime,MIN(alarm_time) firstTime FROM alarm_item_collection WHERE alarm_status = 0 GROUP BY alarm_type,alarm_level,alarm_source,alarm_desc")
    List<AlarmItemGroup> getGroupData();
}
