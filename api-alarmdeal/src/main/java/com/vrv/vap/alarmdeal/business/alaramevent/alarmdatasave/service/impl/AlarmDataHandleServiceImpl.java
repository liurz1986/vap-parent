package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventLogDstBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.Label;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.DisponseConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataForLogService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.IUpReportEventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.AlarmDataSaveUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventUrge;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.VapUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 梁国露
 * @date 2021年11月01日 15:26
 */
@Service
public class AlarmDataHandleServiceImpl implements AlarmDataHandleService {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(AlarmDataHandleServiceImpl.class);

    /**
     * gson对象
     */
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * eventTabelService
     */
    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private RiskEventRuleService riskEventRuleService;

    @Autowired
    private EventCategoryService eventCategoryService;

    @Autowired
    private EventAlarmSettingService eventAlarmSettingService;


    @Autowired
    MapperUtil mapper;

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;


    @Autowired
    AlarmDataForLogService alarmDataForLogService;

    @Autowired
    ElasticSearchRestClient elasticSearchRestClient;
    @Autowired
    private IUpReportCommonService reportCommonService;

    private static Map<String, Integer> eventTypeMap = new ConcurrentHashMap<>();

    static {
        eventTypeMap.put("/safer/ConfigurationCompliance", 1); //管理配置违规 1
        eventTypeMap.put("/safer/NetworkSecurityException", 2); //网络和应用系统异常 2
        eventTypeMap.put("/safer/AbnormalUserBehavior", 3); //用户行为异常 3
        eventTypeMap.put("/safer/OperationaBehavior", 4); //运维行为异常 4
        eventTypeMap.put("/safer/AbnormalApplicationBehavior", 5); // 应用异常 5
        eventTypeMap.put("/safer/ConnectivityAbnormal", 6); //互联互通异常  6
        eventTypeMap.put("/safer/adminException", 7); //管理员行为异常  7
        eventTypeMap.put("/safer/boundaryException", 8); //外部边界异常  8
    }

    /**
     * 处理基础数据
     *
     * @param warnResultLogTmpVO
     * @param doc
     */
    @Override
    public void handleBaseData(WarnResultLogTmpVO warnResultLogTmpVO, AlarmEventAttribute doc) {
        doc.setIsRead(false);
        doc.setIsUrge(false);
        doc.setIsSupervise(false);
        doc.setEventCreattime(warnResultLogTmpVO.getTriggerTime());
        doc.setEventId(warnResultLogTmpVO.getId());
        doc.setEventDetails(warnResultLogTmpVO.getAlamDesc());
        doc.setPrinciple(warnResultLogTmpVO.getPrinciple());
        doc.setDataSource(warnResultLogTmpVO.getDataSource());
        doc.setTag(warnResultLogTmpVO.getTag());
        doc.setSrcIps(warnResultLogTmpVO.getSrc_ips());
        doc.setDstIps(StringUtils.isNotBlank(warnResultLogTmpVO.getDstIps()) ? warnResultLogTmpVO.getDstIps() : "");
        doc.setPrincipalIp(warnResultLogTmpVO.getRelatedIps());
        //设值主体计算Ip计算值
        doc.setPrincipalIpNum(VapUtil.ip2int(doc.getPrincipalIp()));
        String version = AlarmDataSaveUtil.getVersion(warnResultLogTmpVO.getMultiVersions());
        doc.setEventVersion(version);

        // 设置规则编码
        if (warnResultLogTmpVO.getRuleCode().split("-").length > 1) {
            doc.setFilterCode(warnResultLogTmpVO.getRuleCode().split("-")[1]);
        }
        logger.info("AlarmDataSaveJob handleAlarmData doc = {}", JSONObject.toJSONString(doc));
    }

    /**
     * 获取原始日志表 信息
     *
     * @return map
     */
    @Override
    public Map<String, List<EventTable>> getEventTableMap() {
        Map<String, List<EventTable>> result = new ConcurrentHashMap<>();
        // 判断缓存
        if (CommomLocalCache.containsKey("eventTable")) {
            result = CommomLocalCache.get("eventTable");
        } else {
            // 缓存中不存在，查询数据
            Result<List<EventTable>> eventTableResult = eventTabelService.getAllEventTable();
            List<EventTable> eventTableList = eventTableResult.getList();
            if (CollectionUtils.isNotEmpty(eventTableList)) {
                // 查询新数据后，更新缓存
                result = eventTableList.parallelStream().collect(Collectors.groupingBy(EventTable::getName));
                CommomLocalCache.put("eventTable", result, 2, TimeUnit.HOURS);
            }
        }
        logger.debug("AlarmDataHandleService getEventTableMap result success");
        return result;
    }

