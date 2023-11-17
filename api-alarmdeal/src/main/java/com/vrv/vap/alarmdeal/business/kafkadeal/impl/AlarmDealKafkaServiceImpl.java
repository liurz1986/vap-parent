package com.vrv.vap.alarmdeal.business.kafkadeal.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.DimensionSync;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.SyncRequest;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.DimensionSyncService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.FilterSourceStatus;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.FilterSourceStatusInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.SourceStatusKafkaInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.FilterSourceStatusService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl.WarnResultCreateService;
import com.vrv.vap.alarmdeal.business.analysis.vo.FilterOperatorGroupStartVO;
import com.vrv.vap.alarmdeal.business.kafkadeal.AlarmDealKafkaService;
import com.vrv.vap.alarmdeal.frameworks.util.RedissonSingleUtil;
import com.vrv.vap.es.service.ElasticSearchMapManage;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 梁国露
 * @since: 2023/3/31 15:24
 * @description:
 */
@Service
public class AlarmDealKafkaServiceImpl implements AlarmDealKafkaService {
    private static Logger logger = LoggerFactory.getLogger(AlarmDealKafkaServiceImpl.class);
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    @Autowired
    private FilterSourceStatusService filterSourceStatusService;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RuleFilterService ruleFilterService;

    @Autowired
    private RiskEventRuleService riskEventRuleService;

    @Autowired
    private RedissonSingleUtil redissonSingleUtil;

    @Autowired
    private DimensionSyncService dimensionSyncService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DimensionTableService dimensionTableService;

    @Autowired
    private WarnResultCreateService warnResultCreateService;
    @Autowired
    private ElasticSearchMapManage elasticSearchMapManage;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 数据源变更监听
     *
     * @param message
     */
    @Override
    public void sourceChange(String message) {
        SourceStatusKafkaInfo sourceStatusKafkaInfo = gson.fromJson(message, SourceStatusKafkaInfo.class);
        List<FilterSourceStatusInfo> filterSourceStatusInfos = sourceStatusKafkaInfo.getData();
        if (CollectionUtils.isNotEmpty(filterSourceStatusInfos)) {
            handleFilterStatus(filterSourceStatusInfos);
        }
    }

    /**
     * 基础数据变更监听
     *
     * @param message
     */
    @Override
    public void baseDataChannel(String message) {
        Map<String, String> msg = JSONObject.parseObject(String.valueOf(message), Map.class);
        String baseLine = msg.get("item");
        /*******************查询基础数据名称***********************/
        String baseName = getBaseDataTableName(baseLine);
        if (StringUtils.isBlank(baseName)) {
            logger.debug("{}，不存在该索引对应维表！", baseLine);
        }

        /*******************清除缓存************************/
        clearRedis(baseName);
        logger.debug("{}，索引删除成功！", baseName);
    }

    /**
     * 告警数据监听
     *
     * @param message
     */
    @Override
    public void comsumerFlinkAlarmData(String message) {
        WarnResultLogTmpVO warnResultLogVO = gson.fromJson(message, WarnResultLogTmpVO.class);
        warnResultCreateService.constructAlarmInfoData(warnResultLogVO);
    }

    @Override
    public void comsumerEventTypeData(String message) {
        logger.info("对象事件保存到es");
        Map<String, String> msg = gson.fromJson(String.valueOf(message), Map.class);
        String indexName = msg.get("indexName");
        String content = msg.get("content").toString();
        if (StringUtils.isNotBlank(indexName) && StringUtils.isNotBlank(content)) {
            String format = DateUtil.format(new Date(), "yyyy.MM");
            indexName = indexName + "-" + format;
            //解析content
            Map<String, Object> map = gson.fromJson(content, Map.class);
            //创建索引
//            Boolean index = elasticSearchMapManage.createIndex(indexName, map);
            //保存数据
            elasticSearchMapManage.save(indexName, map, map.get("resultGuid"));
        }
    }

