package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.NameValueBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ParamsColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.ExecutorConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealDictEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmNotice;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.FieldConditionBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.SyncRequest;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.res.SyncRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.RiskEventRuleRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ValueText;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventLogFieldVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventLogTable;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilterVo;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Exchanges;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Tables;
import com.vrv.vap.alarmdeal.business.analysis.server.*;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.AlarmInfoMergerHandler;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.RuleMergeHandler;
import com.vrv.vap.alarmdeal.business.analysis.vo.*;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterOpertorVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.StartConfigVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.config.FlinkConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.FeignCache;
import com.vrv.vap.alarmdeal.frameworks.util.RedissonSingleUtil;
import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.common.model.User;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.*;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 告警规则业务类
 *
 * @author wd-pc
 */
@Service
public class RiskEventRuleServiceImpl extends BaseServiceImpl<RiskEventRule, String> implements RiskEventRuleService {

    public static final String THRAT_ANALYSIS_JOB_NAME = "asset-risk-job";

    public static final String THRAT_ANALYSIS_MAIN_CLASS = "com.vrv.rule.ruleInfo.assetRisk.FlinkAssetAnalysisFunctionTest";

    private static Logger logger = LoggerFactory.getLogger(RiskEventRuleServiceImpl.class);

    // 无效
    public static final String NOT_VALIDATION = "0";

    // 有效
    public static final String VALIDATION = "1";

    public static final String CASCADE_STATE = "local";

    public static final String LOGVO_TYPE = "logVOType";

    public static final String JDBC_TYPE = "jdbcType";

    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    // 目前这个功能没有了，把密码写入代码中 20230613
    private String exportPasswd = "12345678";

    @Autowired
    private RiskEventRuleRespository riskEventRuleRespository;

    @Autowired
    private EventCategoryService eventCategoryService;

    @Autowired
    private MapperUtil mapper;

    @Autowired
    private FlinkConfiguration flinkConfiguration;

    @Autowired
    private AlarmInfoMergerHandler alarmInfoMergerHandler;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private EventColumService eventColumService;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private FlinkErrorLogService flinkErrorLogService;

    @Autowired
    EventAlarmSettingService eventAlarmSettingService;

    @Autowired
    private RuleFilterService ruleFilterService;

    @Autowired
    private AlarmAnalysisService alarmAnalysisService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

//    @Autowired
//    private RedisUtil redisUtil;

    @Autowired
    private RedissonSingleUtil redissonSingleUtil;

    @Autowired
    private DimensionSyncService dimensionSyncService;

    @Autowired
    private DimensionTableFieldService dimensionTableFieldService;

    @Autowired
    private DimensionTableService dimensionTableService;

    @Autowired
    private FeignCache feignCache;
    @Autowired
    private CommonFilterOperatorService commonFilterOperatorService;

    //创建线程池
    private Executor cacheThreadPool = ExecutorConfig.riskEventRuleExecutor();

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    public RiskEventRuleRespository getRepository() {
        return riskEventRuleRespository;
    }

    @Override
    public PageRes<RiskRuleListVO> getRiskEventRulePager(RiskEventRuleQueryVO riskEventRuleVO, Pageable pageable) {
        PageRes<RiskRuleListVO> result = new PageRes();
        List<QueryCondition> conditions = getRiskEventQueryConditions(riskEventRuleVO);
        User currentUser = SessionUtil.getCurrentUser();

        if (currentUser != null) {
            List<String> roleCode = currentUser.getRoleCode();
            if (roleCode != null && !roleCode.contains("admin") && !roleCode.contains("secretMgr")) {
                List<QueryCondition> cons = new ArrayList<>();
                List<QueryCondition> ors = new ArrayList<>();
                for (String code : roleCode) {
                    ors.add(QueryCondition.like("toRole", "\"" + code + "\""));
                }
                ors.add(QueryCondition.like("toUser", ":" + currentUser.getId() + ","));

                cons.add(QueryCondition.or(ors));
                List<EventAlarmSetting> findAll = eventAlarmSettingService.findAll(cons);
                List<String> ids = findAll.stream().filter(item->StringUtils.isNotBlank(item.getRuleCode())).map(EventAlarmSetting::getRuleCode).collect(Collectors.toList());
                findAll = findAll.stream().filter(item->item.getGuid().equals(item.getLinkGuid())).collect(Collectors.toList());
                List<String> guids = new ArrayList<>();

                for (EventAlarmSetting setting : findAll) {
                    if ("category".equals(setting.getLinkType())) {
                        guids.add(setting.getLinkGuid());
                    }
                }

                if (CollectionUtils.isNotEmpty(guids) && CollectionUtils.isNotEmpty(ids)) {
                    conditions.add(QueryCondition.or(QueryCondition.in("riskEventId", guids), QueryCondition.in("id", ids), QueryCondition.eq("createUserno", currentUser.getId())));
                } else if (CollectionUtils.isNotEmpty(guids) && CollectionUtils.isEmpty(ids)) {
                    conditions.add(QueryCondition.or(QueryCondition.in("riskEventId", guids), QueryCondition.eq("createUserno", currentUser.getId())));
                } else if (CollectionUtils.isEmpty(guids) && CollectionUtils.isNotEmpty(ids)) {
                    conditions.add(QueryCondition.or(QueryCondition.in("riskEventId", guids), QueryCondition.eq("createUserno", currentUser.getId())));
                } else {
                    conditions.add(QueryCondition.or(QueryCondition.eq("id", ""), QueryCondition.eq("createUserno", currentUser.getId())));
                }
            }
        }
        Page<RiskEventRule> pager = findAll(conditions,pageable);
        List<RiskEventRule> list = pager.getContent();

        List<RiskRuleListVO> mapList = this.mapper.mapList(list, RiskRuleListVO.class);
        for (RiskRuleListVO riskRuleListVO : mapList) {
            if (StringUtils.isNotEmpty(riskRuleListVO.getRuleType()) && riskRuleListVO.getRuleType().equals(RiskEventRule.MODEL)) {
                List<RiskEventRule> riskEventRuleList = getRiskEventRuleInstanceList(riskRuleListVO);
                List<RiskRuleListVO> childList = this.mapper.mapList(riskEventRuleList, RiskRuleListVO.class);
                if (childList != null) {
                    riskRuleListVO.setChildren(childList);
                }
            }
        }
        getRiskEventRuleList(mapList);
        getRiskEventRuleSettingData(mapList);
//        getRiskEventRuleFilterData(mapList);
        getRiskEventRuleAlarmNotice(mapList);
        getRiskEventRuleField(mapList);
        getRuleJsonFieldMappingCn(mapList);
        getRuleSourceMsg(mapList);
        result.setList(mapList);
        result.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        result.setTotal(pager.getTotalElements());
        result.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        return result;
    }