    /**
     * 告警规则表 信息
     *
     * @return map
     */
    @Override
    public Map<String, List<RiskEventRule>> getRiskEventRuleMap() {
        Map<String, List<RiskEventRule>> result = new ConcurrentHashMap<>();
        // 判断缓存
        if (CommomLocalCache.containsKey("riskEventRule")) {
            result = CommomLocalCache.get("riskEventRule");
        } else {
            // 缓存中不存在，查询数据
            List<RiskEventRule> riskEventRuleList = riskEventRuleService.getAllRiskEventRule();
            if (CollectionUtils.isNotEmpty(riskEventRuleList)) {
                // 查询新数据后，更新缓存
                result = riskEventRuleList.parallelStream().collect(Collectors.groupingBy(RiskEventRule::getRuleCode));
                CommomLocalCache.put("riskEventRule", result, 2, TimeUnit.HOURS);
            }
        }
        logger.debug("AlarmDataHandleService getRiskEventRuleMap success");
        return result;
    }

    /**
     * 告警规则表 信息
     *
     * @return map
     */
    @Override
    public Map<String, List<RiskEventRule>> getRiskEventRuleMapForId() {
        Map<String, List<RiskEventRule>> result = new ConcurrentHashMap<>();
        // 判断缓存
        if (CommomLocalCache.containsKey("riskEventRule-id")) {
            result = CommomLocalCache.get("riskEventRule-id");
        } else {
            // 缓存中不存在，查询数据
            List<RiskEventRule> riskEventRuleList = riskEventRuleService.getAllRiskEventRule();
            if (CollectionUtils.isNotEmpty(riskEventRuleList)) {
                // 查询新数据后，更新缓存
                result = riskEventRuleList.parallelStream().collect(Collectors.groupingBy(RiskEventRule::getId));
                CommomLocalCache.put("riskEventRule-id", result, 2, TimeUnit.HOURS);
            }
        }
        logger.debug("AlarmDataHandleService getRiskEventRuleMap success");
        return result;
    }

    @Override
    public void putRiskEventRuleMap(RiskEventRule riskEventRule) {
        Map<String, List<RiskEventRule>> map = getRiskEventRuleMap();
        map.put(riskEventRule.getRuleCode(), Arrays.asList(riskEventRule));
        CommomLocalCache.put("riskEventRule", map, 2, TimeUnit.HOURS);

        Map<String, List<RiskEventRule>> map1 = getRiskEventRuleMapForId();
        map1.put(riskEventRule.getId(), Arrays.asList(riskEventRule));
        CommomLocalCache.put("riskEventRule-id", map, 2, TimeUnit.HOURS);
    }

    /**
     * 事件分类表 信息
     *
     * @return map
     */
    @Override
    public Map<String, List<EventCategory>> getEventCategoryMap() {
        Map<String, List<EventCategory>> result = new ConcurrentHashMap<>();
        // 判断缓存
        if (CommomLocalCache.containsKey("eventCategory")) {
            result = CommomLocalCache.get("eventCategory");
        } else {
            // 缓存中不存在，查询数据
            List<EventCategory> eventCategoryList = eventCategoryService.queryAllEventCategory();
            if (CollectionUtils.isNotEmpty(eventCategoryList)) {
                // 查询新数据后，更新缓存
                result = eventCategoryList.parallelStream().collect(Collectors.groupingBy(EventCategory::getId));
                CommomLocalCache.put("eventCategory", result, 2, TimeUnit.HOURS);
            }
        }
        logger.debug("AlarmDataHandleService getEventCategoryMap result success");
        return result;
    }

    /**
     * 告警事件配置
     *
     * @return map
     */
    @Override
    public Map<String, List<EventAlarmSetting>> getEventAlarmSettingMap() {
        Map<String, List<EventAlarmSetting>> result = new ConcurrentHashMap<>();
        // 判断缓存
        if (CommomLocalCache.containsKey("eventAlarmSetting")) {
            result = CommomLocalCache.get("eventAlarmSetting");
        } else {
            // 缓存中不存在，查询数据
            List<EventAlarmSetting> eventCategoryList = eventAlarmSettingService.queryAllEventAlarmSetting();
            if (CollectionUtils.isNotEmpty(eventCategoryList)) {
                // 查询新数据后，更新缓存
                result = eventCategoryList.parallelStream().collect(Collectors.groupingBy(EventAlarmSetting::getLinkGuid));
                CommomLocalCache.put("eventAlarmSetting", result, 2, TimeUnit.HOURS);
            }
        }
        logger.debug("AlarmDataHandleService getEventAlarmSettingMap result success");
        return result;
    }

