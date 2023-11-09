package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import org.elasticsearch.search.SearchHit;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

/**
 * @author liangguolu
 * @version 1.0
 * @date 2021/11/12 18:58
 */
public interface HaveAlarmLogService {
    /**
     *
     * 查询ES告警数据
     *
     * @return list
     */
    public List<SearchHit> queryAlarmDataForEs();

    /**
     *
     * 补充告警日志数据
     *
     * @param esResults 告警初数据
     * @return list
     */
    public List<AlarmEventAttribute> haveAlarmLogData(List<SearchHit> esResults, Map<String, List<EventTable>> eventTableMap);

}