    public void getRuleSourceMsg(List<RiskRuleListVO> mapList){
        for(RiskRuleListVO riskRuleListVO : mapList){
            if("1".equals(riskRuleListVO.getStarted())){
                FilterOperatorGroupStartVO filterOperatorGroupStartVO = new FilterOperatorGroupStartVO();
                filterOperatorGroupStartVO.setGuids(String.join(",",riskRuleListVO.getId()));
                String msg = filterOperatorService.getRuleFilterSourceStatus(filterOperatorGroupStartVO);
                if(StringUtils.isNotBlank(msg)){
                    riskRuleListVO.setMsg(msg);
                    continue;
                }
                List<String> ruleIds = filterOperatorService.startFilterCheckFilterBaseLineData(filterOperatorGroupStartVO);
                if(CollectionUtils.isNotEmpty(ruleIds)){
                    List<String> ruleNames = new ArrayList<>();
                    for(String ruleId : ruleIds){
                        RiskEventRule riskEventRule = getOne(ruleId);
                        ruleNames.add(riskEventRule.getName());
                    }
                    if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(ruleNames)){
                        logger.warn("当前策略[{}]存在维表未配置数据，请先配置，再行启动！",String.join(",",ruleNames));
                        String result =  "当前策略["+String.join(",",ruleNames)+"]存在维表未配置数据，请先配置，再行启动！";
                        riskRuleListVO.setMsg(result);
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public List<RuleFilterVo> riskEventRuleFilterData(String id) {
        List<QueryCondition> ruleFilerParams = new ArrayList<>();
        ruleFilerParams.add(QueryCondition.eq("ruleId", id));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilerParams);
        List<RuleFilterVo> ruleFilterVos = new ArrayList<>();
        for (RuleFilter ruleFilter : ruleFilters) {
            RuleFilterVo ruleFilterVo = new RuleFilterVo();
            List<QueryCondition> filterParams = new ArrayList<>();
            filterParams.add(QueryCondition.eq("code", ruleFilter.getFilterCode()));
            filterParams.add(QueryCondition.eq("deleteFlag",true));
            List<FilterOperator> filterOperators = filterOperatorService.findAll(filterParams);
            if (CollectionUtils.isEmpty(filterOperators)) {
                continue;
            }
            FilterOperator filterOperator = filterOperators.get(0);
            ruleFilterVo.setFilterCode(ruleFilter.getFilterCode());
            ruleFilterVo.setFilterName(filterOperator.getLabel());
            ruleFilterVo.setFilterType(filterOperator.getFilterType());
            ruleFilterVo.setRuleId(ruleFilter.getRuleId());
            ruleFilterVo.setGuid(filterOperator.getGuid());
            if(StringUtils.isNotEmpty(filterOperator.getParamValue())){
                ruleFilterVo.setParamValue(gson.fromJson(filterOperator.getParamValue(),Map.class));
            }
            if(StringUtils.isNotEmpty(filterOperator.getStartConfig())){
                ruleFilterVo.setStartConfig(gson.fromJson(filterOperator.getStartConfig(), StartConfigVO.class));
            }
            if(StringUtils.isNotEmpty(filterOperator.getParamConfig())){
                ruleFilterVo.setParamConfig(gson.fromJson(filterOperator.getParamConfig(),List.class));
            }
            if(StringUtils.isNotEmpty(filterOperator.getFilterConfig())){
                ruleFilterVo.setFilterConfig(gson.fromJson(filterOperator.getFilterConfig(),FilterConfigObject.class));
            }
            ruleFilterVo.setTag(filterOperator.getTag());
            ruleFilterVo.setIsStarted(ruleFilter.getIsStarted());

            // 查询判断是否可以进行维表配置
            boolean isConfig = commonFilterOperatorService.isConfigParam(filterOperator);
            if(isConfig){
                ruleFilterVo.setIsConfigure("1");
            }else{
                ruleFilterVo.setIsConfigure("0");
            }
            // 通过规则获取该规则涉及到的维表
            Set<String> dimensionTableNames = getDimensionTableNames(ruleFilter.getFilterCode());
            String isConfigDataStr = "--";
            if(CollectionUtils.isNotEmpty(dimensionTableNames)){
                String isConfigData = "";
                for(String dimensionName : dimensionTableNames){
                    if(dimensionName.startsWith("baseline")){
                        boolean isConfigDataFlag = getDimensionDataForId(id,ruleFilter.getFilterCode(),dimensionName);
                        isConfigData = isConfigDataFlag ? "1":"0";
                    }
                }
                if(StringUtils.isNotBlank(isConfigData) && "1".equals(isConfigData)){
                    isConfigDataStr = "已配置";
                }else if(StringUtils.isNotBlank(isConfigData) && "0".equals(isConfigData)){
                    isConfigDataStr = "未配置";
                }else{
                    isConfigDataStr = "--";
                }
            }
            ruleFilterVo.setIsConfigData(isConfigDataStr);
            ruleFilterVos.add(ruleFilterVo);
        }
        return ruleFilterVos;
    }


    public boolean isConfigDimension(String filterCode){
        FilterOpertorVO filterOpertorVO = filterOperatorService.getFilterOpertorVOByAnlysisId(filterCode);
        if (filterOpertorVO != null) {
            Set<String> dimensionTableNames = getDimensionTableNames(filterOpertorVO);
            dimensionTableNames = dimensionTableNames.stream().filter(item->item.startsWith("baseline")).collect(Collectors.toSet());
            if(CollectionUtils.isNotEmpty(dimensionTableNames)){
                return true;
            }
        }
        return false;
    }

    boolean getDimensionDataForId(String ruleId,String filterCode,String dimensionName){
        String sql = "select count(1) as count from {0} where rule_code = {1} and filter_code = {2};";
        sql = sql.replace("{0}",dimensionName).replace("{1}","'"+ruleId+"'").replace("{2}","'"+filterCode+"'");
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);
        if(CollectionUtils.isEmpty(result)){
            return false;
        }
        Map<String,Object> map = result.get(0);
        Integer count = Integer.valueOf(String.valueOf(map.get("count")));
        if(count > 0){
            return true;
        }
        return false;
    }

    @Override
    public Set<String> getDimensionTableNames(String filterCode){
        FilterOpertorVO filterOpertorVO = filterOperatorService.getFilterOpertorVOByAnlysisId(filterCode);
        if (filterOpertorVO != null) {
            Set<String> dimensionTableNames = getDimensionTableNames(filterOpertorVO);
            return dimensionTableNames;
        }
        return null;
    }

    @Override
    public List<ParamsContent> getParamsContents(String ruleId, String filterCode) {
        List<ParamsContent> paramsContents = new ArrayList<>();
        FilterOpertorVO filterOpertorVO = filterOperatorService.getFilterOpertorVOByAnlysisId(filterCode);
        if (filterOpertorVO != null) {
            FilterConfigObject filterConfig = filterOpertorVO.getFilterConfig();
            Tables[][] tables = filterConfig.getTables();
            Exchanges[][] exchanges = filterConfig.getExchanges();

            Set<String> dimensionTableNames = getDimensionTableNames(filterOpertorVO);

            logger.debug("dimensionTableNames:{}" + JSON.toJSONString(dimensionTableNames));
            for (String dimensionTableName : dimensionTableNames) {
                if(!dimensionTableName.startsWith("baseline")){
                    continue;
                }
                List<DimensionTableColumn> dimensionTableColumns = alarmAnalysisService.getDimensionTableColumns(dimensionTableName);
                dimensionTableColumns = dimensionTableColumns.stream().filter(item->!"insert_time".equals(item.getColumnName())).collect(Collectors.toList());
                DimensionTableColumn isSyncdimensionTableColumn = dimensionTableColumns.stream().filter(item->"is_sync".equals(item.getColumnName())).collect(Collectors.toList()).get(0);
                List<DimensionTableColumn> colunmIntersection = getColunmIntersection(tables, exchanges,
                        dimensionTableColumns);
                colunmIntersection.add(isSyncdimensionTableColumn);
                logger.debug("colunmIntersection:{}" + JSON.toJSONString(colunmIntersection));
                ParamsContent paramsContent = getParamsContent(dimensionTableName, colunmIntersection);
                if (paramsContent != null) {
                    // 查看维表 是否存在数据 paramsContent
                    ParamsData paramsData = new ParamsData();

                    List<DataRow> dataRows = alarmAnalysisService.getDimensionTableData(dimensionTableName, ruleId,filterOpertorVO.getCode(), paramsContent.getParamsColumns().getDisplay());
                    List<DataRow> baselineDataRows = alarmAnalysisService.getBaselineDataRows(dimensionTableName, paramsContent.getParamsColumns().getDisplay());
                    // 基线表数据需要做清洗
//					ParamsColumns paramsColumns = paramsContent.getParamsColumns();
//					List<ParamsColumn> display = paramsColumns.getDisplay();

                    paramsData.setBaseline(baselineDataRows);
                    paramsData.setDisplay(dataRows);
                    paramsContent.setParamsData(paramsData);

                    paramsContents.add(paramsContent);
                }
            }
        }
        return paramsContents;
    }

    private ParamsContent getParamsContent(String dimensionTableName, List<DimensionTableColumn> dimensionTableColumns) {
        if (!org.apache.commons.lang3.StringUtils.isEmpty(dimensionTableName)) {
            ParamsContent paramsContent = new ParamsContent();

            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("nameEn", dimensionTableName));
            conditions.add(QueryCondition.or(QueryCondition.isNull("tableType"), QueryCondition.notEq("tableType", "base")));
            List<DimensionTableInfo> dimensionTableInfos = dimensionTableService.findAll(conditions);

            String tableGuid = "";

            if (dimensionTableInfos != null && !dimensionTableInfos.isEmpty()) {

                DimensionTableInfo dimensionTableInfo = dimensionTableInfos.get(0);
                paramsContent.setTabName(dimensionTableInfo.getName());
                paramsContent.setParamsDesc(dimensionTableInfo.getDescription());

                tableGuid = dimensionTableInfo.getGuid();

                if ("baseline".equals(dimensionTableInfo.getTableType())) {
                    paramsContent.setBtnType(2);
                } else {
                    paramsContent.setBtnType(1);
                }
            } else {
                //表示不是定制的维表
                return null;
            }

            paramsContent.setDimensionTableName(dimensionTableName);
            ParamsColumns paramsColumns = new ParamsColumns();
            List<ParamsColumn> display = new ArrayList<>();
            List<QueryParam> queryParams = new ArrayList<>();

//			"constant">指定值
//			"attribute">源表字段
//			"eventAttribute">维表字段
            for (DimensionTableColumn dimensionTableColumn : dimensionTableColumns) {
                ParamsColumn paramsColumn = new ParamsColumn();
                paramsColumn.setDataIndex(dimensionTableColumn.getColumnName());

                QueryParam queryParam = new QueryParam();
                queryParam.setQueryKey(dimensionTableColumn.getColumnName());
                queryParam.setQueryName(dimensionTableColumn.getColumnComment());
                queryParams.add(queryParam);

                String columnType = dimensionTableColumn.getColumnType();
                switch (columnType) {
                    case "date":
                    case "datetime":
                    case "time":
                        paramsColumn.setType("input");
                        break;
                    case "int":
                        paramsColumn.setType("number");
                        break;
                    case "bigint":
                        paramsColumn.setType("number");
                        break;
                    case "double":
                        paramsColumn.setType("number");
                        break;
                    case "varchar":
                    case "nvarchar":
                    case "text":
                    default:
                        paramsColumn.setType("input");
                        break;
                }

                paramsColumn.setTitle(dimensionTableColumn.getColumnComment());

                List<QueryCondition> querys = new ArrayList<>();
                querys.add(QueryCondition.eq("tableGuid", tableGuid));
                querys.add(QueryCondition.eq("fieldName", dimensionTableColumn.getColumnName()));
                List<DimensionTableField> findAll = dimensionTableFieldService.findAll(querys);
                if (findAll != null && !findAll.isEmpty()) {
                    DimensionTableField dimensionTableField = findAll.get(0);

                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(dimensionTableField.getFormatType()) || org.apache.commons.lang3.StringUtils.isNotEmpty(dimensionTableField.getEnumType())) {

                        if ("person".equals(dimensionTableField.getFormatType())) {

                            paramsColumn.setType("userSelect");
                            List<BasePersonZjg> allPerson = feignCache.getAllBasePersonZjg();

                            if (allPerson == null) {
                                logger.error("getAllBasePersonZjg出现异常");
                                allPerson = new ArrayList<>();
                            }
                            List<NameValue> enums = new ArrayList<>();

                            //展示提示信息为：将员工编号（user_no）转换为人员名称（user_name）展示
                            if ("user_no".equals(dimensionTableField.getEnumType())) {
                                for (BasePersonZjg person : allPerson) {
                                    enums.add(new NameValue(person.getUserName(), person.getUserNo()));
                                }
                            } else if ("user_idn_ex".equals(dimensionTableField.getEnumType())) {
                                for (BasePersonZjg person : allPerson) {
                                    enums.add(new NameValue(person.getUserName(), person.getUserIdnEx()));
                                }
                            }

                            paramsColumn.setItems(enums);
                        } else if ("unit".equals(dimensionTableField.getFormatType())) {
                            paramsColumn.setType("orgSelect");

                            //			展示提示信息为：将机构编码（code）转换为机构名称（name）展示
                            Map<String, String> param = new HashMap<>();
                            param.put("count_", "100000");
                            param.put("start_", "0");
                            List<BaseKoalOrg> orgList = feignCache.getOrgPage(param);

                            if (orgList == null) {
                                logger.error("getAllBasePersonZjg出现异常");
                                orgList = new ArrayList<>();
                            }

                            if ("code".equals(dimensionTableField.getEnumType())) {
                                List<NameValue> enums = new ArrayList<>();
                                for (BaseKoalOrg org : orgList) {
                                    enums.add(new NameValue(org.getName(), org.getCode()));
                                }
                                paramsColumn.setItems(enums);
                            }
                        } else {
                            if (!org.apache.commons.lang3.StringUtils.isEmpty(dimensionTableField.getEnumType())) {

                                paramsColumn.setType("select");

                                Map<String, Object> param = new HashMap<>();

                                param.put("by_", "asc");
                                param.put("count_", "100");
                                param.put("order_", "sort");
                                param.put("start_", 0);
                                param.put("parentType", dimensionTableField.getEnumType());

                                List<BaseDictAll> list = feignCache.getPageDict(param);
                                if (list != null) {

                                    List<NameValue> enums = new ArrayList<>();
                                    for (BaseDictAll dic : list) {
                                        enums.add(new NameValue(dic.getCodeValue(), dic.getCode()));
                                    }
                                    paramsColumn.setItems(enums);
                                }
                            }
                        }
                    }
                }

                display.add(paramsColumn);
            }
            if (display == null || display.isEmpty()) {
                return null;
            }
            paramsColumns.setHand(display);
            paramsColumns.setDisplay(display);
            paramsColumns.setBaseline(display);

            paramsContent.setQueryParams(queryParams);
            paramsContent.setParamsColumns(paramsColumns);

            return paramsContent;
        }

        return null;
    }