    @Override
    public void putEventAlarmSettingMap(EventAlarmSetting eventAlarmSetting) {
        Map<String, List<EventAlarmSetting>> map = getEventAlarmSettingMap();
        map.put(eventAlarmSetting.getGuid(), Arrays.asList(eventAlarmSetting));
        CommomLocalCache.put("eventAlarmSetting", map, 2, TimeUnit.HOURS);
    }

    /**
     * 获取版本号信息
     *
     * @param warnBean 告警bean
     * @return string 版本号
     */
    @Override
    public String getVersion(WarnResultLogTmpVO warnBean) {
        return AlarmDataSaveUtil.getVersion(warnBean.getMultiVersions());
    }


    /**
     * 补充日志信息
     *
     * @param logIds     id数组
     * @param indexName  索引名称
     * @param eventTable 原始日志信息
     * @param doc        告警数据
     */
    @Override
    public void haveLogData(String[] logIds, String indexName, EventTable eventTable, AlarmEventAttribute doc) {
        // 查询资产数据
        List<LogIdVO> logs = new CopyOnWriteArrayList<>();
        // completionBaseDataForDb(logIds, indexName, eventTable, doc, logs);
        List<QueryCondition_ES> conditions = new CopyOnWriteArrayList<>();
        conditions.add(QueryCondition_ES.in("guid", logIds));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
        try {
            SearchResponse response = elasticSearchRestClient.getDocs(new String[]{indexName + "*"}, queryBuilder, null, null, 0, logIds.length);
            if (response.getHits() != null) {
                long total = response.getHits().getTotalHits().value;
                if (total > 0) {
                    completionBaseDataForEs(eventTable, doc, logs, response);
                }
            }
//            if (CollectionUtils.isEmpty(logs)) {
//                completionBaseDataForDb(logIds, indexName, eventTable, doc, logs);
//            }
        } catch (Exception ex) {
            logger.error("告警生成 补全日志信息，查询es日志报错！");
        }
        // 设置日志，以及应用、人员、app、文件数量
        doc.setLogs(logs);
    }


    /**
     * 通过数据库补全基础信息
     *
     * @param logIds
     * @param indexName
     * @param eventTable
     * @param doc
     * @param logs
     */
    private void completionBaseDataForDb(String[] logIds, String indexName, EventTable eventTable, AlarmEventAttribute doc, List<LogIdVO> logs) {
        LogIdVO vo = new LogIdVO();
        vo.setIndexName(indexName);
        vo.setEventTableName(eventTable.getName());
        vo.setIds(Arrays.asList(logIds));
        logs.add(vo);
    }

    /**
     * 通过es日志，补全基础信息
     *
     * @param eventTable
     * @param logs
     * @param doc
     * @param response
     */
    private void completionBaseDataForEs(EventTable eventTable, AlarmEventAttribute doc, List<LogIdVO> logs, SearchResponse response) {
        SearchHits result = response.getHits();
        SearchHit[] hits = result.getHits();

        List<SearchHit> searchHitList = new CopyOnWriteArrayList<>(Arrays.asList(hits));
        logger.debug("AlarmDataHandleService haveLogData searchHitList size={}", searchHitList.size());
        handleAlarmLogsData(eventTable, logs, searchHitList);

        // 补全设备信息，人员信息，文件信息
        for (SearchHit hit : searchHitList) {
            String sourceAsString = hit.getSourceAsString();
            logger.debug("haveLogData get source string:{}", sourceAsString);
            Gson gson = new GsonBuilder().create();
            EventLogDstBean eventLogDstBean = gson.fromJson(sourceAsString, EventLogDstBean.class);
            // 处理文件信息
            alarmDataForLogService.handleFileData(eventLogDstBean, doc);
//            handleLogData(eventLogDstBean, doc);
        }
        searchHitList.clear();
    }

