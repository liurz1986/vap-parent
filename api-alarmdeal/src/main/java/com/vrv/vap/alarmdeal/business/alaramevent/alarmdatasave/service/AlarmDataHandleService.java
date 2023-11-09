package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;

import java.util.List;
import java.util.Map;

/**
 * @author 梁国露
 * @date 2021年11月01日 15:25
 */
public interface AlarmDataHandleService {

    public void handleBaseData(WarnResultLogTmpVO warnResultLogTmpVO, AlarmEventAttribute doc);

    /**
     * 获取原始日志表 信息
     *
     * @return map
     */
    public Map<String, List<EventTable>> getEventTableMap();

    /**
     * 告警规则表 信息
     *
     * @return map
     */
    public Map<String, List<RiskEventRule>> getRiskEventRuleMap();

    /**
     * 告警规则表 信息
     *
     * @return map
     */
    public Map<String, List<RiskEventRule>> getRiskEventRuleMapForId();

    /**
     * 添加策略
     */
    public void putRiskEventRuleMap(RiskEventRule riskEventRule);

    /**
     * 事件分类表 信息
     *
     * @return map
     */
    public Map<String, List<EventCategory>> getEventCategoryMap();

    /**
     * 告警事件配置
     *
     * @return map
     */
    public Map<String, List<EventAlarmSetting>> getEventAlarmSettingMap();

    /**
     * 添加事件 配置
     *
     * @param eventAlarmSetting
     */
    public void putEventAlarmSettingMap(EventAlarmSetting eventAlarmSetting);

    /**
     * 获取版本号信息
     *
     * @param warnBean 告警bean
     * @return string 版本号
     */
    public String getVersion(WarnResultLogTmpVO warnBean);

    /**
     * 补充日志信息
     *
     * @param logIds     id数组
     * @param indexName  索引名称
     * @param eventTable 原始日志信息
     * @param doc        告警数据
     */
    public void haveLogData(String[] logIds, String indexName, EventTable eventTable, AlarmEventAttribute doc);

    /**
     * 补全分类信息
     *
     * @param riskEventRule 告警事件规则对象
     * @param eventCategory 事件分类对象
     * @param doc           告警对象
     */
    public void formEventCategory(RiskEventRule riskEventRule, EventCategory eventCategory, AlarmEventAttribute doc);

    /**
     * 补全告警状态信息
     *
     * @param alarmStatus 告警状态
     * @param doc         告警对象
     */
    public void formAlarmStatus(int alarmStatus, AlarmEventAttribute doc);

    /**
     * 补全认证信息
     *
     * @param doc
     */
    public void formAuthData(Map<String, List<EventAlarmSetting>> eventAlarmSettingMap, Map<String, List<EventCategory>> eventCategoryMap, AlarmEventAttribute doc);

    /**
     * 上传告警信息
     *
     * @param docs 告警对象
     */
    public void pushAlarmData(List<AlarmEventAttribute> docs);


    /**
     * 上传级联信息
     *
     * @param docs 告警对象
     */
    public void pushSuperviseData(List<AlarmEventAttribute> docs);


    /**
     * 功能描述
     *
     * @param eventLogDstBean
     * @param doc
     */
    public void handleLogData(EventLogDstBean eventLogDstBean, AlarmEventAttribute doc);


}