    private List<DimensionTableColumn> getColunmIntersection(Tables[][] tables, Exchanges[][] exchanges,
                                                             List<DimensionTableColumn> dimensionTableColumns) {
        List<DimensionTableColumn> colunmIntersection = new ArrayList<>();
        for (DimensionTableColumn column : dimensionTableColumns) {

            boolean isHave = false;

            if (Boolean.TRUE.equals(column.getIsMust())) {
                if (!"id".equals(column.getColumnName()) && !"foreign_key_id".equals(column.getColumnName())) {
                    isHave = true;
                }
            }

            for (Tables[] tableaArr : tables) {
                if (isHave) {
                    break;
                }
                for (Tables tablea : tableaArr) {
                    if (isHave) {
                        break;
                    }
                    if (tablea.getAttachs() != null && !tablea.getAttachs().isEmpty()) {
                        for (Attach attach : tablea.getAttachs()) {
                            if (isHave) {
                                break;
                            }
                            if (org.apache.commons.lang3.StringUtils.isEmpty(attach.getOptions())) {
                                continue;
                            }
                            String options = attach.getOptions();
                            if(!isJSONObject(options)){
                                continue;
                            }
                            JSONObject optionsJosn = JSONObject.parseObject(attach.getOptions());
                            String dimensionFieldName = optionsJosn.getString("dimensionFieldName");
                            if(StringUtils.isNotEmpty(dimensionFieldName)){
                                List<String> fieldNames = new ArrayList<>(Arrays.asList(dimensionFieldName.split(",")));
                                if (fieldNames.contains(column.getColumnName())) {
                                    if (column.getColumnName().contains("user_type")) {
                                        logger.debug(attach.getOptions());
                                    }
                                    isHave = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (isHave) {
                colunmIntersection.add(column);
            }

        }
        return colunmIntersection;
    }

    private boolean isJSONObject(String options) {
        try{
            JSONObject.parseObject(options);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    private Set<String> getDimensionTableNames(FilterOpertorVO filterOpertorVO) {

        Set<String> dimensionTableNames = new HashSet<>();
        FilterConfigObject filterConfig = filterOpertorVO.getFilterConfig();
        Tables[][] tables = filterConfig.getTables();
        if (tables != null && tables.length > 0) {
            for (int i = 0; i < tables.length; i++) {
                Tables[] tableItms = tables[i];

                for (int j = 0; j < tableItms.length; j++) {
                    Tables table = tableItms[j];

                    if (table.getAttachs() != null && !table.getAttachs().isEmpty()) {
                        for (Attach attach : table.getAttachs()) {
                            if (attach != null && "dimension".equals(attach.getType())) {

                                //dimensionTableName
                                String options = attach.getOptions();
                                Map fromJson = gson.fromJson(options, Map.class);

                                if (fromJson.containsKey("dimensionTableName")) {
                                    String dimensionTableName = fromJson.get("dimensionTableName").toString();
                                    dimensionTableNames.add(dimensionTableName);
                                }
                            }
                        }
                    }

                }

            }
        }

        return dimensionTableNames;
    }

    public void getRiskEventRuleSettingData(List<RiskRuleListVO> mapList) {
        for (RiskRuleListVO riskRuleListVO : mapList) {
            List<QueryCondition> settingParams = new ArrayList<>();
            settingParams.add(QueryCondition.eq("ruleCode", riskRuleListVO.getId()));
            List<EventAlarmSetting> eventAlarmSettings = eventAlarmSettingService.findAll(settingParams);
            if (CollectionUtils.isNotEmpty(eventAlarmSettings)) {
                EventAlarmSetting eventAlarmSetting = eventAlarmSettings.get(0);
                if (eventAlarmSetting != null) {
                    riskRuleListVO.setLinkType(eventAlarmSetting.getLinkType());
                    riskRuleListVO.setRulePath(eventAlarmSetting.getRulePath());
                    riskRuleListVO.setIsUrge(eventAlarmSetting.getIsUrge());
                    riskRuleListVO.setTimeLimitNum(eventAlarmSetting.getTimeLimitNum());
                    riskRuleListVO.setUrgeReason(eventAlarmSetting.getUrgeReason());
                    riskRuleListVO.setToRole(eventAlarmSetting.getToRole());
                    riskRuleListVO.setToUser(eventAlarmSetting.getToUser());
                    riskRuleListVO.setToAssetUser(eventAlarmSetting.getToAssetUser());
                }
            }
        }
    }

    private void getRiskEventRuleFilterData(List<RiskRuleListVO> mapList) {
        for (RiskRuleListVO riskRuleListVO : mapList) {
            List<QueryCondition> ruleFilerParams = new ArrayList<>();
            ruleFilerParams.add(QueryCondition.eq("ruleId", riskRuleListVO.getId()));
            List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilerParams);
            List<RuleFilterVo> ruleFilterVos = new ArrayList<>();
            for (RuleFilter ruleFilter : ruleFilters) {
                RuleFilterVo ruleFilterVo = new RuleFilterVo();
                List<QueryCondition> filterParams = new ArrayList<>();
                filterParams.add(QueryCondition.eq("code", ruleFilter.getFilterCode()));
                filterParams.add(QueryCondition.eq("deleteFlag",true));
                List<FilterOperator> filterOperators = filterOperatorService.findAll(filterParams);
                if (CollectionUtils.isEmpty(filterOperators)) {
                    continue;
                }
                FilterOperator filterOperator = filterOperators.get(0);
                ruleFilterVo.setFilterCode(ruleFilter.getFilterCode());
                ruleFilterVo.setFilterName(filterOperator.getLabel());
                ruleFilterVo.setFilterType(filterOperator.getFilterType());
                ruleFilterVo.setRuleId(ruleFilter.getRuleId());
                ruleFilterVo.setIsStarted(ruleFilter.getIsStarted());

                // 查询判断是否可以进行维表配置
                List<ParamsContent> paramsContents = getParamsContents(riskRuleListVO.getId(),ruleFilter.getFilterCode());
                if(CollectionUtils.isNotEmpty(paramsContents)){
                    ruleFilterVo.setIsConfigure("1");
                }else{
                    ruleFilterVo.setIsConfigure("0");
                }
                ruleFilterVos.add(ruleFilterVo);
            }
            riskRuleListVO.setFilters(ruleFilterVos);
        }
    }

    private List<RiskEventRule> getRiskEventRuleInstanceList(RiskRuleListVO riskRuleListVO) {
        List<QueryCondition> conditions1 = new ArrayList<>();
        conditions1.add(QueryCondition.eq("modelId", riskRuleListVO.getId()));
        conditions1.add(QueryCondition.eq("deleteFlag", true));
        Sort sort = Sort.by(Sort.Direction.ASC, "createdTime");
        return findAll(conditions1, sort);
    }

    /**
     * 告警规则分页查询条件集合
     *
     * @param riskEventRuleVO
     * @return
     */
    @Override
    public List<QueryCondition> getRiskEventQueryConditions(RiskEventRuleQueryVO riskEventRuleVO) {
        String riskEventRuleName = riskEventRuleVO.getRiskEventRuleName();
        Boolean attackType = riskEventRuleVO.getAttack_type();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("validations", VALIDATION));
        if (StringUtils.isNotEmpty(riskEventRuleName)) {
            conditions.add(QueryCondition.like("name", riskEventRuleName));
        }
        if (attackType != null) {
            conditions.add(QueryCondition.eq("attack_type", attackType));
        }
        String riskEventId = riskEventRuleVO.getRiskEventId();
        if (StringUtils.isNotEmpty(riskEventId)) {
            conditions.add(QueryCondition.like("riskEventId", riskEventId));
        }
        String warmType = riskEventRuleVO.getWarmType();
        if (StringUtils.isNotEmpty(warmType)) {
            conditions.add(QueryCondition.like("warmType", warmType));
        }
        Integer type = riskEventRuleVO.getType();
        if (type != null) {
            conditions.add(QueryCondition.eq("type", type));
        }
        String isStart = riskEventRuleVO.getStarted();
        if (StringUtils.isNotBlank(isStart)) {
            conditions.add(QueryCondition.eq("started", isStart));
        }

        String ruleLevel = riskEventRuleVO.getLevelstatus();
        if(StringUtils.isNotBlank(ruleLevel)){
            conditions.add(QueryCondition.in("levelstatus", new ArrayList<>(Arrays.asList(ruleLevel.split(",")))));
        }

        conditions.add(QueryCondition.eq("deleteFlag", true));
        conditions.add(QueryCondition.or(QueryCondition.isNull("ruleType"), QueryCondition.notEq("ruleType", RiskEventRule.INSTANCE)));
        return conditions;
    }

    /**
     * 自定义字段映射成中文
     *
     * @param mapList
     */
    public void getRuleJsonFieldMappingCn(List<RiskRuleListVO> mapList) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RiskRuleListVO riskRuleListVO : mapList) {
            String ruleFieldJson = riskRuleListVO.getRule_field_json();
            if (StringUtils.isNotEmpty(ruleFieldJson)) { //映射字段
                Map<String, Object> map = JsonMapper.fromJsonString(ruleFieldJson, Map.class);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Map<String, Object> newMap = new HashMap<>();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String cn = AlarmDealDictEnum.getRuleCn(key);
                    newMap.put("name", key);
                    newMap.put("value", value);
                    newMap.put("cn", cn);
                    list.add(newMap);
                }
                riskRuleListVO.setRule_field_json(JsonMapper.toJsonString(list));
            }
        }
    }

    /**
     * 获得RiskEvent的eventId和text
     *
     * @param mapList
     */
    private void getRiskEventRuleList(List<RiskRuleListVO> mapList) {
        for (RiskRuleListVO riskRuleListVO : mapList) {
            ValueText event = riskRuleListVO.getEvent();
            String eventId = event.getValue().toString();
            EventCategory eventCategory = eventCategoryService.getOne(eventId);
            if (eventCategory != null) {
                String title = eventCategory.getTitle();
                event.setText(title);
                EventCategory parentCategory = eventCategoryService.getOne(eventCategory.getParentId());
                if (parentCategory != null) {
                    ValueText parent = new ValueText();
                    parent.setText(parentCategory.getTitle());
                    parent.setValue(parentCategory.getId());
                    riskRuleListVO.setEventCategory(parent);
                }
            }
        }
    }

    /**
     * 获得告警规则响应
     *
     * @param mapList
     */
    private void getRiskEventRuleAlarmNotice(List<RiskRuleListVO> mapList) {
        for (RiskRuleListVO riskRuleListVO : mapList) {
            String extend2 = riskRuleListVO.getExtend2();
            AlarmNotice alarmNotice = JsonMapper.fromJsonString(extend2, AlarmNotice.class);
            riskRuleListVO.setAlarmNotice(alarmNotice);
        }
    }

    /**
     * 获取策略字段
     * @param mapList
     */
    private void getRiskEventRuleField(List<RiskRuleListVO> mapList) {
        for (RiskRuleListVO riskRuleListVO : mapList) {
            String fieldInfo = riskRuleListVO.getField_info();
            FieldInfoVO fieldInfoVO = JsonMapper.fromJsonString(fieldInfo, FieldInfoVO.class);
            riskRuleListVO.setFieldInfoVO(fieldInfoVO);
        }
    }

    /**
     * 添加策略
     * @param riskRuleEditVO
     * @return
     */
    @Override
    public Result<RiskRuleListVO> addRiskEventRule(RiskRuleEditVO riskRuleEditVO) {
        // 保存策略信息
        String id = UUIDUtils.get32UUID();
        RiskEventRule riskEventRule = mapper.map(riskRuleEditVO, RiskEventRule.class);
        RiskRuleListVO riskRuleListVo2 = saveRiskRuleObj(riskRuleEditVO,id,riskEventRule);
        // 保存权限setting信息
        saveEventAlarmSettingData(riskRuleEditVO, riskEventRule.getRiskEventId(), id);
        // 策略规则关联信息
        saveRuleFilterObj(riskRuleEditVO, id);
        Result<RiskRuleListVO> result = ResultUtil.success(riskRuleListVo2);
        return result;
    }

    /**
     * 策略规则关联信息
     * @param riskRuleEditVO
     * @param id
     */
    private void saveRuleFilterObj(RiskRuleEditVO riskRuleEditVO, String id) {
        List<RuleFilterVo> ruleFilters = riskRuleEditVO.getFilters();
        if (CollectionUtils.isNotEmpty(ruleFilters)) {
            for (RuleFilterVo ruleFilterVo : ruleFilters) {
                RuleFilter ruleFilter = mapper.map(ruleFilterVo, RuleFilter.class);
                ruleFilter.setRuleId(id);
                ruleFilter.setGuid(UUIDUtils.get32UUID());
                ruleFilterService.save(ruleFilter);
                EventRuleParams param = ruleFilterVo.getParams();
                if (param != null) {
                    param.setEventRuleId(ruleFilterVo.getRuleId());
                    param.setRuleCode(ruleFilterVo.getRuleId());
                    param.setFilterCode(ruleFilterVo.getFilterCode());
                    saveEventRuleParams(param);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            saveEventRuleParams(param);
//                        }
//                    }).start();
                }
            }
        }
    }

    /**
     * 保存策略数据
     * @param riskRuleEditVO
     * @param id
     * @param riskEventRule
     * @return
     */
    public RiskRuleListVO saveRiskRuleObj(RiskRuleEditVO riskRuleEditVO,String id,RiskEventRule riskEventRule){
        User user = SessionUtil.getCurrentUser();
        List<RelateSqlVO> relateList = riskRuleEditVO.getRelateList();
        String tableName = riskRuleEditVO.getTableName();
        String tableLabel = riskRuleEditVO.getTableLabel();
        String flag = riskRuleEditVO.getFlag();
        if ("complex".equals(flag)) { //复合模式
            // String dynamicRelateSql = this.getDynamicRelateSql(tableName, relateList);
            // riskEventRule.setRiskSql(dynamicRelateSql);
            if(relateList != null){
                String json = gson.toJson(relateList);
                riskEventRule.setExtend1(json);
            }
        } else {
            riskEventRule.setRiskSql(riskRuleEditVO.getRiskSql());
        }
        String orginalLogPath = this.getOrginalLogPath(tableName);
        AlarmNotice alarmNotice = riskRuleEditVO.getAlarmNotice();
        FieldInfoVO fieldInfoVO = riskRuleEditVO.getFieldInfoVO();
        String fieldStr = JsonMapper.toJsonString(fieldInfoVO);
        String alarmNoticeStr = JsonMapper.toJsonString(alarmNotice);
        riskEventRule.setId(id);
        if(StringUtils.isNotBlank(alarmNoticeStr)){
            riskEventRule.setExtend2(alarmNoticeStr);
        }
        riskEventRule.setRecommend(false);
        riskEventRule.setIsBuiltInData(false);
        riskEventRule.setThreatCredibility("1");
        riskEventRule.setProduceThreat(true);
        riskEventRule.setFailedStatus(0);
        riskEventRule.setMaxScore(Float.valueOf(1));
        riskEventRule.setCreatedTime(DateUtil.format(new Date()));
        riskEventRule.setCascadeState(CASCADE_STATE);
        riskEventRule.setValidations(VALIDATION);
        riskEventRule.setType(0);
        riskEventRule.setField_info(fieldStr); //添加关于引擎启动的部分目前不做处理
        riskEventRule.setDeleteFlag(true);
        riskEventRule.setInitStatus(riskRuleEditVO.getInitStatus());
        riskEventRule.setRuleCode(riskRuleEditVO.getName() + "_" + id);
        riskEventRule.setJob_name(riskRuleEditVO.getName() + " job" + UUIDUtils.get32UUID().toString());
        riskEventRule.setInitStatus(String.valueOf(0));
        riskEventRule.setRuleType(RiskEventRule.RULE);
        riskEventRule.setAnalysisId(id);
        riskEventRule.setInitStatus("0");
        riskEventRule.setCreateUsername(user.getName());
        riskEventRule.setCreateUserno(String.valueOf(user.getId()));
        String tableType = riskRuleEditVO.getTableType();
        setRiskEventRuleTypeInfo(riskEventRule, orginalLogPath, tableType);
        riskEventRule.setTableLabel(tableLabel);
        riskEventRule.setStarted("0");
        RiskRuleListVO riskRuleListVo2 = getRiskRuleListVO(riskEventRule);
        return riskRuleListVo2;
    }

    /**
     * 保存参数
     * @param item
     */
    private void saveEventRuleParams(EventRuleParams item) {
        List<ParamsContent> paramsContents = item.getParamsContents();
        if (paramsContents != null && !paramsContents.isEmpty()) {
            for (ParamsContent paramsContent : paramsContents) {
                ParamsData paramsData = paramsContent.getParamsData();
                if (StringUtils.isNotEmpty(paramsContent.getDimensionTableName())) {
                    List<DataRow> display = paramsData.getDisplay();
                    //通过 row  向表中添加数据
                    alarmAnalysisService.saveDimensionTableData(paramsContent.getDimensionTableName(), item.getRuleCode(), item.getFilterCode(), display);
                    // 清除redis缓存
                    redissonSingleUtil.deleteByPrex(paramsContent.getDimensionTableName());
                }
            }
        }
    }

    /**
     * 增加event_alram_setting信息  策略权限
     * @param riskRuleEditVO
     * @param ruleId
     * @param ruleCode
     */
    private void saveEventAlarmSettingData(RiskRuleEditVO riskRuleEditVO, String ruleId, String ruleCode) {
        // 增加event_alram_setting信息  策略权限
        // 传入isUrge、linkType、timeLimitNum、toAssetUser、toRole、toUser、urgeReason、validityDate
        EventAlarmSetting eventAlarmSetting = new EventAlarmSetting();
        eventAlarmSetting.setGuid(ruleCode);
        eventAlarmSetting.setLinkGuid(ruleId);
        eventAlarmSetting.setRulePath(riskRuleEditVO.getWarmType());
        eventAlarmSetting.setToAssetUser(riskRuleEditVO.getToAssetUser());
        eventAlarmSetting.setLinkType("category");
        eventAlarmSetting.setIsUrge(riskRuleEditVO.getIsUrge() == null ? false : riskRuleEditVO.getIsUrge());
        eventAlarmSetting.setTimeLimitNum(riskRuleEditVO.getTimeLimitNum() == null ? 0 :riskRuleEditVO.getTimeLimitNum());
        eventAlarmSetting.setToRole(riskRuleEditVO.getToRole());
        eventAlarmSetting.setToUser(riskRuleEditVO.getToUser());
        eventAlarmSetting.setUrgeReason(riskRuleEditVO.getUrgeReason());
        eventAlarmSetting.setRuleCode(ruleCode);
        if (StringUtils.isNotBlank(riskRuleEditVO.getToRole()) || StringUtils.isNotBlank(riskRuleEditVO.getToUser())) {
            eventAlarmSettingService.save(eventAlarmSetting);
        }
        alarmDataHandleService.putEventAlarmSettingMap(eventAlarmSetting);
    }

    /**
     * 编辑event_alram_setting信息  策略权限
     * @param riskRuleEditVO
     * @param ruleId
     * @param ruleCode
     */
    private void editEventAlarmSettingData(RiskRuleEditVO riskRuleEditVO, String ruleId, String ruleCode) {
        List<QueryCondition> settingParams = new ArrayList<>();
        settingParams.add(QueryCondition.eq("ruleCode", ruleCode));
        List<EventAlarmSetting> eventAlarmSettings = eventAlarmSettingService.findAll(settingParams);
        EventAlarmSetting eventAlarmSetting = new EventAlarmSetting();
        if (CollectionUtils.isNotEmpty(eventAlarmSettings)) {
            eventAlarmSetting = eventAlarmSettings.get(0);
        }else{
            eventAlarmSetting.setGuid(riskRuleEditVO.getId());
            eventAlarmSetting.setLinkGuid(ruleId);
        }
        eventAlarmSetting.setRulePath(riskRuleEditVO.getWarmType());
        eventAlarmSetting.setToAssetUser(riskRuleEditVO.getToAssetUser() == null ? false : riskRuleEditVO.getToAssetUser());
        eventAlarmSetting.setLinkType("category");
        eventAlarmSetting.setIsUrge(riskRuleEditVO.getIsUrge() == null ? false : riskRuleEditVO.getIsUrge());
        eventAlarmSetting.setTimeLimitNum(riskRuleEditVO.getTimeLimitNum() == null ? 0 : riskRuleEditVO.getTimeLimitNum());
        eventAlarmSetting.setToRole(riskRuleEditVO.getToRole());
        eventAlarmSetting.setToUser(riskRuleEditVO.getToUser());
        eventAlarmSetting.setUrgeReason(riskRuleEditVO.getUrgeReason());
        eventAlarmSetting.setRuleCode(ruleCode);
        eventAlarmSettingService.save(eventAlarmSetting);
        alarmDataHandleService.putEventAlarmSettingMap(eventAlarmSetting);
    }

    /**
     * 设置策略主函数
     * @param riskEventRule
     * @param orginalLogPath
     * @param tableType
     */
    private void setRiskEventRuleTypeInfo(RiskEventRule riskEventRule, String orginalLogPath, String tableType) {
        if (StringUtils.isNotEmpty(tableType)) {
            if (tableType.equals(LOGVO_TYPE)) {
                riskEventRule.setLogPath(orginalLogPath);
                riskEventRule.setMain_class("com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction");
            } else {
                riskEventRule.setLogPath("");
                riskEventRule.setMain_class("com.vrv.rule.ruleInfo.FlinkJdbcFilterFunction");
            }
        } else { //默认采用VO
            riskEventRule.setLogPath(orginalLogPath);
            riskEventRule.setMain_class("com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction");
        }
    }

    /**
     * 告警规则发生变化
     *
     * @param riskEventRule
     * @return
     */
    private RiskRuleListVO getRiskRuleListVO(RiskEventRule riskEventRule) {
        save(riskEventRule);
        alarmDataHandleService.putRiskEventRuleMap(riskEventRule);
        RiskRuleListVO riskRuleListVO = mapper.map(riskEventRule, RiskRuleListVO.class);
        List<RiskRuleListVO> riskList = new ArrayList<>();
        riskList.add(riskRuleListVO);
        getRiskEventRuleList(riskList);
        getRiskEventRuleAlarmNotice(riskList);
        getRiskEventRuleField(riskList);
        getRuleJsonFieldMappingCn(riskList);
        RiskRuleListVO riskRuleListVo2 = riskList.get(0);
        return riskRuleListVo2;
    }

    /**
     * 编辑规则
     * @param riskRuleEditVO
     * @return
     */
    @Override
    @Transactional
    public Result<RiskRuleListVO> editRiskEventRule(RiskRuleEditVO riskRuleEditVO) {
        User user = SessionUtil.getCurrentUser();
        // 保存策略信息
        RiskEventRule riskEventRule = saveRiskRuleObj(riskRuleEditVO,user);
        setRiskEventRuleTypeInfo(riskEventRule, getOrginalLogPath(riskRuleEditVO.getTableName()), riskRuleEditVO.getTableType());
        RiskRuleListVO riskRuleListVO = getRiskRuleListVO(riskEventRule);
        // 增加event_alram_setting信息  策略权限
        List<String> roleCode = user.getRoleCode();
        if (!roleCode.contains("businessMgr") && !roleCode.contains("operationMgr")) {
            editEventAlarmSettingData(riskRuleEditVO, riskEventRule.getRiskEventId(), riskEventRule.getId());
            logger.info("editEventAlarmSettingData");
        }
        // 策略规则关联信息
        // 删除原关联数据
        deleteRuleFilter(riskEventRule.getId());
        // 保存新的关联数据
        saveRuleFilter(riskRuleEditVO);
        // 如果状态是1 则重启规则
        if ("1".equals(riskRuleEditVO.getStarted())) {
            reStartRuleFilter(riskEventRule);
            addRuleInfoToAlarmHandler(riskEventRule);
        }
        Result<RiskRuleListVO> result = ResultUtil.success(riskRuleListVO);
        return result;
    }

    /**
     * 保存新的关联数据
     * @param riskRuleEditVO
     */
    private void saveRuleFilter(RiskRuleEditVO riskRuleEditVO) {
        List<RuleFilterVo> ruleFilters = riskRuleEditVO.getFilters();
        if (CollectionUtils.isNotEmpty(ruleFilters)) {
            for (RuleFilterVo ruleFilterVo : ruleFilters) {
                RuleFilter ruleFilter = mapper.map(ruleFilterVo, RuleFilter.class);
                ruleFilter.setRuleId(riskRuleEditVO.getId());
                ruleFilter.setGuid(UUIDUtils.get32UUID());
                ruleFilterService.save(ruleFilter);
                // 保存参数信息
                if (ruleFilterVo.getParams() != null) {
                    saveEventRuleParams(ruleFilterVo.getParams());
                }
            }
        }
    }

    /**
     * 删除策略规则关联关系
     * @param id
     */
    private void deleteRuleFilter(String id) {
        String sql = "delete from rule_filter where rule_id = '" + id + "'";
        jdbcTemplate.execute(sql);
    }

    /**
     * 构建策略基础信息
     * @param riskRuleEditVO
     * @param user
     * @return
     */
    public RiskEventRule saveRiskRuleObj(RiskRuleEditVO riskRuleEditVO,User user){
        RiskEventRule riskEventRule = getOne(riskRuleEditVO.getId());
        RiskEventRule riskEventRule1 = new RiskEventRule();
        mapper.copy(riskRuleEditVO, riskEventRule1);
        if ("complex".equals(riskRuleEditVO.getFlag())) { //复合模式
            String dynamicRelateSql = this.getDynamicRelateSql(riskRuleEditVO.getTableName(), riskRuleEditVO.getRelateList());
            riskEventRule1.setRiskSql(dynamicRelateSql);
            String json = gson.toJson(riskRuleEditVO.getRelateList());
            riskEventRule1.setExtend1(json);
        } else {
            riskEventRule1.setRiskSql(riskRuleEditVO.getRiskSql());
        }
        riskEventRule1.setType(riskEventRule.getType());
        riskEventRule1.setField_info(JsonMapper.toJsonString(riskRuleEditVO.getFieldInfoVO()));
        riskEventRule1.setExtend2(JsonMapper.toJsonString(riskRuleEditVO.getAlarmNotice()));
        riskEventRule1.setModifiedTime(DateUtil.format(new Date()));
        riskEventRule1.setCreatedTime(riskEventRule.getCreatedTime());
        riskEventRule1.setCascadeState(CASCADE_STATE);
        riskEventRule1.setValidations(VALIDATION);
        riskEventRule1.setJob_name(riskEventRule.getJob_name());
        riskEventRule1.setMain_class(riskEventRule.getMain_class());
        riskEventRule1.setRule_field_json(riskRuleEditVO.getRule_field_json()); //自定义表单字段
        riskEventRule1.setRule_desc(riskEventRule.getRule_desc()); //规则对应的描述
        riskEventRule1.setUnitScore(riskRuleEditVO.getUnitScore()); //单位分数
        riskEventRule1.setMaxScore(riskRuleEditVO.getMaxScore());
        riskEventRule1.setRule_desc(riskEventRule.getRule_desc()); //规则对应的描述
        riskEventRule1.setTableLabel(riskRuleEditVO.getTableLabel());
        riskEventRule1.setWarmType(riskRuleEditVO.getWarmType());
        riskEventRule1.setInitStatus(riskRuleEditVO.getInitStatus());
        riskEventRule1.setRuleCode(riskEventRule.getRuleCode());
        riskEventRule1.setAllowStart(true);
        riskEventRule1.setDeleteFlag(true);
        riskEventRule1.setAnalysisId(riskEventRule.getAnalysisId());
        riskEventRule1.setStarted(riskRuleEditVO.getStarted());
        riskEventRule1.setCreateUserno(riskEventRule.getCreateUserno());
        riskEventRule1.setCreateUsername(riskEventRule.getCreateUsername());
        riskEventRule1.setUpdateUsername(user.getName());
        riskEventRule1.setUpdateUserno(String.valueOf(user.getId()));
        riskEventRule1.setInitStatus(riskEventRule.getInitStatus());
        riskEventRule1.setStarted(riskEventRule.getStarted());
        riskEventRule1.setThreatCredibility(riskEventRule.getThreatCredibility());
        riskEventRule1.setRecommend(riskEventRule.getRecommend());
        riskEventRule1.setIsBuiltInData(riskEventRule.getIsBuiltInData());
        riskEventRule1.setProduceThreat(riskEventRule.getProduceThreat());
        riskEventRule1.setFailedStatus(riskEventRule.getFailedStatus());
        riskEventRule1.setMaxScore(riskEventRule.getMaxScore());
        return riskEventRule1;
    }


    /**
     * 重启任务
     * @param riskEventRule
     */
    private void reStartRuleFilter(RiskEventRule riskEventRule) {
        String startType = ruleFlinkTypeService.getRuleFlinkStart();
        if("category".equals(startType)){
            // 停掉任务
            EventCategory eventCategory = eventCategoryService.getOne(riskEventRule.getRiskEventId());
            EventCategory eventParentCategory = eventCategoryService.getOne(eventCategory.getParentId());
            filterOperatorService.stopJobByJobName(eventParentCategory.getTitle());
            // 启动任务
            List<String> riskEventIds = getRuleListByCategory(riskEventRule.getRiskEventId());
            if (CollectionUtils.isNotEmpty(riskEventIds)) {
                Map<String,List<String>> map = new HashMap<>();
                for(String ruleId : riskEventIds){
                    List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId,true);
                    if(CollectionUtils.isNotEmpty(filterCodes)){
                        map.put(ruleId,filterCodes);
                    }
                }
                if(!map.isEmpty()){
                    filterOperatorService.startOperatorJobGroup(eventParentCategory.getId(), map,startType);
                }

            }
        }else{
            List<String> sourceIds = getSourceByRuleId(riskEventRule.getId());
            for(String sourceId : sourceIds){
                EventTable eventTable = eventTabelService.getOne(sourceId);
                if(eventTable!=null){
                    filterOperatorService.stopJobByJobName(eventTable.getLabel());
                }
                List<String> riskEventIds = getRuleListBySourceId(sourceId);
                if (CollectionUtils.isNotEmpty(riskEventIds)) {
                    Map<String,List<String>> map = new HashMap<>();
                    for(String ruleId : riskEventIds){
                        List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId,false,sourceId);
                        if(CollectionUtils.isNotEmpty(filterCodes)){
                            map.put(ruleId,filterCodes);
                        }
                    }
                    if(!map.isEmpty()){
                        filterOperatorService.startOperatorJobGroup(sourceId, map,startType);
                    }

                }
            }
        }
    }

    public List<String> getRuleListBySourceId(String sourceId){
        List<QueryCondition> filterCondition = new ArrayList<>();
        filterCondition.add(QueryCondition.like("sourceId",sourceId));
        filterCondition.add(QueryCondition.eq("deleteFlag",true));
        List<FilterOperator> filterOperators = filterOperatorService.findAll(filterCondition);
        List<String> filterCodes = filterOperators.stream().map(FilterOperator::getCode).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(filterCodes)){
            List<QueryCondition> ruleCondition = new ArrayList<>();
            ruleCondition.add(QueryCondition.eq("isStarted","1"));
            ruleCondition.add(QueryCondition.in("filterCode",filterCodes));
            List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleCondition);
            List<String> ruleIds = ruleFilters.stream().map(RuleFilter::getRuleId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(ruleIds)){
                List<QueryCondition> riskCondition = new ArrayList<>();
                riskCondition.add(QueryCondition.eq("deleteFlag",true));
                riskCondition.add(QueryCondition.eq("started",true));
                riskCondition.add(QueryCondition.in("id",ruleIds));
                List<RiskEventRule> riskEventRules = findAll(riskCondition);
                return riskEventRules.stream().map(RiskEventRule::getId).collect(Collectors.toList());
            }
        }
        return null;
    }

    /**
     * 通过分类ID 查询该二级分类下的全部启动规则
     * @param categoryId
     * @return
     */
    public List<String> getRuleListByCategory(String categoryId){
        // 查分类，获取二级分类ID
        EventCategory eventCategory = eventCategoryService.getOne(categoryId);

        // 通过二级分类ID，查询下级全部的分类
        List<QueryCondition> categoryCondition = new ArrayList<>();
        categoryCondition.add(QueryCondition.eq("parentId",eventCategory.getParentId()));
        List<EventCategory> eventCategories = eventCategoryService.findAll(categoryCondition);
        List<String> eventCategoryId = eventCategories.stream().map(EventCategory::getId).collect(Collectors.toList());

        // 通过分类查询全部的规则
        List<QueryCondition> riskContidion = new ArrayList<>();
        riskContidion.add(QueryCondition.eq("deleteFlag",true));
        riskContidion.add(QueryCondition.eq("started",true));
        riskContidion.add(QueryCondition.in("riskEventId",eventCategoryId));
        List<RiskEventRule> riskEventRules = findAll(riskContidion);
        if(CollectionUtils.isNotEmpty(riskEventRules)){
            return riskEventRules.stream().map(RiskEventRule::getId).collect(Collectors.toList());
        }
        return null;
    }

    public List<String> getSourceByRuleId(String ruleId){
        List<QueryCondition> ruleCondition = new ArrayList<>();
        ruleCondition.add(QueryCondition.eq("isStarted",true));
        ruleCondition.add(QueryCondition.eq("ruleId",ruleId));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleCondition);
        if(CollectionUtils.isNotEmpty(ruleFilters)){
            List<String> filterCodes = ruleFilters.stream().map(RuleFilter::getFilterCode).collect(Collectors.toList());
            List<QueryCondition> filterCondition = new ArrayList<>();
            filterCondition.add(QueryCondition.eq("deleteFlag",true));
            filterCondition.add(QueryCondition.in("code",filterCodes));
            List<FilterOperator> filterOperators = filterOperatorService.findAll(filterCondition);
            String sourceIds = filterOperatorService.getSourceIds(filterOperators);
            return Arrays.asList(sourceIds.split(","));
        }
        return null;
    }

    /**
     * 将开启或者是编辑的规则添加到处理器当中
     *
     * @param riskEventRule
     */
    @Override
    public void addRuleInfoToAlarmHandler(RiskEventRule riskEventRule) {
        String ruleCode = riskEventRule.getRuleCode();
        RuleMergeHandler ruleMergeHandler = new RuleMergeHandler();
        alarmInfoMergerHandler.ruleMapContructor(riskEventRule, ruleCode, ruleMergeHandler);
    }

    /**
     * 删除策略
     * @param ids
     * @return
     */
    @Override
    public Result<Boolean> delRiskEventRules(List<String> ids) {
        try {
            for (String id : ids) {
                RiskEventRule riskEventRule = getOne(id);
                if ("1".equals(riskEventRule.getStarted())) {
                    throw new AlarmDealException(ResultCodeEnum.Unauthorized.getCode(), "该策略启动状态，无法删除，请先停止策略！");
                }
                riskEventRule.setDeleteFlag(false);
                save(riskEventRule);
                // eventAlarmSettingService.delete(id);
                // reStartRuleFilter(riskEventRule);
            }
            Result<Boolean> result = SocUtil.getBooleanResult(true);
            return result;
        } catch (AlarmDealException e) {
            throw new AlarmDealException(ResultCodeEnum.SUCCESS.getCode(), e.getMessage());
        }
    }

    /**
     * 改变策略
     * @param riskRuleIdVO
     * @return
     */
    @Override
    @Transactional
    public Result<RiskRuleListVO> changeRiskEventRule(RiskRuleIdVO riskRuleIdVO) {
        String id = riskRuleIdVO.getId();
        Boolean booleanResult = riskRuleIdVO.getResult();
        RiskEventRule riskEventRule = getOne(id);
        riskEventRule.setModifiedTime(DateUtil.format(new Date()));
        riskEventRule.setStarted(booleanResult ? "1" : "0");
        riskEventRule.setType(0);
        addRuleInfoToAlarmHandler(riskEventRule);
        //启动flink相关Job
        RuleProcessVO ruleProcessVO = getRuleProcessVO(riskEventRule);
        if (booleanResult) {
            startAlarmAnalysisEngine(ruleProcessVO);
        } else {
            stopAlarmAnalysisEngine(riskEventRule.getJob_name());
        }
        String flag = riskEventRule.getFlag();
        if ("editor".equals(flag)) {

            changeFilterOpertorStatus(ruleProcessVO, riskEventRule.getStarted());
        }
        RiskRuleListVO riskRuleListVO = getRiskRuleListVO(riskEventRule);
        Result<RiskRuleListVO> result = ResultUtil.success(riskRuleListVO);
        return result;
    }

    @Override
    @Transactional
    public void changeRiskEventRuleOnlyStatus(String guid, String status) {
        RiskEventRule riskEventRule = getOne(guid);
        if (riskEventRule != null) {
            riskEventRule.setModifiedTime(DateUtil.format(new Date()));
            riskEventRule.setStarted(status);
            // riskEventRule.setType(0);
            addRuleInfoToAlarmHandler(riskEventRule);
        }
    }

    private RuleProcessVO getRuleProcessVO(RiskEventRule riskEventRule) {
        RuleProcessVO ruleProcessVO = new RuleProcessVO();
        String mainClass = riskEventRule.getMain_class(); //启动main函数
        String ruleCode = riskEventRule.getRuleCode(); //告警编码
        logger.info("ruleCode:" + ruleCode);
        String logPath = riskEventRule.getLogPath(); //日志路径
        String jobName = riskEventRule.getJob_name(); //job name
        String tableName = riskEventRule.getTableName(); //表名
        String riskSql = riskEventRule.getRiskSql();
        ruleProcessVO.setMain_class(mainClass);
        ruleProcessVO.setRuleType(riskEventRule.getFlag());
        ruleProcessVO.setAnalysisId(riskEventRule.getAnalysisId());
        ruleProcessVO.setRuleId(riskEventRule.getId());
        ruleProcessVO.setRuleCode(ruleCode);
        ruleProcessVO.setOrignalLogPath(logPath);
        ruleProcessVO.setSql(riskSql);
        ruleProcessVO.setJobName(jobName);
        ruleProcessVO.setTableName(tableName);
        ruleProcessVO.setRuleName(riskEventRule.getName());
        ruleProcessVO.setRuleLevel(riskEventRule.getLevelstatus());
        return ruleProcessVO;
    }

    /**
     * 修改规则状态
     * @param ruleProcessVO
     * @param status
     */
    private void changeFilterOpertorStatus(RuleProcessVO ruleProcessVO, String status) {
        String analysisId = ruleProcessVO.getAnalysisId();
        List<FilterOperator> analysisiors = filterOperatorService.getFilterOperators(analysisId);
        if (analysisiors.size() == 1) {
            FilterOperator filterOperator = analysisiors.get(0);
            filterOperatorService.updateFilterOperator(filterOperator, status);
        } else {
            throw new RuntimeException("该规则没有关联到分析器或者有多个分析器，请检查");
        }
    }

    /**
     * 判断策略是否存在
     * @param ruleCode
     * @param guid
     * @return
     */
    @Override
    public Result<Boolean> judgeRuleCodeIsRepeat(String ruleCode, String guid) {
        Result<Boolean> result = null;
        List<QueryCondition> conditions = new ArrayList<>();
        long count = 0;
        conditions.add(QueryCondition.eq("ruleCode", ruleCode));
        count = count(conditions);
        if (StringUtils.isNotEmpty(guid)) {
            RiskEventRule riskEventRule = getOne(guid);
            if (riskEventRule != null) {
                String originalCode = riskEventRule.getRuleCode();
                if (ruleCode.equals(originalCode)) {
                    result = SocUtil.getBooleanResult(true);
                    return result;
                } else {
                    count = count(conditions);
                }
            }
        }
        if (count == 0) {
            result = SocUtil.getBooleanResult(true);
        } else {
            result = SocUtil.getBooleanResult(false);
        }
        return result;
    }

    /**
     * flink-job 启动shell脚本
     */
    private String[] getStartJobShells(RuleProcessVO ruleProcessVO) {
        String[] exeShellArray = null;
        String mainClass = ruleProcessVO.getMain_class();
        String ruleCode = ruleProcessVO.getRuleCode();
        String jobName = ruleProcessVO.getJobName(); //热舞
        String sql = ruleProcessVO.getSql();
        String orignalLogPath = ruleProcessVO.getOrignalLogPath();
        String tableName = ruleProcessVO.getTableName();
        String ruleType = ruleProcessVO.getRuleType();
        String analysisId = ruleProcessVO.getAnalysisId();
        //TODO flink sql执行
        boolean remoteFlag = flinkConfiguration.isRemote_flag();
        if (remoteFlag) { //远程标识开启(需开启ssh免登陆)
            if ("editor".equals(ruleType)) {
                changeFilterOpertorStatus(ruleProcessVO, "1");
                exeShellArray = new String[]{"ssh", flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip(), flinkConfiguration.getFlink_home_path() + "/bin/flink", "run", "-d","-c", mainClass, flinkConfiguration.getFlink_jar_path(), analysisId};
            } else if (StringUtils.isNotEmpty(orignalLogPath)) {
                exeShellArray = new String[]{"ssh", flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip(), flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d", "-c", mainClass, flinkConfiguration.getFlink_jar_path(), ruleCode, "\"" + jobName + "\"", orignalLogPath, "\"" + sql + "\"", tableName};
            } else {
                exeShellArray = new String[]{"ssh", flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip(), flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d", "-c", mainClass, flinkConfiguration.getFlink_jar_path(), ruleCode, "\"" + jobName + "\"", "\"" + sql + "\"", tableName};
            }
        } else {
            if ("editor".equals(ruleType)) {
                exeShellArray = new String[]{flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d","-c", mainClass, flinkConfiguration.getFlink_jar_path(), analysisId};
            } else if (StringUtils.isNotEmpty(orignalLogPath)) {
                exeShellArray = new String[]{flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d", "-c", mainClass, flinkConfiguration.getFlink_jar_path(), ruleCode, jobName, orignalLogPath, sql, tableName};
            } else {
                exeShellArray = new String[]{flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d", "-c", mainClass, flinkConfiguration.getFlink_jar_path(), ruleCode, jobName, sql, tableName};
            }
        }
        String[] shellArray = exeShellArray;
        return shellArray;
    }

    @Override
    public Result<Boolean> startAlarmAnalysisEngine(RuleProcessVO ruleProcessVO) {
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000L);
                    startFlinkJob(ruleProcessVO);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("启动规则中断");
                }

            }
        });
        Result<Boolean> success = ResultUtil.success(true);
        return success;
    }

    public Boolean startFlinkJob(RuleProcessVO ruleProcessVO) {
        Boolean bool = true;
        String[] shellArray = getStartJobShells(ruleProcessVO);
        List<String> jobList = getJobList();
        String jobName = ruleProcessVO.getJobName();
        boolean judgeJobNameIsExist = judgeJobNameIsExist(jobName, jobList);
        logger.info("jobName是否存在：" + judgeJobNameIsExist);
        if (!judgeJobNameIsExist) {
            BooleanMessageVO booleanMessageVO = ShellExecuteScript.shellByResultArray(shellArray);
            saveRuleExecutionErrorInfo(ruleProcessVO, booleanMessageVO);
        } else {
            logger.info("judgeJobNameIsExist为：" + judgeJobNameIsExist);
        }

        return bool;
    }

    /**
     * @param ruleProcessVO
     * @param booleanMessageVO
     */
    private void saveRuleExecutionErrorInfo(RuleProcessVO ruleProcessVO, BooleanMessageVO booleanMessageVO) {
        if (!booleanMessageVO.isResult()) {
            FlinkRunningTimeErrorLog flinkRunningTimeErrorLog = new FlinkRunningTimeErrorLog();
            flinkRunningTimeErrorLog.setDataTime(new Date());
            flinkRunningTimeErrorLog.setExceptionType("starting"); //启动过程中报错
            flinkRunningTimeErrorLog.setLogInfo(booleanMessageVO.getErrorMessage());
            flinkRunningTimeErrorLog.setGuid(UUID.randomUUID().toString());
            flinkRunningTimeErrorLog.setRuleLevel(ruleProcessVO.getRuleLevel());
            flinkRunningTimeErrorLog.setRuleName(ruleProcessVO.getRuleName());
            flinkErrorLogService.save(flinkRunningTimeErrorLog);
        }
    }

    @Override
    public Result<Boolean> stopAlarmAnalysisEngine(String jobName) {

        //线程池执行
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                executeStopShell(jobName);
            }
        });
        Result<Boolean> success = ResultUtil.success(true);
        return success;

    }

    /**
     * 执行挺停止flink-job命令
     */
    private void executeStopShell(String jobName) {
        String flinkHomePath = flinkConfiguration.getFlink_home_path();
        String cmdPath = flinkHomePath + "/bin/flink" + " " + "list";
        boolean remoteFlag = flinkConfiguration.isRemote_flag();
        if (remoteFlag) {
            cmdPath = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + cmdPath;
        }
        String exeShell = cmdPath;
        List<String> list = ShellExecuteScript.queryExecuteCmd(exeShell);
        String jobId = SocUtil.getJobId(jobName, list);
        logger.info("jobId:" + jobId);
        stopShellCommand(jobId);
    }

    /**
     * 停止命令
     *
     * @param jobId
     */
    private void stopShellCommand(String jobId) {
        String flinkHomePath = flinkConfiguration.getFlink_home_path();
        boolean remoteFlag = flinkConfiguration.isRemote_flag();
        if (StringUtils.isNotEmpty(jobId)) {
            String cancelJobCommand = null;
            if (remoteFlag) {
                cancelJobCommand = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + flinkHomePath + "/bin/flink" + " cancel " + jobId;
            } else {
                cancelJobCommand = flinkHomePath + "/bin/flink" + " cancel " + jobId;
            }
            logger.info("cancel_job_command:" + cancelJobCommand);
            boolean result = ShellExecuteScript.executeShellByResult(cancelJobCommand);
            logger.info("cancel result:" + result);
        }
    }

    /**
     * 重启flink任务job
     *
     * @return
     */
    @Override
    public Result<Boolean> reStartAlarmAnalysisEngine(RuleProcessVO ruleProcessVO) {
        String[] shellArray = getStartJobShells(ruleProcessVO);
        //线程池执行
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                executeStopShell(ruleProcessVO.getJobName()); //停止
                ShellExecuteScript.executeShellByResultArray(shellArray); //启动
            }
        });
        Result<Boolean> success = ResultUtil.success(true);
        return success;

    }

    @Override
    public Result<List<EventLogTable>> getEventLogTable(String tagName) {
        List<EventLogTable> list = getEventTableByJdbc(tagName);
        Result<List<EventLogTable>> result = ResultUtil.success(list);
        return result;
    }

    /**
     * 通过JDBC获得事件表
     *
     * @param tagName
     * @return
     */
    private List<EventLogTable> getEventTableByJdbc(String tagName) {
        List<EventLogTable> list = new ArrayList<>();
        List<QueryCondition> conditions = new ArrayList<>();
        List<EventTable> eventtables = eventTabelService.getEventTables();
        if (StringUtils.isNotEmpty(tagName)) {
            eventtables = eventTabelService.getEventTableByTitle(tagName);
        }
        for (EventTable eventTable : eventtables) {
            EventLogTable eventLogTable = new EventLogTable();
            String label = eventTable.getLabel();
            String name = eventTable.getName();
            eventLogTable.setTableName(name);
            eventLogTable.setTag(label);
            eventLogTable.setType(JDBC_TYPE);
            list.add(eventLogTable);
        }
        return list;
    }

    @Override
    public Result<List<EventLogFieldVO>> getEventLogFieldVO(String sourceType, String tableName) {
        List<EventLogFieldVO> list = new ArrayList<>();
        if (sourceType.equals(LOGVO_TYPE)) {
            list = getEventLogFieldByLogVO(tableName);
        } else {
            list = getEventLogFieldByJdbcType(tableName);
        }
        return ResultUtil.success(list);
    }

    private List<EventLogFieldVO> getEventLogFieldByJdbcType(String tableName) {
        List<EventLogFieldVO> list = new ArrayList<>();

        EventTable eventTable = eventTabelService.getEventTableByName(tableName);
        if (eventTable != null) {
            String id = eventTable.getId();
            List<EventColumn> list2 = eventColumService.getEventColumnById(id);
            for (EventColumn eventColumn : list2) {
                EventLogFieldVO eventLogFieldVO = new EventLogFieldVO();
                String name = eventColumn.getName();
                String label = eventColumn.getLabel();
                eventLogFieldVO.setFieldCn(label);
                eventLogFieldVO.setFieldEn(name);
                list.add(eventLogFieldVO);
            }
        }
        return list;
    }

    /**
     * 根据LogVO获得对应的字段
     *
     * @param tableName
     */
    private List<EventLogFieldVO> getEventLogFieldByLogVO(String tableName) {
        List<EventLogFieldVO> list = new ArrayList<>();
        Set<Class<?>> set = PackageUtil.getClassPackage("com.vrv.logVO", LogDesc.class);
        for (Class<?> clazz : set) {
            LogDesc logDesc = clazz.getAnnotation(LogDesc.class);
            String logTableName = logDesc.tableName();
            if (logTableName.equals(tableName)) {
                //反射获得对应的字段值
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    //获得中文
                    EventLogFieldVO eventLogFieldVO = new EventLogFieldVO();
                    FieldDesc fieldDesc = field.getAnnotation(FieldDesc.class);
                    if (fieldDesc != null) {
                        String fieldDescCn = fieldDesc.value();
                        eventLogFieldVO.setFieldCn(fieldDescCn);
                    }
                    //获得英文
                    String filedNameEn = field.getName();
                    eventLogFieldVO.setFieldEn(filedNameEn);
                    list.add(eventLogFieldVO);
                }
            }
        }
        return list;
    }

    @Override
    public String getDynamicRelateSql(String tableName, List<RelateSqlVO> relateList) {
        String originalSql = "select * from " + tableName + " where 1=1";
        StringBuffer sqlsb = new StringBuffer(originalSql);
        if (relateList != null) {
            sqlsb.append(" and ");
            for (RelateSqlVO relateSqlVO : relateList) {
                String fieldName = relateSqlVO.getFieldName();
                String relate = relateSqlVO.getRelate();
                String value = relateSqlVO.getValue();
                String append = relateSqlVO.getAppend();
                if ("like".equals(relate)) {
                    value = "%" + value + "%";
                }
                sqlsb = sqlsb.append(fieldName).append(" ").append(relate).append(" ").append("'" + value + "'").append(" ");
                if (append != null) {
                    sqlsb.append(append).append(" ");
                }
            }
        }
        return sqlsb.toString();
    }

    @Override
    public String getOrginalLogPath(String tableName) {
        String orignalLogPath = null;
        Set<Class<?>> set = PackageUtil.getClassPackage("com.vrv.logVO", LogDesc.class);
        for (Class<?> clazz : set) {
            LogDesc logDesc = clazz.getAnnotation(LogDesc.class);
            String logTableName = logDesc.tableName();
            if (logTableName.equals(tableName)) {
                orignalLogPath = clazz.getName();
                break;
            }
        }
        return orignalLogPath;
    }

    @Override
    public void streamCalcucateSyncHandler() {
        List<QueryCondition> conditionList = new ArrayList<>();
        conditionList.add(QueryCondition.eq("started", "1"));
        conditionList.add(QueryCondition.eq("flag", RiskEventRule.FILTER));
        conditionList.add(QueryCondition.eq("deleteFlag", true));
        conditionList.add(QueryCondition.or(QueryCondition.isNull("allowStart"), QueryCondition.eq("allowStart", true)));
        List<RiskEventRule> streamList = findAll(conditionList);
        List<String> jobList = getJobList();
        logger.info("jobList：" + JSON.toJSONString(jobList));
        for (RiskEventRule riskEventRule : streamList) {
            modifyStreamCalculatorBySuccess(riskEventRule, jobList);
        }
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("status", true));
        queryConditions.add(QueryCondition.eq("operatorType", FilterOperator.ANALYSIS));
        queryConditions.add(QueryCondition.or(QueryCondition.isNull("allowStart"), QueryCondition.eq("allowStart", true)));
        queryConditions.add(QueryCondition.eq("deleteFlag", true));
        List<FilterOperator> filterOperatorList = filterOperatorService.findAll(queryConditions);
        for (FilterOperator filterOperator : filterOperatorList) {
            String jobName = filterOperator.getLabel() + " " + filterOperator.getCode();
            String jobId = SocUtil.getJobId(jobName, jobList);
            if (StringUtils.isEmpty(jobId)) {
                String[] shellArray = filterOperatorService.getExeShellArray(filterOperator);
                Boolean bool = ShellExecuteScript.executeShellByResultArray(shellArray);
                if (!bool) {
                    filterOperator.setAllowStart(false);
                    filterOperatorService.save(filterOperator);
                }
            }

        }

    }

    @Override
    public List<RiskEventRule> getAllRiskEventRule() {
        List<RiskEventRule> list = findAll();
        return list;
    }

    /**
     * 流计算规则状态成功,flink job任务却没有启动
     *
     * @param riskEventRule
     */
    private void modifyStreamCalculatorBySuccess(RiskEventRule riskEventRule, List<String> list) {
         String jobId = SocUtil.getJobId(riskEventRule.getJob_name(), list);
        logger.info("riskEventRule: " + JSON.toJSONString(riskEventRule));
        if (StringUtils.isNotEmpty(jobId)) { //说明流计算规则状态和flink job状态不匹配
            logger.info("jobId_1: " + jobId);
            RuleProcessVO ruleProcessVO = riskEventRule.structureVO();
            Boolean bool = startFlinkJob(ruleProcessVO);
            if (!bool) {
                riskEventRule.setAllowStart(false);
                save(riskEventRule);
            }
        }
    }

    private List<String> getJobList() {

        String flinkHomePath = flinkConfiguration.getFlink_home_path();

//        File file = new File(flinkHomePath);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }

        String cmdPath = flinkHomePath + "/bin/flink" + " " + "list";
        boolean remoteFlag = flinkConfiguration.isRemote_flag();
        if (remoteFlag) {
            cmdPath = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + cmdPath;
        }
        String exeShell = cmdPath;
        List<String> list = ShellExecuteScript.queryExecuteCmd(exeShell);
        return list;
    }


    private boolean judgeJobNameIsExist(String jobName, List<String> list) {
        boolean result = SocUtil.judgeJobNameIsExist(jobName, list);
        return result;
    }

    @Override
    public Result<String> exportRiskEventRule() {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("deleteFlag", 1));
        List<RiskEventRule> riskEventRuleList = findAll(conditions);
        ;
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSSSSSSS");
        String now = format.format(new Date());
        String fileName = "rule" + now + ".txt";
        String filePath = fileConfiguration.getFilePath() + File.separator + fileName;
        OutputStream os = null;
        try {
            String riskEventRuleStr = JSON.toJSONString(riskEventRuleList);
            riskEventRuleStr = AESUtil.encrypt(riskEventRuleStr, exportPasswd);
            byte[] data = riskEventRuleStr.getBytes();
            os = new FileOutputStream(filePath, true);
            os.write(data, 0, data.length);    //写入文件
            os.flush();    //将存储在管道中的数据强制刷新出去
        } catch (IOException e) {
            logger.error("规则导出失败！" + e.getMessage());
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "规则导出失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error("关闭输出流失败！" + e.getMessage());
                }
            }
        }
        return ResultUtil.success(fileName);

    }

    @Override
    public Result<Boolean> importRiskEventRuleInfo(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String str = new String(fileBytes);
            str = AESUtil.decrypt(str, exportPasswd);
            List<RiskEventRule> riskEventRuleList = new Gson().fromJson(str, new TypeToken<List<RiskEventRule>>() {
            }.getType());
            save(riskEventRuleList);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "规则导入失败");
        }
        return ResultUtil.success(true);

    }

    /**
     * 复制规则
     */
    @Override
    public Result<RiskRuleListVO> copyRiskEventRule(String guid) {
        RiskEventRule riskEventRule = getOne(guid);
        RiskEventRule riskEventRule1 = new RiskEventRule();
        mapper.copy(riskEventRule, riskEventRule1);
        riskEventRule1.setId(UUIDUtils.get32UUID());
        riskEventRule1.setName(riskEventRule.getName() + "副本_");
        riskEventRule1.setRuleCode(riskEventRule.getRuleCode() + "-" + UUIDUtils.get32UUID().substring(15));
        riskEventRule1.setType(0);
        riskEventRule1.setStarted("0");
        if ("editor".equals(riskEventRule.getFlag())) {
            riskEventRule1.setAnalysisId(UUIDUtils.get32UUID());
            filterOperatorService.copyFilter(riskEventRule1, riskEventRule.getAnalysisId());
        } else {
            riskEventRule.setJob_name(riskEventRule.getTableName() + " job" + UUIDUtils.get32UUID());
        }
        RiskRuleListVO riskRuleListVo1 = getRiskRuleListVO(riskEventRule1);
        return ResultUtil.success(riskRuleListVo1);
    }

    /**
     * 通过分析器id查询告警规则
     */
    @Override
    public RiskEventRule getRiskEventRuleByAnalysisId(String code) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("analysisId", code));
        List<RiskEventRule> riskEventRuleList = findAll(conditions);
        if (riskEventRuleList.size() == 1) {
            return riskEventRuleList.get(0);
        } else {
            return null;

        }
    }

    @Override
    public void autoStartThreatAnalysisEngine() {
        String[] exeShellArray = getThreatAnalysisCommand();
        executeThreatRule(exeShellArray);
    }

    /**
     * 执行风险分析规则
     *
     * @param exeShellArray
     */
    private void executeThreatRule(String[] exeShellArray) {
        List<String> jobList = getJobList();
        String jobId = SocUtil.getJobId(THRAT_ANALYSIS_JOB_NAME, jobList);
        if (StringUtils.isEmpty(jobId)) {
            cacheThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    ShellExecuteScript.executeShellByResultArray(exeShellArray);
                }
            });
        } else {
            logger.info("风险分析规则已启动！无需重复启动");
        }
    }

    /**
     * 获得风险分析命令
     *
     * @return
     */
    private String[] getThreatAnalysisCommand() {
        String[] exeShellArray = null;
        boolean remoteFlag = flinkConfiguration.isRemote_flag();
        if (remoteFlag) { //远程标识开启(需开启ssh免登陆)
            exeShellArray = new String[]{"ssh", flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip(), flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d", "-c", THRAT_ANALYSIS_MAIN_CLASS, flinkConfiguration.getFlink_jar_path()};
        } else {
            exeShellArray = new String[]{flinkConfiguration.getFlink_home_path() + "/bin/flink", "run","-d", "-c", THRAT_ANALYSIS_MAIN_CLASS, flinkConfiguration.getFlink_jar_path()};
        }
        return exeShellArray;
    }

    @Override
    public List<RunningTaskVO> getRunningTasks() {
        List<RunningTaskVO> runnings = new ArrayList<>();
        List<String> jobList = getJobList();
        List<String> runningJobs = SocUtil.getRunningJobs(jobList);

        for (String job : runningJobs) {
            RunningTaskVO runningTaskVO = new RunningTaskVO();
            String[] content = job.split(" : ");
            if (content.length == 3) {
                runningTaskVO.setRunningTime(content[0]);
                runningTaskVO.setJobID(content[1]);
                runningTaskVO.setJobName(content[2]);
                runnings.add(runningTaskVO);
            }
        }
        return runnings;
    }

    @Override
    public RiskRuleListVO createRuleInstance(Map<String, Object> map) {
        RiskRuleListVO riskRuleListVO = null;
        String guid = map.get("guid").toString();
        RiskEventRule riskEventRule = getOne(guid);
        String analysisId = riskEventRule.getAnalysisId();
        map.put("code", analysisId);
        FilterOpertorVO filterOperatorVO = filterOperatorService.createAnalyzerInstanceByCode(map);
        String newAnalysisId = filterOperatorVO.getCode();
        RiskEventRule riskEventRule1 = getRiskEventRuleByAnalysisId(newAnalysisId);
        if (riskEventRule1 != null) {
            riskRuleListVO = getRiskRuleListVO(riskEventRule1);
        }
        return riskRuleListVO;
    }

    @Override
    public RiskRuleListVO editRuleInstance(Map<String, Object> map) {
        RiskRuleListVO riskRuleListVO = null;
        String guid = map.get("guid").toString();
        RiskEventRule riskEventRule = getOne(guid);
        String analysisId = riskEventRule.getAnalysisId();
        map.put("code", analysisId);
        FilterOpertorVO filterOperatorVO = filterOperatorService.editAnalyzerInstanceByCode(map).getData();
        String newAnalysisId = filterOperatorVO.getCode();
        RiskEventRule riskEventRule1 = getRiskEventRuleByAnalysisId(newAnalysisId);
        if (riskEventRule1 != null) {
            riskRuleListVO = getRiskRuleListVO(riskEventRule1);
        }
        return riskRuleListVO;
    }

    @Override
    public Integer countStartRule() {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("started", true));
        queryConditions.add(QueryCondition.eq("deleteFlag", true));
        Long sum = count(queryConditions);
        return sum.intValue();
    }

    /**
     * 手动同步维表数据
     * @param syncRequest
     */
    @Override
    public boolean syncDimensionData(SyncRequest syncRequest) {
        // 获取筛选查询条件
        List<String> conditionSqls = AlarmDealUtil.initConditions(syncRequest.getConditions());
        logger.info("syncDimensionData conditionSqls={}",conditionSqls);
        // 查询数据
        List<DataRow> dataRows = new ArrayList<>();
        for(String sql : conditionSqls){
            String newSql = "select * from "+syncRequest.getDimensionTableName()+" where foreign_key_id = 'null' "+sql;
            List<Map<String,Object>> datas = jdbcTemplate.queryForList(newSql);
            List<DataRow> rows = getDataRowForMap(datas);
            dataRows.addAll(rows);
        }
        logger.warn("syncDimensionData dataRows size={}",dataRows.size());
        // 保存数据
        if(CollectionUtils.isNotEmpty(dataRows)){
            dataRows =dataRows.stream().distinct().collect(Collectors.toList());
            alarmAnalysisService.saveDimensionTableData(syncRequest.getDimensionTableName(), syncRequest.getRuleCode(), syncRequest.getFilterCode(), dataRows,true);
            // 清除缓存
            redissonSingleUtil.deleteByPrex(syncRequest.getDimensionTableName());
        }
        return true;
    }

    /**
     * 获取datarow
     * @param list
     * @return
     */
    public List<DataRow> getDataRowForMap(List<Map<String,Object>> list){
        List<DataRow> dataRows = new ArrayList<>();
        for(Map<String,Object> map : list){
            DataRow row = new DataRow();
            List<NameValueBean> cells = new ArrayList<>();
            for(Map.Entry<String,Object> entry : map.entrySet()){
                if (entry.getValue() != null && !"id".equals(entry.getKey()) && !"foreign_key_id".equals(entry.getKey()) && !"filter_code".equals(entry.getKey()) && !"rule_code".equals(entry.getKey())&& !"is_sync".equals(entry.getKey())) {
                    cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
                }
            }
            row.setRow(cells);
            dataRows.add(row);
        }
        return dataRows;
    }

    /**
     * 保存同步维表信息
     * @param syncRequest
     * @return
     */
    @Override
    public boolean saveSyncDimension(SyncRequest syncRequest){
        DimensionSync dimensionSync = new DimensionSync();
        dimensionSync.setGuid(UUIDUtils.get32UUID().toLowerCase(Locale.ROOT));
        dimensionSync.setRuleCode(syncRequest.getRuleCode());
        dimensionSync.setFilterCode(syncRequest.getFilterCode());
        dimensionSync.setDimensionTableName(syncRequest.getDimensionTableName());
        dimensionSync.setConditions(gson.toJson(syncRequest.getConditions()));
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("dimensionTableName",syncRequest.getDimensionTableName()));
        conditions.add(QueryCondition.eq("ruleCode",syncRequest.getRuleCode()));
        conditions.add(QueryCondition.eq("filterCode",syncRequest.getFilterCode()));
        List<DimensionSync> list = dimensionSyncService.findAll(conditions);
        dimensionSyncService.deleteInBatch(list);
        if(CollectionUtils.isNotEmpty(syncRequest.getConditions())){
            dimensionSyncService.save(dimensionSync);
        }
        return true;
    }

    @Override
    public List<SyncRes> querySyncDimension(SyncRequest syncRequest){
        List<SyncRes> result = new ArrayList<>();

        List<QueryCondition> conditions = new ArrayList<>();
        if(StringUtils.isNotEmpty(syncRequest.getRuleCode())){
            conditions.add(QueryCondition.eq("ruleCode",syncRequest.getRuleCode()));
        }
        if(StringUtils.isNotEmpty(syncRequest.getFilterCode())){
            conditions.add(QueryCondition.eq("filterCode",syncRequest.getFilterCode()));
        }
        if(StringUtils.isNotEmpty(syncRequest.getDimensionTableName())){
            conditions.add(QueryCondition.eq("dimensionTableName",syncRequest.getDimensionTableName()));
        }
        List<DimensionSync> dimensionSyncs = dimensionSyncService.findAll(conditions);

        if(CollectionUtils.isNotEmpty(dimensionSyncs)){
            for(DimensionSync dimensionSync : dimensionSyncs){
                SyncRes syncRes = new SyncRes();
                syncRes.setFilterCode(dimensionSync.getFilterCode());
                syncRes.setRuleCode(dimensionSync.getRuleCode());
                syncRes.setDimensionTableName(dimensionSync.getDimensionTableName());

                // 处理筛选条件列表
                String conditionStr = dimensionSync.getConditions();
                TypeToken<List<String>> typeToken = new TypeToken<List<String>>(){};
                List<String> list = gson.fromJson(conditionStr,typeToken.getType());
                List<List<FieldConditionBean>> fields = new ArrayList<>();
                for(String str : list){
                    TypeToken<List<FieldConditionBean>> fieldTypeToken = new TypeToken<List<FieldConditionBean>>(){};
                    List<FieldConditionBean> fieldConditionBeans =gson.fromJson(str,fieldTypeToken.getType());
                    fields.add(fieldConditionBeans);
                }
                syncRes.setList(fields);
                result.add(syncRes);
            }
        }

        return result;
    }
}