    /**
     * 处理logs信息
     *
     * @param eventTable
     * @param logs
     * @param searchHitList
     */
    private void handleAlarmLogsData(EventTable eventTable, List<LogIdVO> logs, List<SearchHit> searchHitList) {
        // 用索引分组，获取对应的日志ID
        Map<String, List<String>> logsMap = searchHitList.parallelStream().collect(Collectors.toMap(SearchHit::getIndex, ss -> {
            List<String> list = new CopyOnWriteArrayList<String>();
            list.add(ss.getId());
            return list;
        }, (List<String> list1, List<String> list2) -> {
            list1.addAll(list2);
            return list1;
        }));
        // 用索引分组，获取对应的guid
        Map<String, List<String>> logGuidsMap = searchHitList.parallelStream().collect(Collectors.toMap(SearchHit::getIndex, ss -> {
            List<String> list = new CopyOnWriteArrayList<String>();
            list.add(String.valueOf(ss.getSourceAsMap().get("guid")));
            return list;
        }, (List<String> list1, List<String> list2) -> {
            list1.addAll(list2);
            return list1;
        }));

        for (Map.Entry<String, List<String>> itemLog : logsMap.entrySet()) {
            LogIdVO vo = new LogIdVO();
            vo.setIndexName(itemLog.getKey());
            vo.setEventTableGuid(eventTable.getId());
            vo.setEventTableName(eventTable.getName());
            vo.setIds(itemLog.getValue());
            vo.setLogGuids(logGuidsMap.get(itemLog.getKey()));
            logs.add(vo);
        }
    }

    /**
     * 处理日志中信息
     *
     * @param eventLogDstBean
     * @param doc
     */
    @Override
    public void handleLogData(EventLogDstBean eventLogDstBean, AlarmEventAttribute doc) {
        // 处理人员信息
        try {
            alarmDataForLogService.handleStaffInfosData(eventLogDstBean, doc);
        } catch (Exception ex) {
            logger.error("补全人员信息失败");
        }

        // 处理部门信息
        try {
            alarmDataForLogService.handleUnitInfoData(eventLogDstBean, doc);
        } catch (Exception ex) {
            logger.error("补全部门信息失败");
        }

        // 处理设备信息
        try {
            alarmDataForLogService.handleDeviceInfosData(eventLogDstBean, doc);
        } catch (Exception ex) {
            logger.error("补全设备信息失败");
        }

        // 处理应用信息
        try {
            alarmDataForLogService.handleApplicationInfosData(eventLogDstBean, doc);
        } catch (Exception ex) {
            logger.error("补全应用信息失败");
        }

        // 处理扩展信息
        try {
            logger.info("-------------处理补全扩展信息");
            alarmDataForLogService.handleExtention(eventLogDstBean, doc);
        } catch (Exception ex) {
            logger.error("补全应用信息失败");
        }

    }

    /**
     * 补全分类信息
     *
     * @param riskEventRule 告警事件规则对象
     * @param eventCategory 事件分类对象
     * @param doc           告警对象
     */
    @Override
    public void formEventCategory(RiskEventRule riskEventRule, EventCategory eventCategory, AlarmEventAttribute doc) {
        doc.setRuleId(riskEventRule.getId());
        doc.setRuleName(riskEventRule.getName());
        doc.setEventName(riskEventRule.getEventName());
        doc.setEventCode(riskEventRule.getWarmType());
        doc.setCategoryId(eventCategory.getId());

        // 1标准事件（6种事件类型）
        // 2自定义事件
        //事件类型数据补全
        String riskEventCode = eventCategory.getCodeLevel();
        eventTypeMap.forEach((key, value) -> {
            if (riskEventCode.startsWith(key)) {
                doc.setEventType(value);
            }
        });

        //用户行为异常 3
        // if (riskEventCode.startsWith("/safer/AbnormalUserBehavior")) {
        //     doc.setEventType(3);
        // }
        // // 应用异常 5
        // else if (riskEventCode.startsWith("/safer/AbnormalApplicationBehavior")) {
        //     doc.setEventType(5);
        // }
        // //配置合规信息 1
        // else if (riskEventCode.startsWith("/safer/ConfigurationCompliance")) {
        //     doc.setEventType(1);
        // }
        // //互联互通异常  6
        // else if (riskEventCode.startsWith("/safer/ConnectivityAbnormal")) {
        //     doc.setEventType(6);
        // }
        // //网络安全异常 2
        // else if (riskEventCode.startsWith("/safer/NetworkSecurityException")) {
        //     doc.setEventType(2);
        // }
        // //运维行为异常 4
        // else if (riskEventCode.startsWith("/safer/OperationaBehavior")) {
        //     doc.setEventType(4);
        // }

        doc.setEventKind(doc.getEventType() != null && doc.getEventType() <= 6 ? 1 : 2);

        doc.setCategoryId(eventCategory.getId());
        if (riskEventRule != null) {
            doc.setAlarmRiskLevel(Integer.parseInt(riskEventRule.getLevelstatus()));

            String tags = riskEventRule.getKnowledgeTag();
            if (!StringUtils.isEmpty(tags) && (doc.getLabels() == null || doc.getLabels().isEmpty())) {
                List<Label> labels = new CopyOnWriteArrayList<>();
                for (String tag : tags.split(",")) {
                    Label label = new Label(tag, "#000", tag);
                    labels.add(label);
                }
                doc.setLabels(labels);
            }
        }
    }