    private Map<String, Object> analysisContent(String content) {
        Map<String, String> msg = JSONObject.parseObject(String.valueOf(content), Map.class);
        return null;
    }


    /**
     * 查询变动的基础数据是否存在的维表
     *
     * @param name
     * @return
     */
    public String getBaseDataTableName(String name) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("nameEn", name));
        conditions.add(QueryCondition.eq("tableType", "base"));
        List<DimensionTableInfo> dimensionTableInfos = dimensionTableService.findAll(conditions);
        if (CollectionUtils.isEmpty(dimensionTableInfos)) {
            return null;
        }
        return dimensionTableInfos.get(0).getNameEn();
    }

    /**
     * 清除redis缓存
     *
     * @param key
     */
    public void clearRedis(String key) {
        redissonSingleUtil.deleteByPrex(key + "*");
    }


    /**
     * 处理消息数据
     *
     * @param filterSourceStatusInfos
     */
    private void handleFilterStatus(List<FilterSourceStatusInfo> filterSourceStatusInfos) {
        for (FilterSourceStatusInfo sourceStatus : filterSourceStatusInfos) {
            if (StringUtils.isBlank(sourceStatus.getDataTopicName())) {
                continue;
            }
            if (sourceStatus.getDataTopicName().startsWith("base") && sourceStatus.getData_status().equals(1)) {
                String dataTopicName = sourceStatus.getDataTopicName();
                //TODO 临时处理
                if (dataTopicName.equals("base-line-protocol-10") || dataTopicName.equals("base-line-dip-10") || dataTopicName.equals("base-line-dport-10")
                        || dataTopicName.equals("base_line_src_total_bytes") || dataTopicName.equals("base_line_dst_total_bytes")) {
                    String index = sourceStatus.getIndex();
                    redissonSingleUtil.deleteByPrex(index);
                    logger.warn("{}，索引删除成功！", index);
                    return;
                }

                if (StringUtils.isNotBlank(sourceStatus.getInsertTime())) {
                    // 1、同步基线数据
                    String tableName = filterSourceStatusService.filterChange(sourceStatus.getIndex(), sourceStatus.getInsertTime());
                    logger.info("基线同步表表名：{}", tableName);
                    if (StringUtils.isBlank(tableName)) {
                        logger.error("{}，同步数据失败！", sourceStatus.getDataTopicName());
                    } else {
                        redissonSingleUtil.deleteByPrex(tableName);
                        logger.warn("{}，索引删除成功！", tableName);
                    }
                }
            }

            // 3、启动规则
            startRule(sourceStatus);
        }
    }


    /**
     * 启动规则
     *
     * @param sourceStatus
     * @return
     */
    public void startRule(FilterSourceStatusInfo sourceStatus) {
        // 处理数据
        Integer sourceId = sourceStatus.getDataSourceId();
        Integer dataStatus = getFilterSourceStatus(sourceId);
        if (sourceStatus.getData_status() == 0 && dataStatus == 0) {
            logger.warn("数据源[{}]，数据状态为不满足,且表中没有数据或者之前状态为不满足！", sourceId);
            return;
        } else if (sourceStatus.getData_status() == 0 && dataStatus == 1) {
            // 现在数据状态为不满足，之前数据状态为满足，需要停用规则
            logger.warn("数据源[{}]，现在数据状态为不满足，之前数据状态为满足，需要停用规则!", sourceId);
            List<String> ruleIds = getRuleBySourceId(sourceId);
            logger.info("sourceId:{},ruleIds:{}", sourceId, gson.toJson(ruleIds));
            if (CollectionUtils.isEmpty(ruleIds)) {
                logger.warn("数据源[{}]，不存在启动状态的相关规则！", sourceId);
                return;
            }
            FilterOperatorGroupStartVO vo = new FilterOperatorGroupStartVO();
            vo.setGuids(String.join(",", ruleIds));
            vo.setSourceId(sourceId);
            filterOperatorService.stopFlinkJobByEventId(vo);
            filterSourceStatusService.saveFilterSourceStatus(sourceStatus);
        } else if (sourceStatus.getData_status() == 1 && dataStatus == 1) {
            logger.warn("数据源[{}]，现在数据状态为满足状态，之前数据状态也为满足状态，则不处理!", sourceId);
            return;
        } else if (sourceStatus.getData_status() == 1 && dataStatus == 0) {
            //现在数据状态为满足状态，之前数据状态为不满足，启动规则
            logger.warn("数据源[{}]，现在数据状态为满足状态，之前数据状态为不满足，启动规则!", sourceId);
            List<String> ruleIds = getRuleBySourceId(sourceId);   //涉及的数据源
            DimensionTableInfo dimensionTableByIndex = dimensionTableService.getDimensionTableByIndex(sourceStatus.getIndex());
            if (CollectionUtils.isEmpty(ruleIds) && dimensionTableByIndex == null) {
                logger.warn("请注意：数据源[{}]，不存在启动状态的相关规则，且该数据源没有作为维表", sourceId);
                return;
            }
            //设置维表
            if (dimensionTableByIndex != null) {
                String nameEn = dimensionTableByIndex.getNameEn();   //维表名称
                List<FilterOperator> results = filterOperatorService.findFilterOperatotByElementName(nameEn);
                //1.判断这些规则的数据源是否是正常启动的状态
                //2，如果是source正常启动状态，找到规则关联到的策略的id然后开始启动操作
                List<String> filterOperatorCodes = getFilterOperatorCodes(results);
                List<String> dimensionRuIds = getStartedRuleIdsByFilterIds(filterOperatorCodes);
                if (CollectionUtils.isEmpty(dimensionRuIds) && CollectionUtils.isEmpty(ruleIds)) {
                    logger.warn("数据源[{}]，不存在启动状态的相关规则，也不存在使用该数据源作为维表相关启动状态的规则，请检查", sourceId);
                    return;
                }
                ruleIds.addAll(dimensionRuIds);
            }
            checkAndStartRelateRule(ruleIds);
            filterSourceStatusService.saveFilterSourceStatus(sourceStatus);
        }
        return;
    }

    /**
     *
     * @param ruleIds
     */
    private void checkAndStartRelateRule(List<String> ruleIds) {
        ruleIds = ruleIds.stream().distinct().collect(Collectors.toList());
        // 判断维表是否有数据
        FilterOperatorGroupStartVO filterOperatorGroupStartVO = new FilterOperatorGroupStartVO();
        filterOperatorGroupStartVO.setGuids(String.join(",", ruleIds));
        List<String> ruleIdList = filterOperatorService.startFilterCheckFilterBaseLineData(filterOperatorGroupStartVO);
        String msg = filterOperatorService.checkRuleFilterBaseLineData(ruleIdList);

        if (StringUtils.isNotBlank(msg)) {
            return;
        }
        List<String> ruleList = new ArrayList<>(Arrays.asList(filterOperatorGroupStartVO.getGuids().split(",")));
        List<String> cj = getNeedStartRule(ruleList);
        if(cj.size()>0){
            filterOperatorService.startFilterOperatorGroupByName(cj, ruleIdList);
        }
    }

    /**
     * 获得需要启动的策略
     * @param ruleList
     * @return
     */
    private List<String> getNeedStartRule(List<String> ruleList) {
        List<String> notStartRule = new ArrayList<>();  //不需要启动的规则
        Map<Object, Object> hmget = redisUtil.hmget(FilterOperatorService.FLINK_TASK_REDIS_KEY);
        if (hmget != null && hmget.size() > 0) {
            for (String ruleId : ruleList) {
                for (Map.Entry<Object, Object> entry : hmget.entrySet()) {
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    if (value.contains(ruleId)) {
                        logger.warn("规则[{}——{}]已经启动，不需要再次启动！",key,ruleId);
                        notStartRule.add(ruleId);
                        break;
                    }
                }
            }
        }
        List<String> cj = ruleList.stream().filter(item -> !notStartRule.contains(item)).collect(Collectors.toList());
        return cj;
    }


    /**
     * 根据规则code获得启动状态的策略
     *
     * @param filterOperatorCodes
     * @return
     */
    private List<String> getStartedRuleIdsByFilterIds(List<String> filterOperatorCodes) {
        List<String> result = new ArrayList<>();
        List<QueryCondition> ruleConditions = new ArrayList<>();
        ruleConditions.add(QueryCondition.in("filterCode", filterOperatorCodes));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleConditions);
        if (CollectionUtils.isEmpty(ruleFilters)) {
            return result;
        }
        List<String> ruleIds = ruleFilters.stream().map(RuleFilter::getRuleId).collect(Collectors.toList());
        List<QueryCondition> riskConditions = new ArrayList<>();
        riskConditions.add(QueryCondition.in("id", ruleIds));
        riskConditions.add(QueryCondition.eq("deleteFlag", true));
        riskConditions.add(QueryCondition.eq("started", "1"));
        List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(riskConditions);
        if (CollectionUtils.isEmpty(riskEventRules)) {
            return result;
        }
        List<String> riskIds = riskEventRules.stream().map(RiskEventRule::getId).collect(Collectors.toList());
        return riskIds;
    }


    /**
     * 筛选数据源状态是正常状态的规则
     *
     * @param results
     */
    private List<String> getFilterOperatorCodes(List<FilterOperator> results) {
        List<String> fitlerOperatorIds = new ArrayList<>();
        results.forEach(result -> {
            boolean q = false;
            String content = convertSourceString(result);
            String[] sourceArr = content.split(",");
            for (String source : sourceArr) {
                q = filterSourceStatusService.getFilterSourceStatusByRedis(source);
                if (!q) {
                    break;
                }
            }
            if (q) {
                fitlerOperatorIds.add(result.getCode());
            }
        });
        return fitlerOperatorIds;
    }

    /**
     * 转换source资源
     * ["33",""34] ->33,34
     *
     * @param result
     * @return
     */
    private static String convertSourceString(FilterOperator result) {
        String sourceIds = result.getSourceIds();
        sourceIds = sourceIds.replace("[", "").replace("]", "").replace("\"", "");
        int[] arr = Arrays.stream(sourceIds.split(",")).mapToInt(Integer::parseInt).toArray();
        String content = Arrays.toString(arr);
        int begin = content.indexOf("[");
        int end = content.indexOf("]");
        content = content.substring(begin + 1, end);
        return content;
    }


    /**
     * 通过数据源ID 查询策略
     *
     * @param sourceId
     * @return
     */
    public List<String> getRuleBySourceId(int sourceId) {
        List<String> result = new ArrayList<>();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.like("sourceIds", "[\"" + sourceId + "\"]"));
        conditions.add(QueryCondition.eq("deleteFlag", true));
        List<FilterOperator> filterOperators = filterOperatorService.findAll(conditions);
        if (CollectionUtils.isNotEmpty(filterOperators)) {
            List<String> filterCodes = filterOperators.stream().map(FilterOperator::getCode).collect(Collectors.toList());
            result = getStartedRuleIdsByFilterIds(filterCodes);
        }
        return result;
    }

    /**
     * 通过数据源ID 查询上一次数据状态
     *
     * @param sourceId
     * @return
     */
    public Integer getFilterSourceStatus(int sourceId) {
        int status = 0;
        List<QueryCondition> condition = new ArrayList<>();
        condition.add(QueryCondition.eq("dataSourceId", sourceId));
        List<FilterSourceStatus> sourceStatuses = filterSourceStatusService.findAll(condition);
        if (CollectionUtils.isNotEmpty(sourceStatuses)) {
            FilterSourceStatus filterSourceStatus = sourceStatuses.get(0);
            return filterSourceStatus.getDataStatus();
        }
        return status;
    }
}
