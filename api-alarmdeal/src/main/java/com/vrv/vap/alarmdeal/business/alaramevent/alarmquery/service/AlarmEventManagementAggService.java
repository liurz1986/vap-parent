package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmDealAggregationRow;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.page.PageRes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 14:14
 */
public interface AlarmEventManagementAggService {
    /**
     * 获得告警事件(聚合)列表
     * @param query
     * @return
     */
    PageRes<AlarmDealAggregationRow> getAlarmDealAggetStatisticsCountgregationPager(EventDetailQueryVO query);

    /**
     * 统计数量
     * @param query
     * @return
     */
    List<NameValue> getStatisticsCount( EventDetailQueryVO query);

    /**
     * 生成导出文件
     * @param query
     * @param request
     * @return
     */
    String createReportFile( EventDetailQueryVO query, HttpServletRequest request);

    /**
     * 导出文件
     * @param fileName
     * @param request
     * @param response
     */
    void downloadReportFile(String fileName, HttpServletRequest request,HttpServletResponse response);

    /**
     * 获取关注事件统计
     * @param query
     * @param top
     * @return java.util.List<com.vrv.vap.jpa.web.NameValue>
     */
    List<NameValue> getEventNameOfConcern(EventDetailQueryVO query,Integer top);

    /**
     * 获取所有事件名称（不判断数据权限）
     * @param query
     * @param top
     * @return
     */
    List<NameValue> getDistinctEventName(EventDetailQueryVO query,Integer top);

    /**
     * 获得告警事件列表（不判断数据权限）
     * @param query
     * @return
     */
    PageRes_ES<AlarmEventAttribute> getAllAlarmDealPager(EventDetailQueryVO query);
}