    /**
     * 补全告警状态信息
     *
     * @param alarmStatus 告警状态
     * @param doc         告警对象
     */
    @Override
    public void formAlarmStatus(int alarmStatus, AlarmEventAttribute doc) {
        switch (alarmStatus) {
            case 0:
                doc.setAlarmDealState(AlarmDealStateEnum.UNTREATED.getCode());
                break;
            case 1:
            case 3:
            case 4:
                // case 5:
                doc.setAlarmDealState(AlarmDealStateEnum.PROCESSING.getCode());
                break;
            case 2:
            case 7:
            case 8:
            case 5:
            case 6:
                doc.setAlarmDealState(AlarmDealStateEnum.PROCESSED.getCode());
                break;
            default:
                doc.setAlarmDealState(AlarmDealStateEnum.UNTREATED.getCode());
                break;
        }
        doc.setIsUrge(doc.getUrgeInfos() != null && !doc.getUrgeInfos().isEmpty());
    }

    /**
     * 补全认证信息
     *
     * @param doc
     */
    @Override
    public void formAuthData(Map<String, List<EventAlarmSetting>> eventAlarmSettingMap, Map<String, List<EventCategory>> eventCategoryMap, AlarmEventAttribute doc) {
        List<EventAlarmSetting> eventAlarmSettings = eventAlarmSettingMap.get(doc.getRuleId());
        if (CollectionUtils.isEmpty(eventAlarmSettings)) {
            eventAlarmSettings = eventAlarmSettingMap.get(doc.getCategoryId());
        }
        EventAlarmSetting one = getEventAlarmSetting(eventAlarmSettingMap, eventCategoryMap, doc, eventAlarmSettings);
        logger.info("eventAlarmSettings={}", eventAlarmSettings);
        if (one != null) {
            List<GuidNameVO> toUser = null;
            if (!StringUtils.isEmpty(one.getToUser())) {
                toUser = gson.fromJson(one.getToUser(), new TypeToken<List<GuidNameVO>>() {
                }.getType());
            }
            List<GuidNameVO> toRole = null;
            if (!StringUtils.isEmpty(one.getToRole())) {
                toRole = gson.fromJson(one.getToRole(), new TypeToken<List<GuidNameVO>>() {
                }.getType());
            }
            List<AlarmEventUrge> urgeInfos = doc.getUrgeInfos();
            if (urgeInfos == null && Boolean.TRUE.equals(one.getIsUrge())) {
                urgeInfos = new ArrayList<>();
                AlarmEventUrge urgeInfo = new AlarmEventUrge();
                urgeInfo.setIsAuto(true);
                //urgeInfo.setInitiator(initiator);
                urgeInfo.setUrgeRemark(one.getUrgeReason());
                urgeInfo.setUrgeTime(new Date());
                if (toUser != null) {
                    urgeInfo.setToUser(toUser);
                }
                if (toRole != null) {
                    urgeInfo.setToRole(toRole);
                }
                urgeInfo.setValidityDate(DateUtil.addMinutes(new Date(), one.getTimeLimitNum() == null ? 0 : one.getTimeLimitNum()));
                urgeInfos.add(urgeInfo);
                doc.setValidityDate(urgeInfo.getValidityDate());
                doc.setUrgeInfos(urgeInfos);

            }
            AuthorizationControl authorization = doc.getAuthorization();
            if (authorization == null) {
                authorization = new AuthorizationControl();
                List<GuidNameVO> canOperateUser = new ArrayList<>();
                if (null != toUser && CollectionUtils.isNotEmpty(toUser)) {
                    toUser.forEach(item -> {
                        canOperateUser.add(item);
                    });
                }
                List<GuidNameVO> canOperateRole = new ArrayList<>();
                if (null != toRole && CollectionUtils.isNotEmpty(toRole)) {
                    toRole.forEach(item -> {
                        canOperateRole.add(item);
                    });
                }
                authorization.setCanOperateRole(canOperateRole);
                authorization.setCanOperateUser(canOperateUser);
                authorization.setOperatorRecord(new ArrayList<>());
                doc.setAuthorization(authorization);
            }
        }
    }

