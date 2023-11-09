package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.EventTaVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.IdTitleValue;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AppAlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 */
public interface AlarmEventManagementService {
    /**
     * 获得告警事件列表
     * @param query 查询条件
     * @return 返回
     */
    PageRes_ES<AlarmEventAttributeVO> getAlarmDealPager(EventDetailQueryVO query);

    /**
     * 获取关注资产统计
     * @param query 参数
     * @param top 参数
     * @return
     */
    List<NameValue> getAssetOfConcern(EventDetailQueryVO query,Integer top);

    /**
     * 设置关注的资产的ip
     * @param param 参数
     * @return
     */
    Boolean setConcernIps(Map<String, List<String>> param);

    /**
     * 获取关注的资产的ip
     * @return
     */
    List<String> getConcernIps();

    /**
     * 按照事件等级统计数量
     * @param query 参数
     * @return
     */
    List<NameValue> getStatisticsByAlarmRiskLevel(EventDetailQueryVO query);

    /**
     * 按照事件处置状态分组统计
     * @param query 参数
     * @param top 参数
     * @return
     */
    List<IdTitleValue> getStatisticsByEventType(EventDetailQueryVO query, Integer top);

    /**
     * 按照事件名称分组统计
     * @param query
     * @param top
     * @return
     */
    List<NameValue> getStatisticsByEventName(EventDetailQueryVO query,Integer top);

    /**
     * 根据告警处理状态统计
     * @param query
     * @param top
     * @return
     */
    List<IdTitleValue> getStatisticsByAlarmDealState(EventDetailQueryVO query,Integer top);

    /**
     * 按照部门分组统计
     * @param query 参数
     * @param top 参数
     * @return
     */
    List<NameValue> getStatisticsByDepartment(EventDetailQueryVO query,Integer top);

    /**
     * 统计数量
     *
     * @param query 参数
     * @return
     */
    Long getEventCount(EventDetailQueryVO query);

    /**
     * 告警趋势
     *
     * @param query 参数
     * @param timeType 类型
     * @return
     */
    List<NameValue> getAlarmTrend(EventDetailQueryVO query,String timeType);

    /**
     * 将事件标记为已读
     *
     * @param eventId 事件ID
     * @return
     */
    AlarmEventAttribute setAlarmEventMarkRead(String eventId);

    /**
     * 将事件标记为已读
     *
     * @param vo
     * @return
     */
    List<AlarmEventAttribute> setAlarmEventMarkRead(RiskRuleIdVO vo);

    /**
     * 查询单条事件信息
     *
     * @param eventId 事件ID
     * @return
     */
    AlarmEventAttribute getAlarmEvent(String eventId);

    /**
     * 获得追溯原始日志
     *
     * @param eventId 事件ID
     * @return
     */
    List<Map<String, Object>> getAlarmEventLogs(String eventId);

    /**
     * 获得追溯原始日志
     * @param eventId 事件ID
     * @param indexid 索引ID
     * @return
     */
    List<Map<String, Object>> getAlarmEventLogs(String eventId,String indexid);

    /**
     * 生成导出文件
     *
     * @param query
     * @param request 
     * @return
     */
    String createReportFile(EventDetailQueryVO query, HttpServletRequest request);

    /**
     * 导出文件
     * 
     * @param fileName
     * @param request
     * @param response
     */
    void downloadReportFile(String fileName, HttpServletRequest request,HttpServletResponse response);


    Result updateAlarmDealTest( Map<String, Integer> param);

    Long getEventAbnormalCount(EventDetailQueryVO query);

    Integer getEventAbnormalUserCount(EventDetailQueryVO query);

    List<NameValue> getEventAbnormalTypeCount(EventDetailQueryVO query);

    List<NameValue> getEventAbnormalTypeUserCount(EventDetailQueryVO query);

    PageRes_ES<AlarmEventAttributeVO> getAlarmDealAbnormalPager(EventDetailQueryVO query);

    List<NameValue> getAlarmTypeTrend(EventDetailQueryVO query, String loginException);

    Map<String,Long> getAppEventCount(EventDetailQueryVO query);

    PageRes_ES<AppAlarmEventAttributeVO> getAlarmDealAppAbnormalPager(EventDetailQueryVO query);

    List<NameValue> getEventAbnormalAreaCount(EventDetailQueryVO query);

    List<NameValue> getEventAbnormalOrgCount(EventDetailQueryVO query);

    Long getEventUserAbnormalCount(EventDetailQueryVO query);

    List<NameValue> getAlarmUserAbnormalTrend(EventDetailQueryVO query);

    PageRes_ES<AppAlarmEventAttributeVO> getAlarmDealUserAbnormalPager(EventDetailQueryVO query);

    Map<String,Long> getAllEventCount();

    PageRes_ES<AppAlarmEventAttributeVO> getAlarmEventPager(EventDetailQueryVO query);

    Result updateAlarmTypeTest();

    List<NameValue> getAssetAlarmEventTop10(String type);

    List<Map<String, Map<String, Long>>> culStealLeakValue();

    void setEventNumber(List<AssetVO> list);

    PageRes_ES<Map<String, Object>> getAlarmEventLogsPage(EventDetailQueryVO query);

    Integer abnormalAssetCount();

    List<NameValue> abnormalAssetCountTrend(EventDetailQueryVO query, String timeType);

    Map<String,Integer> dayAlarmWorthAssetCount();

    Map<String, Long> getIpGroup();

    List<EventTaVo> getEventObject(String eventId);
}
