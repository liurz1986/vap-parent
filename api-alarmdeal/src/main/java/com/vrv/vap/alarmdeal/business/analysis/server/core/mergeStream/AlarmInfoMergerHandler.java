package com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.analysis.vo.FieldInfoVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.RuleInfoVO;
import com.vrv.vap.alarmdeal.business.asset.contract.OrgRelationQuery;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.alarmdeal.frameworks.util.RedissonSingleUtil;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.AlarmLogDesc;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.jpa.common.ArrayUtil;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.page.QueryCondition;

/**
 *  
 *
 * @author wudi 
 *  E‐mail:wudi@vrvmail.com.cn
 *  @version 创建时间：2018年9月26日 下午5:36:04
 *  类说明     告警信息合并处理器
 */
@Component
@Order(value = 1)
public class AlarmInfoMergerHandler implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(AlarmInfoMergerHandler.class);
    private Map<String, RuleMergeHandler> map = new ConcurrentHashMap<>();
    @Autowired
    private RiskEventRuleService riskEventRuleService;
    @Autowired
    private EventCategoryService eventCategoryService;
    @Autowired
    private AlarmLogDesc alarmLogDesc;
    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private RedisUtil redisUtil;


    private static final Long EXPIRE_TIME = 1000L;    //过期时间


    @Override
    public void run(String... args) throws Exception {
        logger.info("告警信息合并处理器");
        initAlarmInfoHandle();
    }

    /**
     * 初始化告警合并处理器
     */
    private String initAlarmInfoHandle() {
        List<RiskEventRule> list = riskEventRuleService.findAll();
        putRuleToCache(list);

        for (RiskEventRule riskEventRule : list) {
            String ruleCode = riskEventRule.getRuleCode();
            RuleMergeHandler ruleMergeHandler = new RuleMergeHandler();
            ruleMapContructor(riskEventRule, ruleCode, ruleMergeHandler);
        }
        return null;
    }

    public void putRuleToCache(List<RiskEventRule> list) {
        // 过滤启动的规则
        List<RiskEventRule> isStartRules = list.stream().filter(item -> "1".equals(item.getStarted())).collect(Collectors.toList());
        CommomLocalCache.put("isStartRules", isStartRules, 2, TimeUnit.HOURS);
    }

    public RiskEventRule getRuleForCache(String ruleId) {
        List<RiskEventRule> rules = new ArrayList<>();
        if (CommomLocalCache.containsKey("isStartRules")) {
            rules = CommomLocalCache.get("isStartRules");
        } else {
            // 缓存中不存在，查询数据
            List<RiskEventRule> list = riskEventRuleService.findAll();
            if (CollectionUtils.isNotEmpty(list)) {
                // 查询新数据后，更新缓存
                rules = list.stream().filter(item -> "1".equals(item.getStarted())).collect(Collectors.toList());
                CommomLocalCache.put("isStartRules", rules, 2, TimeUnit.HOURS);
            }
        }
        List<RiskEventRule> ruleListIds = rules.stream().filter(item -> ruleId.equals(item.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ruleListIds)) {
            return ruleListIds.get(0);
        }

        return riskEventRuleService.getOne(ruleId);
    }


    public FilterOperator getFilterForCache(String filterCode) {
        List<FilterOperator> filters = new ArrayList<>();
        if (CommomLocalCache.containsKey("isStartFilters")) {
            filters = CommomLocalCache.get("isStartFilters");
        } else {
            // 缓存中不存在，查询数据
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("deleteFlag", true));
            List<FilterOperator> list = filterOperatorService.findAll(conditions);
            if (CollectionUtils.isNotEmpty(list)) {
                // 查询新数据后，更新缓存
                CommomLocalCache.put("isStartFilters", list, 2, TimeUnit.HOURS);
            }
        }
        List<FilterOperator> ruleListIds = filters.stream().filter(item -> filterCode.equals(item.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ruleListIds)) {
            return ruleListIds.get(0);
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("deleteFlag", true));
        conditions.add(QueryCondition.eq("code", filterCode));
        List<FilterOperator> list = filterOperatorService.findAll(conditions);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    public Map<String, RuleMergeHandler> getMap() {
        return map;
    }

    public void setMap(Map<String, RuleMergeHandler> map) {
        this.map = map;
    }


    /**
     * 构成对应的map
     *
     * @param riskEventRule
     * @param ruleCode
     * @param ruleMergeHandler
     */
    public void ruleMapContructor(RiskEventRule riskEventRule, String ruleCode, RuleMergeHandler ruleMergeHandler) {
        RuleInfoVO ruleInfo = getRuleInfo(ruleCode);
        ruleMergeHandler.setRuleInfoVO(ruleInfo);
        if ("1".equals(riskEventRule.getStarted())) {
            String field_info = riskEventRule.getField_info();
            FieldInfoVO fieldInfoVO = JsonMapper.fromJsonString(field_info, FieldInfoVO.class);
            Boolean isStart = fieldInfoVO.getIsStart();
            if (isStart != null && isStart) {
                long timeSpan = fieldInfoVO.getTimeSpan();
                Date nextDate = DateUtil.addMillSeconds(new Date(), timeSpan * 1000); //采用的是毫秒技术
                ruleMergeHandler.setNextTime(nextDate.getTime());
                ruleMergeHandler.setFieldInfoVO(fieldInfoVO);
            }
            map.put(ruleCode, ruleMergeHandler);
        }
    }


    /**
     * 填充告警规则信息
     *
     * @param ruleCode
     */
    private RuleInfoVO getRuleInfo(String ruleCode) {
        RuleInfoVO ruleInfoVO = new RuleInfoVO();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("ruleCode", ruleCode)); // ruleCode是唯一的
        conditions.add(QueryCondition.eq("deleteFlag", true)); // ruleCode是唯一的
        List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(conditions);
        if (riskEventRuleList.size() == 1) {
            RiskEventRule riskEventRule = riskEventRuleList.get(0);
            String analysisId = riskEventRule.getAnalysisId();
            String riskEventId = riskEventRule.getRiskEventId();
            String dealAdvcie = riskEventRule.getDealAdvcie();
            String tableName = riskEventRule.getTableName();
            setRuleInfoVOByRiskEventRule(ruleInfoVO, riskEventRule);
            setRuleInfoVOByEventCategory(ruleInfoVO, riskEventId, dealAdvcie);
            setMuitlVersionStr(analysisId, ruleInfoVO);
            setRuleInfoVOByOthers(ruleInfoVO, analysisId, tableName);
        }
        return ruleInfoVO;
    }

    /**
     * 设置对应的告警规则信息
     *
     * @param ruleInfoVO
     * @param riskEventRule
     */
    private void setRuleInfoVOByRiskEventRule(RuleInfoVO ruleInfoVO, RiskEventRule riskEventRule) {
        ruleInfoVO.setRuleId(riskEventRule.getId());
        ruleInfoVO.setRuleName(riskEventRule.getName());
        ruleInfoVO.setIsStart("1".equals(riskEventRule.getStarted()) ? true : false);
        ruleInfoVO.setWeight(riskEventRule.getLevelstatus());
        ruleInfoVO.setTableLabel(riskEventRule.getTableName());
        ruleInfoVO.setInitStatus(riskEventRule.getInitStatus());
        ruleInfoVO.setTag(riskEventRule.getKnowledgeTag());
        ruleInfoVO.setFailedStatus(riskEventRule.getFailedStatus());
        ruleInfoVO.setAttackLine(riskEventRule.getAttackLine());
        ruleInfoVO.setThreatCredibility(riskEventRule.getThreatCredibility());
        ruleInfoVO.setDealAdvice(riskEventRule.getDealAdvcie());
        ruleInfoVO.setExtend2(riskEventRule.getExtend2());
    }


    /**
     * 设置对应的版本
     *
     * @param analysisId
     * @param ruleInfoVO
     */
    private void setMuitlVersionStr(String analysisId, RuleInfoVO ruleInfoVO) {
        List<FilterOperator> filterOperators = filterOperatorService.getFilterOperators(analysisId);
        if (filterOperators.size() == 1) {
            FilterOperator filterOperator = filterOperators.get(0);
            String multiVersion = filterOperator.getMultiVersion();
            ruleInfoVO.setMuitlVersionStr(multiVersion);
        }
    }


    /**
     * 设置规则时间分类相关信息
     *
     * @param ruleInfoVO
     * @param riskEventId
     * @param dealAdvcie
     */
    private void setRuleInfoVOByEventCategory(RuleInfoVO ruleInfoVO, String riskEventId, String dealAdvcie) {
        EventCategory eventCategory = eventCategoryService.getOne(riskEventId);
        if (eventCategory != null) {
            String title = eventCategory.getTitle();
            String codeLevel = eventCategory.getCodeLevel();
            String eventCategoryId = eventCategory.getId();
            String attackFlag = eventCategory.getAttackFlag();
            ruleInfoVO.setRiskEventName(title);
            ruleInfoVO.setCodeLevel(codeLevel);
            ruleInfoVO.setRiskEventId(eventCategoryId);
            ruleInfoVO.setAttackFlag(attackFlag);
            ruleInfoVO.setHarm(eventCategory.getHarm());
            if (StringUtils.isEmpty(dealAdvcie)) {
                ruleInfoVO.setDealAdvice(eventCategory.getDealAdvice());
            }
            ruleInfoVO.setPrinciple(eventCategory.getPrinciple());
        }
    }


    /**
     * 设置规则其他相关信息
     *
     * @param ruleInfoVO
     * @param analysisId
     * @param tableName
     */
    private void setRuleInfoVOByOthers(RuleInfoVO ruleInfoVO, String analysisId, String tableName) {
        if (StringUtils.isNotEmpty(analysisId)) {
            List<String> dataSources = getDataSourceByFilter(analysisId);
            dataSources = dataSources.stream().distinct().collect(Collectors.toList());
            String[] dataSourceArray = dataSources.toArray(new String[dataSources.size()]);
            String join = ArrayUtil.join(dataSourceArray, ",");
            ruleInfoVO.setDataSource(join);
        } else {
            EventTable eventTable = eventTabelService.getEventTableByName(tableName);
            if (eventTable != null) {
                //关联eventtable 威胁数据来源
                ruleInfoVO.setDataSource(eventTable.getDataSource());
            }
        }
    }


    /**
     * 通过过滤器和分析器获得对应的资源（递归获得）
     *
     * @param analysisId
     * @return
     */
    private List<String> getDataSourceByFilter(String analysisId) {
        List<String> dataSources = new ArrayList<>();
        List<FilterOperator> analysisiors = filterOperatorService.getFilterOperators(analysisId);
        if (analysisiors.size() == 1) {
            Gson gson = new Gson();
            FilterOperator filterOperator = analysisiors.get(0);
            String sourceIds = filterOperator.getSourceIds();
            List<String> sourceIdList = gson.fromJson(sourceIds, new TypeToken<List<String>>() {
            }.getType());
            for (String sourceId : sourceIdList) {
                EventTable eventTable = eventTabelService.getOne(sourceId);
                if (eventTable != null) {
                    String dataSource = eventTable.getDataSource();
                    if (StringUtils.isNotEmpty(dataSource)) {
                        dataSources.add(dataSource);
                    }
                } else { //使用过滤器完成
                    dataSources.addAll(getDataSourceByFilter(sourceId));
                }
            }
        }


        return dataSources;
    }


    /**
     * 设置告警单位信息
     *
     * @param warnResultLogTmpVO
     */
    private void setParenOrgName(WarnResultLogTmpVO warnResultLogTmpVO) {
        Map<String, Object> extendParams = warnResultLogTmpVO.getExtendParams();
        if (extendParams!=null){
            extendParams = setRelateField(extendParams, "std_org_code");
            extendParams = setRelateField(extendParams, "src_std_org_code");
            extendParams = setRelateField(extendParams, "dst_std_org_code");
            Gson gson  = new Gson();
            logger.info("extendParams Value:{}", gson.toJson(extendParams));
            warnResultLogTmpVO.setExtendParams(extendParams);
        }
    }

    /**
     * 替换std_org_code，src_std_org_code， dst_std_org_code三个字段值的方式
     *
     * @param extendParams
     * @param keyWord
     */
    private Map<String, Object> setRelateField(Map<String, Object> extendParams, String keyWord) {
        if (extendParams.containsKey(keyWord)) {
            String paramValue = extendParams.get(keyWord).toString();
            if (!paramValue.equals("未知单位") && !paramValue.equals("")) {
                boolean result = redisUtil.hasKey(paramValue);
                if (result) {  //TODO 存在
                    String orgName = redisUtil.get(paramValue).toString();
                    extendParams.put(keyWord, orgName);
                } else {
                    //TODO 不存在进行替换操作
                    OrgRelationQuery orgRelationQuery = new OrgRelationQuery();
                    orgRelationQuery.setCode(paramValue);
                    VData<BaseKoalOrg> vData = new VData<>();
                    try {
                        vData = adminFeign.queryOrganizationRelation(orgRelationQuery);
                    } catch (Exception e) {
                        logger.error("查询单位信息异常：{}", e);
                        vData.setData(null);
                    }

                    if (vData.getData()==null) {
                        extendParams.put(keyWord, "未知单位");
                        redisUtil.set(paramValue, "未知单位", EXPIRE_TIME + new Random().nextInt(30));
                    } else {
                        String orgName = vData.getData().getName();
                        extendParams.put(keyWord, orgName);
                        redisUtil.set(paramValue, orgName, EXPIRE_TIME + new Random().nextInt(30));
                    }

                }
            }
        }
        return extendParams;
    }


    /**
     * 进行对应的合并数据填充处理
     *
     * @param warnResultLogVO
     */
    public WarnResultLogTmpVO handler(WarnResultLogTmpVO warnResultLogVO) {

        String ruleCode = warnResultLogVO.getRuleCode();
        ruleCode = ruleCode.split("-")[0];
        RuleMergeHandler mergeHandler = map.get(ruleCode);
        logger.debug("mergeHandler of value：" + ruleCode);
        if (mergeHandler != null) {
            mergeHandler.handle(warnResultLogVO);
            setParenOrgName(warnResultLogVO);
            setAlarmDesc(warnResultLogVO);
            return warnResultLogVO;
        } else {
            return null;
        }
    }

    private void setAlarmDesc(WarnResultLogTmpVO warnResultLogVO) {
        try {
            String principle = getReplaceFinalPrinciple(warnResultLogVO);
            logger.info("principle:{}", principle);
            String desc = getReplaceFinalRiskRuleDesc(warnResultLogVO);
            warnResultLogVO.setAlamDesc(desc);
            warnResultLogVO.setPrinciple(principle);
        } catch (Exception e) {
            logger.error("告警描述替换失败", e);
            warnResultLogVO.setAlamDesc("");
            warnResultLogVO.setPrinciple("");
        }
    }

    /**
     * 规则和合并器
     */
    public RuleMergeHandler chooseRuleMergeHandler(String ruleCode) {
        return map.get(ruleCode);
    }

    /**
     * 获得告警描述
     *
     * @param warnResultLogVO
     * @return
     */
    private RiskEventRule getRiskRule(WarnResultLogTmpVO warnResultLogVO) {
        String riskEventId = warnResultLogVO.getRuleId();
        if (StringUtils.isEmpty(riskEventId)) {
            return null;
        }
        RiskEventRule riskEventRule = getRuleForCache(riskEventId);
        return riskEventRule;
    }

    private FilterOperator getFilterOperator(WarnResultLogTmpVO warnResultLogVO) {
        String filterCode = warnResultLogVO.getRuleCode().split("-")[1];
        if (StringUtils.isEmpty(filterCode)) {
            return null;
        }
        FilterOperator filterOperator = getFilterForCache(filterCode);
        return filterOperator;
    }


    /**
     * 把原理当中的占位符替换掉
     *
     * @param warnResultLogTmpVO
     * @return
     */
    private String getReplaceFinalPrinciple(WarnResultLogTmpVO warnResultLogTmpVO) {
        String ruleCode = warnResultLogTmpVO.getRuleCode();
        String principle = warnResultLogTmpVO.getPrinciple();
        if (ruleCode.split("-").length > 1) {
            FilterOperator filterOperator = getFilterOperator(warnResultLogTmpVO);
            if (filterOperator != null) {
                principle = filterOperator.getPrinciple();
                if (StringUtils.isNotEmpty(principle)) {
                    principle = replaceHolder(warnResultLogTmpVO, principle);
                }
            }
        }
        return principle;
    }

    /**
     * 获得替换以后
     *
     * @param warnResultLogVO
     * @return
     */
    private String getReplaceFinalRiskRuleDesc(WarnResultLogTmpVO warnResultLogVO) {
        String ruleCode = warnResultLogVO.getRuleCode();
        String desc = warnResultLogVO.getAlamDesc();
        if (ruleCode.split("-").length > 1) {
            FilterOperator filterOperator = getFilterOperator(warnResultLogVO);
            if (filterOperator != null) {
                desc = filterOperator.getFilterDesc();
                if (StringUtils.isNotEmpty(desc)) {
                    desc = replaceHolder(warnResultLogVO, desc);
                }


            }
        } else {
            RiskEventRule riskRule = getRiskRule(warnResultLogVO);
            if (riskRule != null && StringUtils.isNotBlank(riskRule.getDesc())) {
                desc = riskRule.getDesc();
                if (StringUtils.isNotEmpty(desc)) {
                    desc = replaceHolder(warnResultLogVO, desc);
                }
            }
        }


        return desc;
    }

    /**
     * 占位符替换
     */
    public String replaceHolder(WarnResultLogTmpVO warnResultLogVO, String desc) {
        Map<String, Object> resultMap = mapperUtil.map(warnResultLogVO, Map.class);
        String logsInfo = warnResultLogVO.getLogsInfo();
        Gson gson = new Gson();
        if (StringUtils.isNotEmpty(logsInfo)) {
            List<Map<String, Object>> list = gson.fromJson(logsInfo, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            if (list != null && list.size() > 0) {
                Map<String, Object> map2 = list.get(0);
                if (map2 != null) {
                    resultMap.putAll(map2);
                }
            }
        }
        resultMap.putAll(warnResultLogVO.getFiveElementsMap());
        if (desc.contains("args")) {
            RiskEventRule riskRule = getRiskRule(warnResultLogVO);
            if (riskRule != null) {
                String ruleFieldJson = riskRule.getRule_field_json();
                Map<String, Object> argMap = gson.fromJson(ruleFieldJson, Map.class);
                resultMap.putAll(argMap);
            }
        }
        Map<String, Object> extendMap = warnResultLogVO.getExtendParams(); //增加额外属性
        if (extendMap != null) {
            resultMap.putAll(extendMap);
        }
        String json = gson.toJson(resultMap);
        desc = alarmLogDesc.createAlarmDesc(desc, json);
        return desc;
    }


    /**
     * 清理合并时间窗的缓存
     */
    public void clearCache() {
        for (Map.Entry<String, RuleMergeHandler> entry : map.entrySet()) {
            RuleMergeHandler ruleMergeHandler = entry.getValue();
            boolean result = ruleMergeHandler.clearCache();
            if (result) {
                String key = entry.getKey();
                resetCacheMap(key, ruleMergeHandler);
            }
        }
    }


    /**
     * 重置CacheMap
     *
     * @param key
     * @param ruleMergeHandler
     */
    private void resetCacheMap(String key, RuleMergeHandler ruleMergeHandler) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("ruleCode", key));
        List<RiskEventRule> list = riskEventRuleService.findAll(conditions);
        if (list.size() == 1) {
            RiskEventRule riskEventRule = list.get(0);
            ruleMapContructor(riskEventRule, key, ruleMergeHandler);
        }
    }

    /**
     * 判断告警信息是否是有合并
     * 根据合并条件进行合并（根据状态合并和时间合并进行合并）
     */
    public Boolean isRepeated(WarnResultLogTmpVO warnResultLogTmpVO) {
        Boolean bool = true;
        Integer repeat = warnResultLogTmpVO.getRepeatCount();
        if (repeat == null || repeat == 0 || repeat == 1) {
            bool = false;
        }
        return bool;
    }

    public String getEventId(String ruleCode) {
        RuleMergeHandler mergeHandler = map.get(ruleCode);
        if (mergeHandler != null) {
            return mergeHandler.getRuleInfoVO().getRiskEventId();
        } else {
            return "unFound";
        }

    }
}