    /**
     * getEventAlarmSetting
     *
     * @param eventAlarmSettingMap
     * @param eventCategoryMap
     * @param doc
     * @param eventAlarmSettings
     * @return com.vrv.vap.alarmdeal.model.EventAlarmSetting
     */
    private EventAlarmSetting getEventAlarmSetting(Map<String, List<EventAlarmSetting>> eventAlarmSettingMap, Map<String, List<EventCategory>> eventCategoryMap, AlarmEventAttribute doc, List<EventAlarmSetting> eventAlarmSettings) {
        EventAlarmSetting one = null;
        if (CollectionUtils.isNotEmpty(eventAlarmSettings)) {
            one = eventAlarmSettings.get(0);
        }
        if (one == null) {
            List<EventCategory> eventCategoryList = eventCategoryMap.get(doc.getCategoryId());
            EventCategory eventCategory = null;
            if (CollectionUtils.isNotEmpty(eventCategoryList)) {
                eventCategory = eventCategoryList.get(0);
            }
            while (eventCategory != null && one == null) {
                List<EventAlarmSetting> eventAlarmSettings1 = eventAlarmSettingMap.get(doc.getCategoryId());
                if (CollectionUtils.isNotEmpty(eventAlarmSettings1)) {
                    List<EventAlarmSetting> eventAlarmSettings2 = eventAlarmSettings1.stream().filter(item -> doc.getRuleId().equals(item.getRuleCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(eventAlarmSettings2)) {
                        one = eventAlarmSettings2.get(0);
                    } else {
                        one = eventAlarmSettings1.get(0);
                    }
                }
                if (one == null) {
                    if (StringUtils.isNotEmpty(eventCategory.getParentId())) {
                        List<EventCategory> eventCategoryList1 = eventCategoryMap.get(eventCategory.getParentId());
                        try {
                            eventCategory = eventCategoryList1.get(0);
                        } catch (Exception ex) {
                            return null;
                        }
                    } else {
                        eventCategory = null;
                    }
                }
            }
        }
        return one;
    }

    /**
     * 上传告警对象
     *
     * @param docs 告警对象
     */
    @Override
    public void pushAlarmData(List<AlarmEventAttribute> docs) {
        if (CollectionUtils.isNotEmpty(docs)) {
            alarmEventManagementForEsService.addList(alarmEventManagementForEsService.getIndexName(), docs);
        }
    }

    /**
     * 上传级联信息
     *
     * @param docs 告警对象
     */
    @Override
    public void pushSuperviseData(List<AlarmEventAttribute> docs) {
        if (CollectionUtils.isNotEmpty(docs)) {
            docs.parallelStream().forEach(item -> {
                try {
                    //1,上报监管事件
                    UpEventDTO eventDTO = new UpEventDTO();
                    eventDTO.setDoc(item);
                    logger.warn("------------上报事件处置-----------------");
                    //2，上报事件处置
                    eventDTO.setDisposeStatus(DisponseConstant.WAIT_DISPONSE);
                    eventDTO.setUpReportBeanName(IUpReportEventService.UpReportDispose_BEAN_NAME);
                    reportCommonService.upReportEvent(eventDTO);
                } catch (Exception ex) {
                    logger.error("AlarmDataSaveJob handleAlarmData pushSuperviseData upToDisposeEventData  error", ex);
                }

            });
            UpEventDTO eventDTO = new UpEventDTO();
            eventDTO.setDocs(docs);
            eventDTO.setUpReportBeanName(IUpReportEventService.UpReportRegular_BEAN_NAME);
            reportCommonService.upReportEvent(eventDTO);


            logger.info("AlarmDataSaveJob handleAlarmData pushSuperviseData success size ={}", docs.size());
        }
    }





}
