package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.EventAlarmTaskNodeMsg;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmDealAggregationRow;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.page.PageReq;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月23日 9:23
 */
public interface AlarmEventManagementForESService  {
    /**
     * 功能描述
     *
     * @param query
     * @param pageReq
     * @param auth
     * @return
     */
    PageRes_ES<AlarmEventAttribute> getPageQueryResult(EventDetailQueryVO query, PageReq pageReq, boolean auth);

    /**
     * 处理参数(权限)
     * @return
     */
    List<QueryCondition_ES> getDataPermissions();

    /**
     * 处理参数（根据入参）
     * @param query
     * @return
     */
    List<QueryCondition_ES> getAlarmDealQuerys(EventDetailQueryVO query);

    /**
     * 逾期/小于30分钟/小于4小时/小于1天
     * @param now 现在时间
     * @param event 告警事件
     */
    void autoAppendOverdueLabel(Date now, AlarmEventAttribute event);
    
    /**
     * 获取我关注的资产
     * @return
     */
    public List<String> getIpsOfConcern();

    /**
     * 查询数据
     * @param request
     * @return
     */
    List<AlarmEventAttribute> findAll(List<QueryCondition_ES> request);

    /**
     * 查询数据
     * @param querys
     * @param top
     * @param groupByName
     * @return
     */
    List<NameValue> getStatisticsByStringField(List<QueryCondition_ES> querys, int top, String groupByName);

    /**
     * 查询统计值
     * @param requests
     * @return long
     */
    long count(List<QueryCondition_ES> requests);

    /**
     * 处理参数
     * @param query
     * @return
     */
    public List<QueryCondition_ES> getQuerys(EventDetailQueryVO query);

    List<Map<String, Object>> queryStatistics(List<QueryCondition_ES> conditions, SearchField field);

    AlarmEventAttribute getDocByEventId(String eventId);

    void saveAlarmEventData(AlarmEventAttribute doc);

    void saveAlarmEventDatas(List<AlarmEventAttribute> doc);

    List<Map<String, Object>> getLogByEventIdAndIndexName(String indexName,List<String> eventId);

    String createReportFile(EventDetailQueryVO query, String token);

    String getIndexName();

    List<AlarmDealAggregationRow> getAggDataRows(EventDetailQueryVO query);

    Map<String,Object> getDoc(String indexName,String id);


    Map<String,Long> getCountGroupByField(String indexName,String fieldName,List<QueryCondition_ES> conditions);

    Map<String, Long> queryStatisticsByTime(String indexName,List<QueryCondition_ES> conditions, String fieldName, DateHistogramInterval timeInterval, String timeFormat);
    void addList(String indexName, List<AlarmEventAttribute> entities);
    public void saveEventAlarmDealChange(EventAlarmTaskNodeMsg nodeMsg);

    public void saveEventAlarmDealChange(String eventId,String analysis);
    void save(AlarmEventAttribute doc);
    AlarmEventAttribute getDoc(String eventId);
    public String getBaseField();

    Map<String, Long> getCountGroupNumByField(String indexName, String s, List<QueryCondition_ES> querys);

    PageRes_ES<AlarmEventAttribute> getPageQueryAbnormalResult(EventDetailQueryVO query, EventDetailQueryVO query1, boolean b);

    PageRes_ES<AlarmEventAttribute> getPageQueryAppAbnormalResult(EventDetailQueryVO query, EventDetailQueryVO query1, boolean b,List<Integer> integers);

    Map<String, Long> getCountGroupNumByFieldSize(String indexName, String principalIp, List<QueryCondition_ES> querys, int i);

    List<Map<String, Map<String, Long>>> getAggIpLevelData(String indexName, String principalIp, String alarmRiskLevel, List<QueryCondition_ES> querys);


    AlarmEventAttribute findOne(List<QueryCondition_ES> baseQueryParam);
}
