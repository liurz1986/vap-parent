package com.vrv.vap.alarmdeal.business.analysis.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.RuleTypeConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.dao.EventCategoryDao;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.Attach;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.FilterSourceStatusService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.analysis.enums.ObjectResourceConst;
import com.vrv.vap.alarmdeal.business.analysis.enums.VersionConstant;
import com.vrv.vap.alarmdeal.business.analysis.job.FlinkTaskJob;
import com.vrv.vap.alarmdeal.business.analysis.model.DimensionTable;
import com.vrv.vap.alarmdeal.business.analysis.model.DimensionTableFieldVo;
import com.vrv.vap.alarmdeal.business.analysis.model.ObjectResource;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.*;
import com.vrv.vap.alarmdeal.business.analysis.repository.FilterOperatorRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.KafkaSenderService;
import com.vrv.vap.alarmdeal.business.analysis.vo.FilterOperatorGroupStartVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.FlinkStartVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.RiskRuleEditVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.*;
import com.vrv.vap.alarmdeal.business.asset.service.SystemConfigService;
import com.vrv.vap.alarmdeal.frameworks.config.FlinkConfiguration;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FilterOperator的service层
 *
 * @author wd-pc
 */
@Service
public class FilterOperatorService extends BaseServiceImpl<FilterOperator, String> {

    private static Logger logger = LoggerFactory.getLogger(FilterOperatorService.class);

    private static final String MAIN_CLASS = "com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction";


    @Autowired
    private FilterOperatorRespository filterOperatorRespository;


    @Autowired
    private MapperUtil mapper;

    @Autowired
    private FlinkConfiguration flinkConfiguration;

    @Autowired
    private RiskEventRuleService riskEventRuleService;

    @Autowired
    private ObjectResourceService objectResourceService;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private DimensionTableService dimensionTableService;

    @Autowired
    private DimensionTableFieldService dimensionTableFieldService;

    @Autowired
    private EventCategoryService eventCategoryService;

    @Autowired
    private EventCategoryDao eventCategoryDao;

    @Autowired
    private RuleFilterService ruleFilterService;

    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    @Autowired
    private FilterSourceStatusService filterSourceStatusService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FlinkTaskJob flinkTaskJob;
    @Autowired
    private SystemConfigService systemConfigService;

    private static final String STARTTYPE_DB = "datasource";

    @Value("${flink.solt_allot}")
    private Integer soltAllot;

    private Gson gson = new Gson();

    private ExecutorService cacheThreadPool = Executors.newCachedThreadPool();

    @Override
    public BaseRepository<FilterOperator, String> getRepository() {
        return filterOperatorRespository;
    }

    @Autowired
    private CommonFilterOperatorService commonFilterOperatorService;
    @Autowired
    private RedisUtil redisUtil;
    public static final String FLINK_TASK_REDIS_KEY = "flinkTaskMap";

    /**
     * 获取 规则映射信息
     *
     * @return
     */
    public Map<String, List<String>> getFlinkTaskMap() {
        Map<Object, Object> redisValue = redisUtil.hmget(FLINK_TASK_REDIS_KEY);
        if (redisValue == null) {
            return new HashMap<>();
        }
        for (Map.Entry<Object, Object> entry : redisValue.entrySet()) {
            Object value = entry.getValue();
            String json = gson.toJson(value);
            json = json.replace("\"", "");
            json = json.replace("\\", "");
            List<String> list = gson.fromJson(json, List.class);
            entry.setValue(list);
        }
        return (Map) redisValue;
    }

    public void initFlinkTaskMap() {
        redisUtil.del(FLINK_TASK_REDIS_KEY);
    }


    /**
     * 添加 规则策略映射信息
     *
     * @param taskId
     * @param ids
     */
    public void putFlinkTaskToMap(String taskId, List<String> ids) {
        Map<String, List<String>> flinkTaskMap = getFlinkTaskMap();
        if (flinkTaskMap.containsKey(taskId)) {
            List<String> sourceIds = flinkTaskMap.get(taskId);
            sourceIds.addAll(ids);
            List<String> sources = sourceIds.stream().distinct().collect(Collectors.toList());
            flinkTaskMap.put(taskId, sources);
        } else {
            ids = ids.stream().distinct().collect(Collectors.toList());
            flinkTaskMap.put(taskId, ids);
        }
        removeOtherKeyRepeatIds(taskId, ids, flinkTaskMap);
        redisUtil.hmset(FLINK_TASK_REDIS_KEY, (Map) flinkTaskMap);
    }

    /**
     * 清理其他分组里面可能存在的重复id,比如同一个rule_id在多个分组时不太合理的。
     *
     * @param taskId
     * @param ids
     * @param flinkTaskMap
     */
    private void removeOtherKeyRepeatIds(String taskId, List<String> ids, Map<String, List<String>> flinkTaskMap) {
        for (Map.Entry<String, List<String>> entry : flinkTaskMap.entrySet()) {
            if (entry.getKey().equals(taskId)) {
                continue;
            }
            List<String> idList = entry.getValue();
            for (String id : ids) {
                idList.remove(id);
            }
            entry.setValue(idList);
        }
    }


    /**
     * 删除 规则策略映射信息
     *
     * @param taskId
     * @param id
     */
    public void removeFlinkTaskForMap(String taskId, String id) {
        Map<String, List<String>> flinkTaskMap = getFlinkTaskMap();
        if (flinkTaskMap.containsKey(taskId)) {
            List<String> ids = flinkTaskMap.get(taskId);
            List<String> newIds = new CopyOnWriteArrayList<>();
            for (String guid : ids) {
                if (!id.equals(guid)) {
                    newIds.add(guid);
                }
            }
            flinkTaskMap.put(taskId, newIds);
        } else {
            List<String> keys = new CopyOnWriteArrayList<>();
            flinkTaskMap.forEach((key, value) -> {
                if (key.contains(taskId)) {
                    keys.add(key);
                }
            });

            for (String key : keys) {
                List<String> ids = new CopyOnWriteArrayList<>();
                List<String> ids1 = flinkTaskMap.get(key);
                if (CollectionUtils.isNotEmpty(ids) && ids != null) {
                    //原来的
                    ids.addAll(ids1);
                    Iterator<String> it = ids.iterator();
                    while (it.hasNext()) {
                        if (it.next().equals(id)) {
                            //移除当前的
                            it.remove();
                        }
                    }
                    flinkTaskMap.put(key, ids);
                }
//                ids.remove(id);
            }
        }
        redisUtil.hmset(FLINK_TASK_REDIS_KEY, (Map) flinkTaskMap);
    }

    /**
     * 移除flinkTaskap，根据ruleId，停止flinkjob后一定要保证停止的策略id从flinkTaskMap中移除。避免循环中continie的干扰。
     *
     * @param ruleId 策略id
     */
    public void removeFlinkTaskMapByRuleId(String ruleId) {
        Map<String, List<String>> flinkTaskMap = getFlinkTaskMap();
        for (Map.Entry<String, List<String>> entry : flinkTaskMap.entrySet()) {
            List<String> value = entry.getValue();
            if (value.contains(ruleId)) {
                value.remove(ruleId);
            }
            entry.setValue(value);
        }
        redisUtil.hmset(FLINK_TASK_REDIS_KEY, (Map) flinkTaskMap);
    }

    /**
     * 添加过滤器
     *
     * @param filterOpertorVO
     * @return
     */
    public Result<FilterOpertorVO> addFilterOperator(FilterOpertorVO filterOpertorVO) {
        // 保存时生成随机码
        String uuid = UUIDUtils.get32UUID();
        String code = UUIDUtils.get32UUID();
        Date createTime = new Date();

        // 对FilterOperator当中的column进行自动排序
        filterOpertorVO = orderColumnByFilterOperator(filterOpertorVO);

        // 处理ruleType与filterConfigTemplate
        String ruleType = "";
        String filterConfig = new Gson().toJson(filterOpertorVO.getFilterConfig());
        String filterConfigTemplate = null;
        List<ParamConfigVO> paramConfigVOList = filterOpertorVO.getParamConfig();
        if (paramConfigVOList != null) {
            if (paramConfigVOList.size() > 0) {
                ruleType = FilterOperator.MODEL;
                filterOpertorVO.setFilterConfigTemplate(filterOpertorVO.getFilterConfig());
                filterOpertorVO.setStatus(false);
                filterConfigTemplate = filterConfig;
            }
        } else {
            ruleType = FilterOperator.RULE;
        }

        // 获取版本
        String multiVersions = getMultiVersions(filterOpertorVO, code, 1);

        // 保存规则对象
        FilterOperator filterOperator = getFilterOperatorObj(uuid, code, filterOpertorVO, ruleType, filterConfigTemplate, multiVersions);
        save(filterOperator);
        // 配置返回信息
        saveFilterOperatorVoObj(filterOpertorVO, ruleType, uuid, code, multiVersions, createTime);
        return ResultUtil.success(filterOpertorVO);
    }

    /**
     * 配置返回规则信息
     *
     * @param filterOpertorVO
     * @param ruleType
     * @param uuid
     * @param code
     * @param multiVersions
     * @param createTime
     */
    public void saveFilterOperatorVoObj(FilterOpertorVO filterOpertorVO, String ruleType, String uuid, String code, String multiVersions, Date createTime) {
        filterOpertorVO.setRuleType(ruleType);
        filterOpertorVO.setGuid(uuid);
        filterOpertorVO.setCode(code);
        filterOpertorVO.setOutFieldInfos(this.getOutFiledInfos(filterOpertorVO));
        filterOpertorVO.setMultiVersion(multiVersions);
        filterOpertorVO.setVersion(1);
        filterOpertorVO.setDeleteFlag(true);
        filterOpertorVO.setCreateTime(createTime);
        filterOpertorVO.setInitStatus("0");
    }

    /**
     * 保存时构建规则对象
     *
     * @param uuid
     * @param code
     * @param filterOpertorVO
     * @param ruleType
     * @param filterConfigTemplate
     * @param multiVersions
     * @return
     */
    public FilterOperator getFilterOperatorObj(String uuid, String code, FilterOpertorVO filterOpertorVO, String ruleType, String filterConfigTemplate, String multiVersions) {
        FilterOperator filterOperator = new FilterOperator();
        filterOperator.setName(StringUtils.isNotBlank(filterOpertorVO.getName()) ? filterOpertorVO.getName() : filterOpertorVO.getLabel());
        filterOperator.setCode(code);
        filterOperator.setGuid(uuid);
        filterOperator.setDeleteFlag(true);
        filterOperator.setVersion(1);
        filterOperator.setOutFieldInfos(gson.toJson(this.getOutFiledInfos(filterOpertorVO)));
        filterOperator.setFilterConfig(gson.toJson(filterOpertorVO.getFilterConfig()));
        filterOperator.setSourceIds(gson.toJson(filterOpertorVO.getSourceIds()));
        filterOperator.setDependencies(gson.toJson(filterOpertorVO.getDependencies()));
        List<Outputs> outputs = getAddOutputs(filterOpertorVO);
        filterOperator.setOutputs(gson.toJson(outputs));
        filterOperator.setOperatorType(filterOpertorVO.getOperatorType());
        filterOperator.setMultiVersion(multiVersions);
        filterOperator.setDesc(filterOpertorVO.getDesc());
        filterOperator.setLabel(filterOpertorVO.getLabel());
        filterOperator.setCreateTime(new Date());
        filterOperator.setRoomType(filterOpertorVO.getRoomType());
        filterOperator.setStatus(false);
        if (filterOpertorVO.getParamConfig() != null) {
            filterOperator.setParamConfig(gson.toJson(filterOpertorVO.getParamConfig()));
        }
        if (filterConfigTemplate != null) {
            filterOperator.setFilterConfigTemplate(filterConfigTemplate);
        }
        filterOperator.setRuleType(ruleType);
        filterOperator.setNewlineFlag(filterOpertorVO.getNewlineFlag());
        filterOperator.setFilterType(filterOpertorVO.getFilterType());
        filterOperator.setRuleFilterType(filterOpertorVO.getRuleFilterType());
        filterOperator.setInitStatus("0");

        // 处置建议、描述
        filterOperator.setAttackLine(filterOpertorVO.getAttackLine());
        filterOperator.setThreatCredibility(filterOpertorVO.getThreatCredibility());
        filterOperator.setDealAdvcie(filterOpertorVO.getDealAdvcie());
        filterOperator.setHarm(filterOpertorVO.getHarm());
        filterOperator.setPrinciple(filterOpertorVO.getPrinciple());
        filterOperator.setViolationScenario(filterOpertorVO.getViolationScenario());
        filterOperator.setFilterDesc(filterOpertorVO.getFilterDesc());

        return filterOperator;
    }

    /**
     * 获得综合版本号
     *
     * @param filterOpertorVO
     * @param code
     * @return
     */
    private String getMultiVersions(FilterOpertorVO filterOpertorVO, String code, Integer version) {
        List<Dependencies> list = filterOpertorVO.getDependencies();
        List<MultiVersion> multiLists = new ArrayList<>();
        for (Dependencies dependency : list) {
            MultiVersion multiVersion = new MultiVersion();
            multiVersion.setCode(dependency.getGuid());
            multiVersion.setName(dependency.getName());
            multiVersion.setType(dependency.getType());
            multiVersion.setVersion(Integer.valueOf(dependency.getVersion()));
            multiLists.add(multiVersion);
        }

        MultiVersion selfMultiVersion = new MultiVersion();
        selfMultiVersion.setName(filterOpertorVO.getName());
        selfMultiVersion.setType(VersionConstant.SELF);
        selfMultiVersion.setCode(code);
        selfMultiVersion.setVersion(version);
        multiLists.add(selfMultiVersion);

        String operatorType = filterOpertorVO.getOperatorType();
        if (operatorType.equals("analysis")) {
            MultiVersion ideMultiVersion = new MultiVersion();
            ideMultiVersion.setName(filterOpertorVO.getName());
            ideMultiVersion.setType(VersionConstant.IDEVERSION);
            ideMultiVersion.setCode(code);
            ideMultiVersion.setIdeVersion(filterOpertorVO.getIdeVersion());
            multiLists.add(ideMultiVersion);
        }
        String json = gson.toJson(multiLists);
        return json;

    }

    /*
     * 复制分析器
     * */
    public FilterOperator copyFilter(RiskEventRule riskEventRule, String analysisId) {
        List<FilterOperator> filterOperatorList = getFilterOperators(analysisId);
        if (filterOperatorList.size() == 1) {
            FilterOperator filterOperator = filterOperatorList.get(0);
            FilterOperator filterOperator1 = new FilterOperator();
            mapper.copy(filterOperator, filterOperator1);
            filterOperator1.setGuid(UUIDUtils.get32UUID());
            filterOperator1.setStatus(false);
            filterOperator1.setVersion(1);
            filterOperator1.setCode(analysisId);
            filterOperator1.setName(filterOperator.getName() + "副本_");
            filterOperator1.setLabel(filterOperator.getLabel());
            FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator1);
            filterOperator1.setOperatorType(filterOpertorVO.getOperatorType());
            String multiVersions = getMultiVersions(filterOpertorVO, analysisId, 1);
            filterOperator1.setMultiVersion(multiVersions);
            filterOperator1.setCreateTime(new Date());
            List<Outputs> outputsList = gson.fromJson(filterOperator.getOutputs(), new TypeToken<List<Outputs>>() {
            }.getType());
            String jobName = getJobName(filterOpertorVO);
            for (Outputs output : outputsList) {
                if (output.getType().equals("alarmdeal")) {
                    riskEventRule.setJob_name(jobName);
                    AlarmObj alarmObj = output.getConfig().getAlarmObj();
                    alarmObj.setRiskEventRule(riskEventRule);
                    RiskRuleEditVO riskRuleEditVO = mapper.map(riskEventRule, RiskRuleEditVO.class);
                    alarmObj.setRiskRuleEditVO(riskRuleEditVO);
                }
            }
            filterOperator1.setOutputs(gson.toJson(outputsList));
            save(filterOperator1);
            return filterOperator1;
        } else {
            throw new RuntimeException("分析器不存在，code：" + analysisId);
        }

    }

    /**
     * 分析器
     *
     * @param guid
     * @return
     */
    public FilterOpertorVO copyByGuid(String guid) {
        FilterOperator filterOperator = getOne(guid);
        FilterOpertorVO filterOpertorVO = copyFilterOperate(filterOperator);
        return filterOpertorVO;
    }

    /**
     * 复制规则信息
     *
     * @param filterOperator
     * @return
     */
    private FilterOpertorVO copyFilterOperate(FilterOperator filterOperator) {
        // 复制规则对象
        FilterOperator filterOperator1 = new FilterOperator();
        mapper.copy(filterOperator, filterOperator1);

        // 保存复制的规则相对
        saveCopyFilterObj(filterOperator1, filterOperator);
        save(filterOperator1);
        return getFilterOperatorVO(filterOperator1);
    }

    /**
     * 处理复制对象
     *
     * @param filterOperator1
     * @param filterOperator
     */
    public void saveCopyFilterObj(FilterOperator filterOperator1, FilterOperator filterOperator) {
        String analysisId = UUIDUtils.get32UUID();
        String guid = UUIDUtils.get32UUID();
        filterOperator1.setGuid(guid);
        filterOperator1.setStatus(filterOperator.isStatus());
        filterOperator1.setVersion(filterOperator.getVersion());
        filterOperator1.setCode(analysisId);
        filterOperator1.setName(filterOperator.getLabel() + "_副本");
        filterOperator1.setLabel(filterOperator.getLabel() + "_副本");
        FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator1);
        filterOperator1.setOperatorType(filterOpertorVO.getOperatorType());
        String multiVersions = getMultiVersions(filterOpertorVO, analysisId, 1);
        filterOperator1.setMultiVersion(multiVersions);
        filterOperator1.setCreateTime(new Date());
        filterOperator1.setUpdateTime(null);
        filterOperator1.setInitStatus("0");
        List<Outputs> outputsList = gson.fromJson(filterOperator.getOutputs(), new TypeToken<List<Outputs>>() {
        }.getType());
        filterOperator1.setOutputs(gson.toJson(outputsList));
    }

    /**
     * 获得关联规则以后的输出
     *
     * @param filterOpertorVO
     */
    private List<Outputs> getAddOutputs(FilterOpertorVO filterOpertorVO) {
        List<Outputs> outputs = filterOpertorVO.getOutputs();
        return outputs;
    }

    /**
     * 规则任务名称
     *
     * @param filterOpertorVO
     * @return
     */
    private String getJobName(FilterOpertorVO filterOpertorVO) {
        String lable = filterOpertorVO.getLabel();
        String analysisId = filterOpertorVO.getCode();
        String jobName = lable + " " + analysisId;
        return jobName;
    }

    /**
     * 获得关联规则以后的输出
     *
     * @param filterOpertorVO
     */
    private List<Outputs> getEditOutputs(FilterOpertorVO filterOpertorVO) {
        List<Outputs> outputs = filterOpertorVO.getOutputs();
        return outputs;
    }

    /**
     * 修改name
     *
     * @param filterOpertorVO
     * @return
     */
    public Result<FilterOpertorVO> editFilterOperatorName(FilterOpertorVO filterOpertorVO) {
        String guid = filterOpertorVO.getGuid();
        FilterOperator filterOperator = getOne(guid);
        String name = filterOpertorVO.getName();
        String label = filterOpertorVO.getLabel();
        String desc = filterOpertorVO.getDesc();
        String roomType = filterOpertorVO.getRoomType();
        filterOperator.setName(name);
        filterOperator.setLabel(label);
        filterOperator.setDesc(desc);
        filterOperator.setRoomType(roomType);
        save(filterOperator);
        return ResultUtil.success(filterOpertorVO);
    }

    /**
     * 编辑过滤器
     *
     * @param filterOpertorVO
     * @return
     */
    @Transactional
    public Result<FilterOpertorVO> editFilterOperator(FilterOpertorVO filterOpertorVO) {
        // 1、通过id 查看规则是否被启动的策略管理
        // 获取规则ID
        String guid = filterOpertorVO.getGuid();
        judgeFilterStatus(filterOpertorVO);
        //对字段进行排序
        filterOpertorVO = orderColumnByFilterOperator(filterOpertorVO);
        // 2、复制新对象
        FilterOperator newFilterOperator = new FilterOperator();
        FilterOperator filterOperator = getOne(guid);
        filterOperator.setDeleteFlag(false);  //原先的逻辑删除
        save(filterOperator);
        mapper.copy(filterOperator, newFilterOperator);
        String uuid = UUIDUtils.get32UUID();
        String code = filterOperator.getCode();
        editCopyFilter(newFilterOperator, filterOpertorVO, filterOperator, uuid, code);

        save(newFilterOperator);
        // 修改规则后，策略重启
        // reStartRule(newFilterOperator);
        /*********************过滤器关联分析器发生变化***************************/
        // changeAnalysisOperatorByFilter(newFilterOperator);
        FilterOpertorVO filterOpertorVO1 = getFilterVo(newFilterOperator);
        return ResultUtil.success(filterOpertorVO1);
    }

    /**
     * 设置规则返回信息
     *
     * @param newFilterOperator
     */
    public FilterOpertorVO getFilterVo(FilterOperator newFilterOperator) {
        Date updateTime = new Date();
        FilterOpertorVO filterOpertorVO = getFilterOperatorVO(newFilterOperator);
        // filterOpertorVO = orderColumnByFilterOperator(filterOpertorVO);
        filterOpertorVO.setUpdateTime(updateTime);
        //分析器模板关联实例
        setFilterOperatorChildren(filterOpertorVO);
        return filterOpertorVO;
    }

    /**
     * 处理编辑复制规则对象
     *
     * @param newFilterOperator
     * @param filterOpertorVO
     * @param filterOperator
     * @param uuid
     * @param code
     */
    public void editCopyFilter(FilterOperator newFilterOperator, FilterOpertorVO filterOpertorVO, FilterOperator filterOperator, String uuid, String code) {
        String name = filterOpertorVO.getName();
        newFilterOperator.setName(StringUtils.isNotBlank(name) ? name : filterOpertorVO.getLabel());
        newFilterOperator.setVersion(filterOperator.getVersion());  //老版本
        newFilterOperator.setOutFieldInfos(gson.toJson(this.getOutFiledInfos(filterOpertorVO)));
        newFilterOperator.setFilterConfig(gson.toJson(filterOpertorVO.getFilterConfig()));
        newFilterOperator.setSourceIds(gson.toJson(filterOpertorVO.getSourceIds()));
        newFilterOperator.setDependencies(gson.toJson(filterOpertorVO.getDependencies()));
        newFilterOperator.setAllowStart(true); //允许启动
        List<Outputs> outputs = getEditOutputs(filterOpertorVO);
        newFilterOperator.setOutputs(gson.toJson(outputs));
        newFilterOperator.setOperatorType(filterOpertorVO.getOperatorType());
        newFilterOperator.setInitStatus(filterOpertorVO.getInitStatus());
        newFilterOperator.setGuid(uuid);
        Integer version = filterOperator.getVersion();
        String multiVersions = getMultiVersions(filterOpertorVO, code, version + 1);

        newFilterOperator.setDeleteFlag(true);
        newFilterOperator.setDesc(filterOpertorVO.getDesc());
        newFilterOperator.setMultiVersion(multiVersions);
        newFilterOperator.setVersion(newFilterOperator.getVersion() + 1);
        newFilterOperator.setUpdateTime(new Date());
        newFilterOperator.setStatus(false);
        newFilterOperator.setLabel(filterOpertorVO.getLabel());
        newFilterOperator.setFilterType(filterOpertorVO.getFilterType());
        newFilterOperator.setRuleFilterType(filterOpertorVO.getRuleFilterType());
        if (StringUtils.isNotEmpty(newFilterOperator.getRuleType()) && newFilterOperator.getRuleType().equals(FilterOperator.MODEL)) {
            String filterConfig = new Gson().toJson(filterOpertorVO.getFilterConfig());
            newFilterOperator.setFilterConfigTemplate(filterConfig);
            newFilterOperator.setParamConfig(gson.toJson(filterOpertorVO.getParamConfig()));
        }
        newFilterOperator.setNewlineFlag(filterOpertorVO.getNewlineFlag());
        List<Outputs> outputsList = outputs.stream().filter(output -> output.getType().equals("alarmdeal")).collect(Collectors.toList());
        if (outputsList.size() > 0) {
            if (!(StringUtils.isNotEmpty(filterOperator.getRuleType()) && filterOperator.getRuleType().equals(FilterOperator.MODEL))) {
                newFilterOperator.setStatus(true);
            }
        }

        // 处置建议、描述
        newFilterOperator.setAttackLine(filterOpertorVO.getAttackLine());
        newFilterOperator.setThreatCredibility(filterOpertorVO.getThreatCredibility());
        newFilterOperator.setDealAdvcie(filterOpertorVO.getDealAdvcie());
        newFilterOperator.setHarm(filterOpertorVO.getHarm());
        newFilterOperator.setPrinciple(filterOpertorVO.getPrinciple());
        newFilterOperator.setViolationScenario(filterOpertorVO.getViolationScenario());
        newFilterOperator.setFilterDesc(filterOpertorVO.getFilterDesc());
    }

    /**
     * 判断规则状态
     *
     * @param filterOpertorVO
     */
    private void judgeFilterStatus(FilterOpertorVO filterOpertorVO) {
        List<QueryCondition> ruleFilterParam = new ArrayList<>();
        ruleFilterParam.add(QueryCondition.eq("filterCode", filterOpertorVO.getCode()));
        ruleFilterParam.add(QueryCondition.eq("isStarted", "1"));
        // 查询关联的策略
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterParam);
        // 获取策略id
        List<String> ruleFilterCodes = ruleFilters.stream().map(RuleFilter::getRuleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ruleFilterCodes)) {
            // 通过策略ID 查询启动的策略
            List<QueryCondition> riskEventParams = new ArrayList<>();
            riskEventParams.add(QueryCondition.in("id", ruleFilterCodes));
            riskEventParams.add(QueryCondition.eq("started", "1"));
            riskEventParams.add(QueryCondition.eq("deleteFlag", true));
            List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(riskEventParams);

            // 如果存在开启的策略，则不能修改
            if (CollectionUtils.isNotEmpty(riskEventRuleList)) {
                throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "改规则被开启的策略引用，无法修改！");
            }
        }
    }

    /**
     * 设置下级规则
     *
     * @param filterOpertorVO
     */
    private void setFilterOperatorChildren(FilterOpertorVO filterOpertorVO) {
        if (StringUtils.isNotEmpty(filterOpertorVO.getRuleType()) && filterOpertorVO.getRuleType().equals(FilterOperator.MODEL)) {
            List<FilterOpertorVO> childList = getFilterModelChildren(filterOpertorVO.getCode());
            filterOpertorVO.setChildren(childList);
        }
    }

    /**
     * 根据过滤器修改分析器
     *
     * @param newFilterOperator
     */
    public void changeAnalysisOperatorByFilter(FilterOperator newFilterOperator) {
        String operatorType = newFilterOperator.getOperatorType();
        if (operatorType.equals("filter")) {
            String code = newFilterOperator.getCode();
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("operatorType", "analysis"));
            conditions.add(QueryCondition.eq("deleteFlag", true));
            conditions.add(QueryCondition.like("multiVersion", code));
            List<FilterOperator> analysisorList = findAll(conditions);
            for (FilterOperator analysisor : analysisorList) {
                String multiVersion = analysisor.getMultiVersion();
                if (StringUtils.isNotEmpty(multiVersion)) {
                    List<MultiVersion> multiList = new Gson().fromJson(multiVersion, new TypeToken<List<MultiVersion>>() {
                    }.getType());
                    for (MultiVersion multi : multiList) {
                        if (code.equals(multi.getCode())) {
                            Integer version = newFilterOperator.getVersion();
                            multi.setVersion(version);
                            break;
                        }
                    }
                    analysisor.setMultiVersion(this.gson.toJson(multiList));
                }
            }
            if (analysisorList.size() != 0) {
                save(analysisorList);
            }
        } else {
            List<FilterOperator> analysisorList = new ArrayList<>();
            String filterConfigTemplate = newFilterOperator.getFilterConfigTemplate();
            if (StringUtils.isNotEmpty(filterConfigTemplate)) {
                Boolean paramChanged = false;
                List<FilterOperator> filterOperatorList = getFilterOperatorInstances(newFilterOperator.getCode());
                for (FilterOperator filterOperator1 : filterOperatorList) {
                    String modelParamConfigStr = newFilterOperator.getParamConfig();
                    List<ParamConfigVO> modelParamConfigList = (new Gson()).fromJson(modelParamConfigStr, new TypeToken<List<ParamConfigVO>>() {
                    }.getType());
                    List<String> modelKeyList = modelParamConfigList.stream().map(item -> item.toString()).collect(Collectors.toList());
                    String instanceParamConfigStr = filterOperator1.getParamConfig();
                    List<ParamConfigVO> instanceParamConfigList = (new Gson()).fromJson(instanceParamConfigStr, new TypeToken<List<ParamConfigVO>>() {
                    }.getType());
                    List<String> instanceKeyList = instanceParamConfigList.stream().map(item -> item.toString()).collect(Collectors.toList());
                    Map<String, Object> paramValueMap = new Gson().fromJson(filterOperator1.getParamValue(), Map.class);
                    if (instanceKeyList.size() == modelKeyList.size()) {
                        for (String key : modelKeyList) {
                            String[] keyStr = key.split(",");
                            if (!instanceKeyList.contains(key) || !paramValueMap.containsKey(keyStr[1])) {
                                updateFilterOperator(filterOperator1, "0");
                                filterOperator1.setTag(FilterOperator.FORBID);
                                paramChanged = true;
                                break;
                            } else {
                                filterOperator1.setFilterConfigTemplate(filterConfigTemplate);
                                filterOperator1.setParamConfig(newFilterOperator.getParamConfig());
                                //占位符重新替换
                                editAnalyzerInstanceOnly(paramValueMap, filterOperator1);
                            }
                        }
                    } else {
                        filterOperator1.setTag(FilterOperator.FORBID);
                        updateFilterOperator(filterOperator1, "0");
                        paramChanged = true;
                    }
                    filterOperator1.setFilterConfigTemplate(filterConfigTemplate);
                    filterOperator1.setParamConfig(newFilterOperator.getParamConfig());
                    analysisorList.add(filterOperator1);
                }
                if (paramChanged.booleanValue()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", newFilterOperator.getCode());
                    map.put("paramConfig", newFilterOperator.getParamConfig());
                    map.put("updateTime", DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN));
                    kafkaSenderService.send("alarmRuleStopedEvent", JSON.toJSONString(map));
                }
            } else {
                analysisorList.add(newFilterOperator);
            }
        }
    }

    /**
     * 通过code查询未被删除的规则
     *
     * @param code
     * @return
     */
    public List<FilterOperator> getFilterOperators(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("code", code));
        queryConditions.add(QueryCondition.eq("deleteFlag", true));
        return findAll(queryConditions);
    }

    /**
     * 通过modelId 获取未被删除的规则
     *
     * @param code
     * @return
     */
    public List<FilterOperator> getFilterOperatorInstances(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("modelId", code));
        queryConditions.add(QueryCondition.eq("deleteFlag", true));
        return findAll(queryConditions);
    }

    /**
     * 对FilterOperator当中的column进行自动排序
     *
     * @return
     */
    private FilterOpertorVO orderColumnByFilterOperator(FilterOpertorVO filterOpertorVO) {
        FilterConfigObject filterConfig = filterOpertorVO.getFilterConfig();
        Tables[][] tables = filterConfig.getTables();
        for (int i = 0; i < tables.length; i++) {
            for (int j = 0; j < tables[i].length; j++) {
                Tables table = tables[i][j];
                if (table != null) {
                    List<Column> column = table.getColumn();
                    Collections.sort(column, Comparator.comparing(Column::getOrder));
                }
            }
        }
        filterOpertorVO.setFilterConfig(filterConfig);
        return filterOpertorVO;
    }

    /**
     * 获得对应的输出结构
     *
     * @param filterOpertorVO
     * @return
     */
    private List<OutFieldInfo> getOutFiledInfos(FilterOpertorVO filterOpertorVO) {
        FilterConfigObject filterConfig = filterOpertorVO.getFilterConfig();
        Tables[][] tableArray = filterConfig.getTables();
        Tables lastTable = null;
        for (int j = 0; j < tableArray[0].length; j++) {
            lastTable = tableArray[0][j];
        }
        if (lastTable != null) {
            List<OutFieldInfo> list = new ArrayList<>();
            List<Column> columns = lastTable.getColumn();
            for (Column column : columns) {
                OutFieldInfo outFieldInfo = new OutFieldInfo();
                outFieldInfo.setFieldName(column.getName());
                outFieldInfo.setOrder(column.getOrder());
                outFieldInfo.setFieldType(column.getDataType());
                outFieldInfo.setFieldLabel(column.getLabel());
                list.add(outFieldInfo);
            }
            String operatorType = filterOpertorVO.getOperatorType();
            if (operatorType.equals("analysis")) {
                OutFieldInfo outFieldInfo1 = getRoomField(filterOpertorVO, columns);
                list.add(outFieldInfo1);
            }
            return list;
        } else {
            throw new RuntimeException("没有对应的table，请检查！");
        }
    }

    /**
     * 获得盒子类型对象
     *
     * @param filterOpertorVO
     * @param columns
     * @return
     */
    private OutFieldInfo getRoomField(FilterOpertorVO filterOpertorVO, List<Column> columns) {
        int order = columns.size();
        String roomType = filterOpertorVO.getRoomType();
        OutFieldInfo outFieldInfo1 = new OutFieldInfo();
        if (roomType.equals("timeRoom")) {
            outFieldInfo1 = getRoomInfo(order, "timeRoom", "mapMap");
        } else {
            outFieldInfo1 = getRoomInfo(order, "idRoom", "mapArray");
        }
        return outFieldInfo1;
    }

    /**
     * 获得对应的roomInfo相关信息
     *
     * @param order
     * @return
     */
    private OutFieldInfo getRoomInfo(Integer order, String roomType, String fieldType) {
        OutFieldInfo outFieldInfo1 = new OutFieldInfo();
        outFieldInfo1.setFieldName(roomType);
        outFieldInfo1.setFieldType(fieldType);
        outFieldInfo1.setOrder(order);
        if (roomType.equals("timeRoom")) {
            outFieldInfo1.setFieldLabel("时间盒子");
        } else {
            outFieldInfo1.setFieldLabel("ID盒子");
        }
        return outFieldInfo1;
    }

    /**
     * 逻辑删除告警过滤器的信息
     *
     * @param filterOpertorVO
     * @return
     */
    @Transactional
    public Result<Boolean> deleteFilterOperator(FilterOpertorVO filterOpertorVO) {
        String guid = filterOpertorVO.getGuid();
        FilterOperator filterOperator = getOne(guid);
        String code = filterOperator.getCode();
        List<QueryCondition> ruleFilterParams = new ArrayList<>();
        ruleFilterParams.add(QueryCondition.eq("filterCode", code));
        ruleFilterParams.add(QueryCondition.eq("isStarted", "1"));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterParams);
        List<String> ruleCodes = ruleFilters.stream().map(RuleFilter::getRuleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ruleCodes)) {
            deleteFilter(filterOperator);
            return ResultUtil.success(true);
        }
        List<QueryCondition> riskParams = new ArrayList<>();
        riskParams.add(QueryCondition.in("id", ruleCodes));
        riskParams.add(QueryCondition.eq("deleteFlag", true));
        riskParams.add(QueryCondition.eq("started", "1"));
        List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(riskParams);
        if (CollectionUtils.isNotEmpty(riskEventRules)) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "规则被启用策略所引用，无法删除！");
        }

        deleteFilter(filterOperator);
        return ResultUtil.success(true);
    }

    public void deleteFilter(FilterOperator filterOperator) {
        String filterConfigTemplate = filterOperator.getFilterConfigTemplate();
        if (StringUtils.isNotEmpty(filterConfigTemplate)) {
            FilterOperator filterOperator1 = new FilterOperator();
            filterOperator1.setStatus(true);
            filterOperator1.setModelId(filterOperator.getCode());
            Boolean isExist = exists(filterOperator1);
            if (isExist.booleanValue()) {
                throw new RuntimeException("删除失败，存在启动的分析器实例");
            }
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("modelId", filterOperator.getCode()));
            List<FilterOperator> filterOperatorList = findAll(conditions);
            this.filterOperatorRespository.deleteInBatch(filterOperatorList);
            deleteFilterOperator(filterOperator);
        } else {
            deleteFilterOperator(filterOperator);
        }
    }

    /**
     * 逻辑删除规则
     *
     * @param filterOperator
     */
    private void deleteFilterOperator(FilterOperator filterOperator) {
        filterOperator.setDeleteFlag(false);
        save(filterOperator);
    }

    /**
     * 查询对应的FilterOpertorVO
     *
     * @return
     */
    public FilterOpertorVO getFilterOpertorVOByAnlysisId(String analysisId) {
        List<FilterOperator> analysisiors = getFilterOperators(analysisId);
        if (analysisiors.size() == 1) {
            FilterOperator filterOperator = analysisiors.get(0);
            FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
            return filterOpertorVO;
        } else {
            throw new RuntimeException("没有对应的分析器,或者分析器个数不对,请检查！");
        }
    }

    /**
     * 查询过滤器列表
     *
     * @param filterPagerVO
     * @return
     */
    public PageRes<FilterOpertorVO> getFilterOperatorPager(FilterPagerVO filterPagerVO) {
        PageReq pager = mapper.map(filterPagerVO, PageReq.class);
        pager.setOrder_("desc");
        pager.setBy_("createTime");
        String name = filterPagerVO.getName();
        String operatorType = filterPagerVO.getOperatorType();
        List<QueryCondition> conditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(name)) {
            conditions.add(QueryCondition.or(QueryCondition.like("name", name), QueryCondition.like("label", name)));
        }
        if (StringUtils.isNotEmpty(operatorType)) {
            conditions.add(QueryCondition.eq("operatorType", operatorType));
        }
        // 查询非离线任务
        conditions.add(QueryCondition.eq("filterType", "0"));
        conditions.add(QueryCondition.eq("deleteFlag", true));
        conditions.add(QueryCondition.or(QueryCondition.isNull("ruleType"), QueryCondition.notEq("ruleType", FilterOperator.INSTANCE)));
        Page<FilterOperator> page = findAll(conditions, pager.getPageable());
        PageRes<FilterOpertorVO> res = getFilterPage(page);
        return res;
    }

    /**
     * 转换filterPage
     *
     * @param page
     * @return
     */
    private PageRes<FilterOpertorVO> getFilterPage(Page<FilterOperator> page) {
        List<FilterOpertorVO> list = new ArrayList<>();
        List<FilterOperator> content = page.getContent();
        for (FilterOperator filterOperator : content) {
            FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
            String filterConfigTemplate = filterOperator.getFilterConfigTemplate();
            if (StringUtils.isNotEmpty(filterConfigTemplate)) {
                List<FilterOpertorVO> childList = getFilterModelChildren(filterOperator.getCode());
                filterOpertorVO.setChildren(childList);
            }
            List<QueryCondition> ruleParams = new ArrayList<>();
            ruleParams.add(QueryCondition.eq("filterCode", filterOperator.getCode()));
            ruleParams.add(QueryCondition.eq("isStarted", "1"));
            List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleParams);
            if (CollectionUtils.isNotEmpty(ruleFilters)) {
                List<String> ruleIds = ruleFilters.stream().map(RuleFilter::getRuleId).distinct().collect(Collectors.toList());
                List<QueryCondition> riskEventParams = new ArrayList<>();
                riskEventParams.add(QueryCondition.in("id", ruleIds));
                riskEventParams.add(QueryCondition.eq("deleteFlag", true));
                riskEventParams.add(QueryCondition.eq("started", "1"));
                List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(riskEventParams);
                filterOpertorVO.setStartNum(riskEventRules.size());
            } else {
                filterOpertorVO.setStartNum(0);
            }
            boolean isConfig = commonFilterOperatorService.isConfigParam(filterOperator);
            if (isConfig) {
                filterOpertorVO.setIsConfigure("1");
            } else {
                filterOpertorVO.setIsConfigure("0");
            }
            list.add(filterOpertorVO);
        }
        PageRes<FilterOpertorVO> res = new PageRes();
        res.setList(list);
        res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        res.setTotal(Long.valueOf(page.getTotalElements()));
        return res;
    }


    /**
     * 获取分器器模板实例集合
     *
     * @param code
     * @return
     */
    private List<FilterOpertorVO> getFilterModelChildren(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("modelId", code));
        queryConditions.add(QueryCondition.eq("deleteFlag", true));
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        List<FilterOperator> intances = findAll(queryConditions, sort);
        List<FilterOpertorVO> childList = new ArrayList<>();
        intances.forEach(item -> {
            FilterOpertorVO filterOpertorVO1 = getFilterOperatorVO(item);
            childList.add(filterOpertorVO1);
        });
        return childList;
    }

    /**
     * 获得getFilterOperatorVO
     *
     * @param filterOperator
     * @return
     */
    public FilterOpertorVO getFilterOperatorVO(FilterOperator filterOperator) {
        Gson gson = new Gson();
        FilterOpertorVO filterOpertorVO = new FilterOpertorVO();
        filterOpertorVO.setFilterConfig(gson.fromJson(filterOperator.getFilterConfig(), FilterConfigObject.class));
        filterOpertorVO.setFilterConfigTemplate(gson.fromJson(filterOperator.getFilterConfigTemplate(), FilterConfigObject.class));
        filterOpertorVO.setDeleteFlag(filterOperator.getDeleteFlag());
        filterOpertorVO.setGuid(filterOperator.getGuid());
        filterOpertorVO.setName(filterOperator.getName());
        filterOpertorVO.setVersion(filterOperator.getVersion());
        filterOpertorVO.setStatus(filterOperator.isStatus());
        filterOpertorVO.setOperatorType(filterOperator.getOperatorType());
        filterOpertorVO.setCode(filterOperator.getCode());
        filterOpertorVO.setMultiVersion(filterOperator.getMultiVersion());
        filterOpertorVO.setInitStatus(filterOperator.getInitStatus());
        if (StringUtils.isNotBlank(filterOperator.getMultiVersion()) && "analysis".equals(filterOperator.getOperatorType())) {
            List<Map> version = JSONArray.parseArray(filterOperator.getMultiVersion(), Map.class);
            for (Map map : version) {
                if ("ideVersion".equals(map.get("type"))) {
                    filterOpertorVO.setIdeVersion(String.valueOf(map.get("ideVersion")));
                }
            }
        }
        filterOpertorVO.setStartConfig(gson.fromJson(filterOperator.getStartConfig(), StartConfigVO.class));
        filterOpertorVO.setParamValue(gson.fromJson(filterOperator.getParamValue(), Map.class));
        filterOpertorVO.setDesc(filterOperator.getDesc());
        filterOpertorVO.setLabel(filterOperator.getLabel());
        filterOpertorVO.setCreateTime(filterOperator.getCreateTime());
        filterOpertorVO.setUpdateTime(filterOperator.getUpdateTime());
        filterOpertorVO.setRoomType(filterOperator.getRoomType());
        filterOpertorVO.setRuleType(filterOperator.getRuleType());
        filterOpertorVO.setParamConfig(gson.fromJson(filterOperator.getParamConfig(), new TypeToken<List<ParamConfigVO>>() {
        }.getType()));
        filterOpertorVO.setFilterConfigTemplate(gson.fromJson(filterOperator.getFilterConfigTemplate(), FilterConfigObject.class));
        filterOpertorVO.setTag(filterOperator.getTag());
        filterOpertorVO.setFilterType(filterOperator.getFilterType());
        filterOpertorVO.setRuleFilterType(filterOperator.getRuleFilterType());
        filterOpertorVO.setModelId(filterOperator.getModelId());
        filterOpertorVO.setStartNum(0);

        // 描述信息
        filterOpertorVO.setAttackLine(filterOperator.getAttackLine());
        filterOpertorVO.setThreatCredibility(filterOperator.getThreatCredibility());
        filterOpertorVO.setDealAdvcie(filterOperator.getDealAdvcie());
        filterOpertorVO.setHarm(filterOperator.getHarm());
        filterOpertorVO.setPrinciple(filterOperator.getPrinciple());
        filterOpertorVO.setViolationScenario(filterOperator.getViolationScenario());
        filterOpertorVO.setFilterDesc(filterOperator.getFilterDesc());

        if (FilterOperator.MODEL.equals(filterOperator.getRuleType())) {
            List<FilterOpertorVO> childList = getFilterModelChildren(filterOperator.getCode());
            filterOpertorVO.setChildren(childList);

        }
        filterOpertorVO.setNewlineFlag(filterOperator.getNewlineFlag());
        try {
            String dependencies = filterOperator.getDependencies();
            if (StringUtils.isNotEmpty(dependencies)) {
                List<Dependencies> dependenciesList = JsonMapper.fromJsonString2List(dependencies, Dependencies.class);
                List<OutFieldInfo> outputFields = JsonMapper.fromJsonString2List(filterOperator.getOutFieldInfos(), OutFieldInfo.class);
                List<String> sourceIds = JsonMapper.fromJsonString2List(filterOperator.getSourceIds(), String.class);
                List<Outputs> outputs = JsonMapper.fromJsonString2List(filterOperator.getOutputs(), Outputs.class);
                filterOpertorVO.setDependencies(dependenciesList);
                filterOpertorVO.setOutFieldInfos(outputFields);
                filterOpertorVO.setSourceIds(sourceIds);
                filterOpertorVO.setOutputs(outputs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filterOpertorVO;
    }

    /**
     * 获得输出告警规则Id
     *
     * @param filterOperator
     * @return
     */
    public RiskEventRule getRiskEventRule(FilterOperator filterOperator) {
        Outputs outputs = getAlarmOutputsByGuid(filterOperator);
        if (outputs != null) {
            return outputs.getConfig().getAlarmObj().getRiskEventRule();
        } else {
            return null;
        }

    }

    /**
     * 通过分析器获取对应的告警输出
     */
    private Outputs getAlarmOutputsByGuid(FilterOperator filterOperator) {
        String outputs = filterOperator.getOutputs();
        Gson gson = new Gson();
        List<Outputs> outputList = gson.fromJson(outputs, new TypeToken<List<Outputs>>() {
        }.getType());
        for (Outputs output : outputList) {
            String type = output.getType();
            if (type.equals("alarmdeal")) {
                return output;
            }
        }
        return null;
    }

    /**
     * 启动过滤器job
     *
     * @param filterOpertorVO
     * @return
     */
    public Result<FilterOpertorVO> startFilterOperatorJob(FilterOpertorVO filterOpertorVO) {
        FilterOperator filterOperator = startFlinkJob(filterOpertorVO);
        updateFilterOperator(filterOperator, "1");
        filterOpertorVO = getFilterOperatorVO(filterOperator);
        Result<FilterOpertorVO> success = ResultUtil.success(filterOpertorVO);
        return success;
    }

    public void updateFilterOperator(FilterOperator filterOperator, String status) {
        try {
            Gson gson = new Gson();
            List<Outputs> outputs = JsonMapper.fromJsonString2List(filterOperator.getOutputs(), Outputs.class);
            for (Outputs output : outputs) {
                String type = output.getType();
                if (type.equals("alarmdeal")) {
                    AlarmObj alarmObj = output.getConfig().getAlarmObj();
                    RiskEventRule riskEventRule = alarmObj.getRiskEventRule();
                    String id = riskEventRule.getId();
                    riskEventRule = riskEventRuleService.getOne(id);
                    riskEventRule.setStarted(status);
                    riskEventRuleService.save(riskEventRule);
                    alarmObj.setRiskEventRule(riskEventRule);
                    RiskRuleEditVO riskRuleEditVO = alarmObj.getRiskRuleEditVO();
                    riskRuleEditVO.setStarted(status);
                    alarmObj.setRiskRuleEditVO(riskRuleEditVO);
                    output.getConfig().setAlarmObj(alarmObj);
                }
            }
            filterOperator.setStatus("1".equals(status) ? true : false);
            filterOperator.setOutputs(gson.toJson(outputs));
            save(filterOperator);
        } catch (IOException e) {
            logger.info("更新输出信息失败：" + e.getMessage());
        }
    }

    public FilterOperator startFlinkJob(FilterOpertorVO filterOpertorVO) {
        String guid = filterOpertorVO.getGuid();
        FilterOperator filterOperator = getOne(guid);
        filterOperator = startOperatorJob(filterOperator);
        return filterOperator;
    }

    /**
     * 启动job
     *
     * @param filterOperator
     * @return
     */
    private FilterOperator startOperatorJob(FilterOperator filterOperator) {
        filterOperator = executeStartJob(filterOperator);
        return filterOperator;
    }

    /**
     * 启动job-同一槽位启动多个规则
     *
     * @param riskEventId
     * @param ruleFilterMap
     */
    public void startOperatorJobGroup(String riskEventId, Map<String, List<String>> ruleFilterMap, String startType) {
        Map<String, String> codes = new HashMap<>();
        for (Map.Entry<String, List<String>> map : ruleFilterMap.entrySet()) {
            String ruleId = map.getKey();
            RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
            List<String> filterCodes = map.getValue();
            String codeResult = String.join(",", filterCodes);
            logger.info("传参filterCode的值为{}", codeResult);
            codes.put(riskEventRule.getRuleCode(), codeResult);
        }

        // 设置并行度
        int size = codes.size();
        if (size != 0) {
            int parallelism = getParallelism(size);
            String title = "";
            String[] riskEventIdArr = riskEventId.split("_");
            if (RuleTypeConstant.CATEGORY.equals(startType)) {
                // 分类方式
                EventCategory eventCategory = eventCategoryService.getOne(riskEventIdArr[0]);
                //二级事件分类
                title = eventCategory.getTitle();
                if (riskEventIdArr.length > 1) {
                    title = title + "_" + riskEventIdArr[1];
                }
            } else {
                // 数据源方式
                title = getJobNameTitle(riskEventIdArr[0]);
                if (riskEventIdArr.length > 1) {
                    title = title + "_" + riskEventIdArr[1];
                }
            }
            logger.info("启动codes={}", codes);
            FlinkStartVO flinkStartVO = FlinkStartVO.builder().type(startType).codeObj(codes).jobName(title).parallelism(parallelism).build();
            executeStartJobGroup(flinkStartVO);
        }
    }


    public List<String> getStartFilterByRuleId(String ruleId, boolean startStatus, String inputSourceId) {
        List<String> result = new ArrayList<>();
        List<QueryCondition> riskCondition = new ArrayList<>();
        riskCondition.add(QueryCondition.eq("id", ruleId));
        riskCondition.add(QueryCondition.eq("deleteFlag", true));
        if (startStatus) {
            riskCondition.add(QueryCondition.eq("started", "1"));
        } else {
            riskCondition.add(QueryCondition.eq("started", "0"));
        }
        List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(riskCondition);
        if (CollectionUtils.isEmpty(riskEventRules)) {
            return result;
        }

        List<QueryCondition> ruleFilterCondition = new ArrayList<>();
        ruleFilterCondition.add(QueryCondition.eq("ruleId", ruleId));
        ruleFilterCondition.add(QueryCondition.eq("isStarted", "1"));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterCondition);
        if (CollectionUtils.isNotEmpty(ruleFilters)) {
            for (RuleFilter ruleFilter : ruleFilters) {
                String ruleIdStr = ruleFilter.getRuleId();
                String filterCode = ruleFilter.getFilterCode();
                List<QueryCondition> filterConditions = new ArrayList<>();
                filterConditions.add(QueryCondition.eq("code", filterCode));
                filterConditions.add(QueryCondition.eq("deleteFlag", true));
                List<FilterOperator> filterOperatorList = findAll(filterConditions);
                if (CollectionUtils.isEmpty(filterOperatorList)) {
                    continue;
                }
                FilterOperator filterOperator = filterOperatorList.get(0);
                String source = filterOperator.getSourceIds();
                List<String> sourceIds = gson.fromJson(source, new TypeToken<List<String>>() {
                }.getType());
                //TODO 判断该规则是否引用这个数据源
                boolean sourceResult = false;
                if (sourceIds.contains(inputSourceId)) {
                    sourceResult = true;
                } else {
                    String label = filterOperator.getLabel();
                    logger.warn("规则{}不包含数源{}", label, inputSourceId);
                }
                boolean status = true;
                for (String sourceId : sourceIds) {
                    if (status) {
                        status = filterSourceStatusService.getFilterSourceStatusByRedis(sourceId);
                        logger.warn("数据源[{}]，{}满足条件。", sourceId, status ? "是" : "不");
                    }
                }
                // 通过规则，查询涉及到的维表
                Set<String> dimensions = getBaseLineDataForFilter(filterOperator.getCode());
                // 判断维表是否存在数据
                boolean isCount = checkFilterCode(dimensions, filterCode, ruleIdStr);
                if (!isCount) {
                    logger.warn("维表[{}]，没有策略[{}],规则[{}]的相关数据", dimensions, ruleIdStr, filterCode);
                }
                if (status && isCount && sourceResult) {
                    result.add(filterOperator.getCode());
                }
            }
        }

        return result;
    }


    /**
     * 检查并判断对应的数据源是否合规
     * @param ruleId
     * @param startStatus
     * @return
     */
    public List<String> getStartFilterByRuleId(String ruleId, boolean startStatus) {
        List<String> result = new ArrayList<>();
        List<QueryCondition> riskCondition = new ArrayList<>();
        riskCondition.add(QueryCondition.eq("id", ruleId));
        riskCondition.add(QueryCondition.eq("deleteFlag", true));
        if (startStatus) {
            riskCondition.add(QueryCondition.eq("started", "1"));
        } else {
            riskCondition.add(QueryCondition.eq("started", "0"));
        }
        List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(riskCondition);
        if (CollectionUtils.isEmpty(riskEventRules)) {
            return result;
        }

        List<QueryCondition> ruleFilterCondition = new ArrayList<>();
        ruleFilterCondition.add(QueryCondition.eq("ruleId", ruleId));
        ruleFilterCondition.add(QueryCondition.eq("isStarted", "1"));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterCondition);
        if (CollectionUtils.isNotEmpty(ruleFilters)) {
            for (RuleFilter ruleFilter : ruleFilters) {
                String ruleIdStr = ruleFilter.getRuleId();
                String filterCode = ruleFilter.getFilterCode();
                List<QueryCondition> filterConditions = new ArrayList<>();
                filterConditions.add(QueryCondition.eq("code", filterCode));
                filterConditions.add(QueryCondition.eq("deleteFlag", true));
                List<FilterOperator> filterOperatorList = findAll(filterConditions);
                if (CollectionUtils.isEmpty(filterOperatorList)) {
                    continue;
                }
                FilterOperator filterOperator = filterOperatorList.get(0);
                String source = filterOperator.getSourceIds();
                List<String> sourceIds = gson.fromJson(source, new TypeToken<List<String>>() {
                }.getType());
                boolean status = true;
                for (String sourceId : sourceIds) {
                    if (status) {
                        status = filterSourceStatusService.getFilterSourceStatusByRedis(sourceId);
                        logger.warn("数据源[{}]，{}满足条件。", sourceId, status ? "是" : "不");
                    }
                }
                // 通过规则，查询涉及到的维表
                Set<String> dimensions = getBaseLineDataForFilter(filterOperator.getCode());
                // 判断维表是否存在数据
                boolean isCount = checkFilterCode(dimensions, filterCode, ruleIdStr);
                if (!isCount) {
                    logger.warn("维表[{}]，没有策略[{}],规则[{}]的相关数据", dimensions, ruleIdStr, filterCode);
                }
                if (status && isCount) {
                    result.add(filterOperator.getCode());
                }
            }
        }

        return result;
    }


    public int getParallelism(int size) {
        String flinkSoltAllot = systemConfigService.getSysConfigById("flink_solt_allot");
        if (StringUtils.isNotEmpty(flinkSoltAllot)) {
            soltAllot = Integer.parseInt(flinkSoltAllot);
        }
        int parallelism = size / soltAllot;
        if (size % soltAllot > 0) {
            parallelism++;
        }
        logger.info("parall:" + parallelism);
        return parallelism;
    }

    /**
     * 启动job
     *
     * @param filterOperator
     * @return
     */
    private FilterOperator executeStartJob(FilterOperator filterOperator) {
        filterOperator.setStatus(true);
        String[] shellArray = getExeShellArray(filterOperator);
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ShellExecuteScript.executeShellByResultArray(shellArray);
            }
        });
        return filterOperator;
    }

    /**
     * 同一槽位启动多个规则
     *
     * @param flinkStartVO
     */
    private void executeStartJobGroup(FlinkStartVO flinkStartVO) {
        //String codes = JSON.toJSONString(flinkStartVO);
        String codes = gson.toJson(flinkStartVO);
        String[] shellArray = getExeShellArrayByFlinkStartVO(codes);
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ShellExecuteScript.executeShellByResultArray(shellArray);
            }
        });
    }

    public String[] getExeShellArray(FilterOperator filterOperator) {
        String code = filterOperator.getCode();
        RiskEventRule riskEventRule = getRiskEventRule(filterOperator);
        if (riskEventRule != null) {
            riskEventRule.setStarted(filterOperator.isStatus() ? "1" : "0");
            riskEventRuleService.addRuleInfoToAlarmHandler(riskEventRule);
            changeRiskEventStatus(riskEventRule.getId(), filterOperator.isStatus());
        }
        String[] exeShellArray = getExeShellArrayByFlinkStartVO(code);
        return exeShellArray;
    }

    public String[] getExeShellArrayByFlinkStartVO(String codes) {
        String[] exeShellArray = null;
        boolean remote_flag = flinkConfiguration.isRemote_flag();
        if (remote_flag) { //远程标识开启(需开启ssh免登陆)
            codes = codes.replace(",", "\\,");
            codes = codes.replace("\"", "\\\"");
            codes = codes.replace("{", "\\{");
            codes = codes.replace("}", "\\}");
            codes = codes.replace("（", "\\（");
            codes = codes.replace("）", "\\）");
            codes = codes.replace("(", "\\(");
            codes = codes.replace(")", "\\)");
            logger.info("分析程序入参值：{}", codes);
            exeShellArray = new String[]{"ssh", flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip(), flinkConfiguration.getFlink_home_path() + "/bin/flink", "run", "-d", "-c", MAIN_CLASS, flinkConfiguration.getFlink_jar_path(), codes};
        } else {
            exeShellArray = new String[]{flinkConfiguration.getFlink_home_path() + "/bin/flink", "run", "-d", "-c", MAIN_CLASS, flinkConfiguration.getFlink_jar_path(), codes};
        }
        return exeShellArray;
    }

    /**
     * 改变告警规则的状态
     *
     * @param riskEventId
     * @param status
     */
    private void changeRiskEventStatus(String riskEventId, boolean status) {
        RiskEventRule riskEventRule = riskEventRuleService.getOne(riskEventId);
        if (riskEventRule != null) {
            riskEventRule.setStarted(status ? "1" : "0");
            riskEventRuleService.save(riskEventRule);
        }
    }

    /**
     * 改变告警规则
     *
     * @param filterOperator
     * @param status
     */
    private void changeRiskEvent(FilterOperator filterOperator, boolean status) {
        RiskEventRule riskEventRule = getRiskEventRule(filterOperator);
        RiskEventRule riskEventRule1 = riskEventRuleService.getOne(riskEventRule.getId());
        if (riskEventRule1 != null) {
            riskEventRule1.setStarted(status ? "1" : "0");
            riskEventRule1.setTag(filterOperator.getTag());
            riskEventRuleService.save(riskEventRule1);
        }
    }

    /**
     * 暂停告警
     *
     * @param filterOpertorVO
     * @return
     */
    public Result<FilterOpertorVO> stopFilterOperatorJob(FilterOpertorVO filterOpertorVO) {
        String guid = filterOpertorVO.getGuid();
        FilterOperator filterOperator = getOne(guid);
        stopOperatorJob(filterOperator); //停止flink任务
        updateFilterOperator(filterOperator, "0"); //更新分析器规则状态
        filterOpertorVO = getFilterOperatorVO(filterOperator);
        Result<FilterOpertorVO> result = ResultUtil.success(filterOpertorVO);
        return result;

    }

    public void stopOperatorJob(FilterOperator filterOperator) {
        RiskEventRule riskEventRule = getRiskEventRule(filterOperator);
        String jobName = "";
        if (riskEventRule != null) {
            jobName = riskEventRule.getJob_name();
            changeRiskEvent(filterOperator, false);
        } else {
            FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
            jobName = getJobName(filterOpertorVO);
        }
        stopJobByJobName(jobName);

    }

    public void stopJobByJobName(String jobName) {
        logger.info("jobName:" + jobName);
        String flink_home_path = flinkConfiguration.getFlink_home_path();
        String cmd_path = flink_home_path + "/bin/flink" + " " + "list";
        boolean remoteFlag = flinkConfiguration.isRemote_flag();
        if (remoteFlag) {
            cmd_path = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + cmd_path;
        }
        String exe_shell = cmd_path;
        List<String> list = ShellExecuteScript.queryExecuteCmd(exe_shell);
        String jobId = SocUtil.getJobId(jobName, list);
        logger.info("jobId:" + jobId);
        stopByJobId(jobId);

    }


    /**
     * 通过job任务列表停止flinkjob
     *
     * @param jobList 任务列表
     */
    public void stopByJobList(List<String> jobList) {
        List<String> jobIdList = new ArrayList<>();
        List<Integer> count = new ArrayList<>();
        String job_Id = null;
        for (int i = 0; i < jobList.size(); i++) {
            String str = jobList.get(i);
            if (str.startsWith("---")) {
                count.add(i); //在---虚线之间的为对应的job的位置
            }
        }
        if (count.size() == 2) {
            jobList = jobList.subList(count.get(0), count.get(1));//在---虚线之间的为对应的job的内容
            for (String job_content : jobList) {
                String[] split = job_content.split(" : ");
                if (split.length == 3) {
                    job_Id = split[1];
                    jobIdList.add(job_Id);
                }
            }
        }
        for (String jobId : jobIdList) {
            logger.info("########自检查任务中需要停止的flink jobId={}", jobId);
            stopByJobId(jobId);
        }
    }


    public void stopByJobId(String jobId) {
        String flink_home_path = flinkConfiguration.getFlink_home_path();
        String cmd_path = flink_home_path + "/bin/flink" + " " + "list";
        boolean remote_flag = flinkConfiguration.isRemote_flag();
        if (remote_flag) {
            cmd_path = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + cmd_path;
        }
        String exe_shell = cmd_path;
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                List<String> list = ShellExecuteScript.queryExecuteCmd(exe_shell);
                if (StringUtils.isNotEmpty(jobId)) {
                    String cancel_job_command = null;
                    if (remote_flag) {
                        cancel_job_command = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + flink_home_path + "/bin/flink" + " cancel " + jobId;
                    } else {
                        cancel_job_command = flink_home_path + "/bin/flink" + " cancel " + jobId;
                    }
                    logger.info("cancel_job_command:" + cancel_job_command);
                    boolean result = ShellExecuteScript.executeShellByResult(cancel_job_command);
                    logger.info("cancel result:" + result);
                }
            }
        });
    }

    /**
     * 改成分析器和过滤器引用资源对象的值
     *
     * @param code
     */
    public void changeFilterAndAnalysisRelateResource(String code, Integer version) {
        List<FilterOperator> resourceOperators = getRelateResourceOperators(code);
        ObjectResource objectResource = getObjectResource(code);
        changeFilterOperatorReourceValue(code, objectResource, resourceOperators);
        changeAggOperatorReourceValue(code, objectResource, resourceOperators);
        changeRelateMultiVersion(code, version, resourceOperators);
        changeRelateDependencyVersion(code, version, resourceOperators);
        save(resourceOperators);
        restartStartStatusOperator(resourceOperators);
    }

    /**
     * 改变启动状态的规则
     *
     * @param resourceOperators
     */
    private void restartStartStatusOperator(List<FilterOperator> resourceOperators) {
        for (FilterOperator filterOperator : resourceOperators) {
            cacheThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    stopOperatorJob(filterOperator);
                    if (filterOperator.isStatus()) {
                        try {
                            Thread.sleep(5 * 1000L);
                        } catch (InterruptedException e) {
                            logger.error("改变启动状态规则线程中断", e);
                            Thread.currentThread().interrupt();
                        }
                        startOperatorJob(filterOperator);
                    }
                }
            });
            save(filterOperator);
        }
    }

    /**
     * 修改关联的综合版本号
     *
     * @param code
     * @param version
     * @param resourceOperators
     */
    private void changeRelateMultiVersion(String code, Integer version, List<FilterOperator> resourceOperators) {
        for (FilterOperator filterOperator : resourceOperators) {
            String multiVersionStr = filterOperator.getMultiVersion();
            logger.info("multiVersionStr对应数据为：" + multiVersionStr);
            List<MultiVersion> multiVersionList = gson.fromJson(multiVersionStr, new TypeToken<List<MultiVersion>>() {
            }.getType());
            for (MultiVersion multiVersion : multiVersionList) {
                String multiCode = multiVersion.getCode();
                if (multiCode.equals(code)) {
                    multiVersion.setVersion(version);
                }
            }
            filterOperator.setMultiVersion(gson.toJson(multiVersionList));
        }
    }

    private void changeRelateDependencyVersion(String code, Integer version, List<FilterOperator> resourceOperators) {
        for (FilterOperator filterOperator : resourceOperators) {
            String dependencies = filterOperator.getDependencies();
            logger.info("dependencies对应数据为：" + dependencies);
            List<Dependencies> dependenciesList = gson.fromJson(dependencies, new TypeToken<List<Dependencies>>() {
            }.getType());
            for (Dependencies dependency : dependenciesList) {
                String guid = dependency.getGuid();
                if (guid.equals(code)) {
                    dependency.setVersion(String.valueOf(version));
                }
            }
            filterOperator.setDependencies(gson.toJson(dependenciesList));
        }
    }

    /**
     * 根据资源对应的引用修改聚合属性列的过滤数据
     *
     * @param code
     * @param objectResource
     * @param resourceOperators
     */
    private void changeAggOperatorReourceValue(String code, ObjectResource objectResource, List<FilterOperator> resourceOperators) {
        for (FilterOperator filterOperator : resourceOperators) {
            String filterConfig = filterOperator.getFilterConfig();
            FilterConfigObject filterConfigObject = gson.fromJson(filterConfig, FilterConfigObject.class);
            Tables[][] tables = filterConfigObject.getTables();
            for (int i = 0; i < tables.length; i++) {
                for (int j = 0; j < tables[i].length; j++) {
                    Tables table = tables[i][j];
                    List<Column> columns = table.getColumn();
                    changeAggColumnInfoByObjectResource(code, objectResource, columns);
                    table.setColumn(columns);
                }
            }
            filterConfigObject.setTables(tables);
            String filterConfigObjectJson = gson.toJson(filterConfigObject);
            filterOperator.setFilterConfig(filterConfigObjectJson);
        }
    }

    /**
     * 根据资源对应的引用修改聚合属性列的过滤数据（业务实现子类）
     *
     * @param code
     * @param objectResource
     * @param columns
     */
    private void changeAggColumnInfoByObjectResource(String code, ObjectResource objectResource, List<Column> columns) {
        for (Column column : columns) {
            String aggType = column.getAggType();
            if (aggType != null && aggType.equals("folds")) {
                String exp = column.getExp();
                if (StringUtils.isNotEmpty(exp)) {
                    AggregateOperator aggregateOperator = gson.fromJson(exp, AggregateOperator.class);
                    LogicOperator loginExp = aggregateOperator.getLoginExp();
                    if (loginExp != null) {
                        changeLogicOperatorValue(loginExp, code, objectResource);
                        logger.info("logicOperator分析器目前对应的值为：" + gson.toJson(loginExp));
                        aggregateOperator.setLoginExp(loginExp);
                        exp = gson.toJson(aggregateOperator);
                        column.setExp(exp);
                    }
                }
            }
        }
    }

    /**
     * 修改过滤组件对应的值
     *
     * @param code
     * @param resourceOperators
     */
    private void changeFilterOperatorReourceValue(String code, ObjectResource objectResource, List<FilterOperator> resourceOperators) {
        for (FilterOperator filterOperator : resourceOperators) {
            String filterConfig = filterOperator.getFilterConfig();
            FilterConfigObject filterConfigObject = gson.fromJson(filterConfig, FilterConfigObject.class);
            Exchanges[][] exchanges = filterConfigObject.getExchanges();
            for (int i = 0; i < exchanges.length; i++) {
                for (int j = 0; j < exchanges[i].length; j++) {
                    Exchanges exchange = exchanges[i][j];
                    String options = exchange.getOptions();
                    if (StringUtils.isNotEmpty(options)) {
                        LogicOperator logicOperator = gson.fromJson(options, LogicOperator.class);
                        changeLogicOperatorValue(logicOperator, code, objectResource);
                        logger.info("logicOperator过滤器目前对应的值为：" + gson.toJson(logicOperator));
                        options = gson.toJson(logicOperator);
                        exchange.setOptions(options);
                    }
                }
            }
            filterConfigObject.setExchanges(exchanges);
            String filterConfigObjectJson = gson.toJson(filterConfigObject);
            filterOperator.setFilterConfig(filterConfigObjectJson);
        }
    }

    private ObjectResource getObjectResource(String code) {
        ObjectResource objectResource = null;
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("code", code));
        conditions.add(QueryCondition.notEq("deleteFlag", ObjectResourceConst.DELETE_FLAG_DELETE));
        List<ObjectResource> list = objectResourceService.findAll(conditions);
        if (list.size() == 1) {
            objectResource = list.get(0);
        } else {
            throw new RuntimeException("code:" + code + "查出的资源多于一个请检查！");
        }
        return objectResource;
    }

    /**
     * 修改logicOperator的
     *
     * @param logicOperator
     * @param code
     */
    public void changeLogicOperatorValue(LogicOperator logicOperator, String code, ObjectResource objectResource) {
        String type = logicOperator.getType();
        if ("filter".equals(type)) {
            ExpVO exp = logicOperator.getExp();
            String resguid = exp.getResguid();
            if (resguid != null && resguid.equals(code)) {
                String content = objectResource.getContent();
                exp.setValue(content);
                logicOperator.setExp(exp);
            }
            return;
        }
        if (type.equalsIgnoreCase("and") || type.equalsIgnoreCase("or")) {
            List<LogicOperator> filters = logicOperator.getFilters();
            for (LogicOperator logicOperators : filters) {
                changeLogicOperatorValue(logicOperators, code, objectResource);
            }
        }
    }

    /**
     * 获得关联资源应用的过滤器和分析器
     *
     * @param code
     * @return
     */
    private List<FilterOperator> getRelateResourceOperators(String code) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("deleteFlag", true));
        conditions.add(QueryCondition.like("multiVersion", code));
        List<FilterOperator> filterOperators = findAll(conditions);
        return filterOperators;
    }

    /**
     * 获得维表信息
     *
     * @return
     */
    public List<DimensionTable> getDimensionTables() {
        List<DimensionTable> dimensionTables = new ArrayList<>();
        List<DimensionTableInfo> tableInfos = dimensionTableService.findAll();
        for (DimensionTableInfo dimensionTableInfo : tableInfos) {
            DimensionTable dimensionTable = new DimensionTable();
            String name = dimensionTableInfo.getName();
            String nameEn = dimensionTableInfo.getNameEn();
            String filterCon = dimensionTableInfo.getFilterCon();
            String guid = dimensionTableInfo.getGuid();
            if (StringUtils.isNotBlank(filterCon)) {
                dimensionTable.setFilterCon(filterCon);
            }
            dimensionTable.setGuid(guid);
            dimensionTable.setCnTableName(name);
            dimensionTable.setEnTableName(nameEn);
            dimensionTable.setTableType(dimensionTableInfo.getTableType());
            List<DimensionTableFieldVo> dimensionFields = getDimensionFields(dimensionTableInfo);
            dimensionTable.setFields(dimensionFields);
            dimensionTables.add(dimensionTable);
        }
        return dimensionTables;
    }

    /**
     * 获得维表对应的字段
     *
     * @param dimensionTableInfo
     * @return
     */
    private List<DimensionTableFieldVo> getDimensionFields(DimensionTableInfo dimensionTableInfo) {
        List<DimensionTableFieldVo> list = new ArrayList<>();
        String guid = dimensionTableInfo.getGuid();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("tableGuid", guid));
        List<DimensionTableField> dimensionTableFields = dimensionTableFieldService.findAll(conditions);
        for (DimensionTableField dimensionTableField : dimensionTableFields) {
            DimensionTableFieldVo dimensionTableFieldVo = new DimensionTableFieldVo();
            String fieldName = dimensionTableField.getFieldName();
            String fieldType = dimensionTableField.getFieldType();
            String aliasName = dimensionTableField.getAliasName();
            dimensionTableFieldVo.setName(fieldName);
            dimensionTableFieldVo.setType(fieldType);
            if (StringUtils.isNotBlank(aliasName)) {
                dimensionTableFieldVo.setAliasName(aliasName);
            } else {
                dimensionTableFieldVo.setAliasName(fieldName);
            }
            list.add(dimensionTableFieldVo);
        }
        return list;
    }

    public FilterOpertorVO createAnalyzerInstanceByGuid(Map<String, Object> map) {
        String guid = map.get("guid").toString();
        FilterOperator filterOperator = getOne(guid);
        return createAnalyzerInstance(map, filterOperator);
    }

    /**
     * 根据时间类型生成cron表达式，要求后面有一个是错开的，不要集中在一起爆发，错峰
     *
     * @param startConfig 启动配置，只有离线任务才有这个启动配置，实时任务没有
     */
    private void generateCronExpression(Map<String, Object> startConfig) {
        Map<String, String> cycleStrategyCondition = (Map) startConfig.get("cycleStrategyCondition");
        if (cycleStrategyCondition == null) {
            return;
        }
        String timeType = cycleStrategyCondition.get("type");
        if (StringUtils.isEmpty(timeType)) {
            return;
        }
        //前端传来的cron表达式
        String cronExpression = cycleStrategyCondition.get("cronExpression");
        //触发cron模板表达式，带有一个占位符的形式，占位符的形式可以采用随机数形式，进行替换，从而达到错峰
        //cron表达式中的年月日    秒  分   时   日   月    星期     年
        if (StringUtils.isEmpty(cronExpression)) {
            //前端没有传cron表达式，考虑自动生成表达式。
            switch (timeType) {
                //小时,每次统计是上一个小时的数据
                case "hour":
                    cronExpression = "{hour_r1} {hour_r2} * * * ?";
                    break;
                //天
                case "day":
                    //每天凌晨 前一天的数据
                    cronExpression = "{r1} {r2} {r3} * * ?";
                    break;
                //周
                case "week":
                    //每周一凌晨 上周的数据
                    cronExpression = "{r1} {r2} {r3} * * 1";
                    break;
                //月
                case "month":
                    //每个月1号 执行上个月的数据
                    cronExpression = "{r1} {r2} {r3} 1 * ?";
                    break;
                default:
                    break;

            }
        }
        cycleStrategyCondition.put("cronExpression", replaceCronTemplate(cronExpression));
        startConfig.put("cycleStrategyCondition", cycleStrategyCondition);
    }


    /**
     * 替换cron模板占位符采用随机数得到最终的cron，错峰
     *
     * @param cronTemplate
     */
    private static String replaceCronTemplate(String cronTemplate) {
        if (!cronTemplate.contains("{r1}") && !cronTemplate.contains("{hour_r1}")) {
            return cronTemplate;
        }
        Random random = new Random();
        int r1 = random.nextInt(59);
        int r2 = random.nextInt(59);
        int r3 = random.nextInt(3);
        int hourR1 = random.nextInt(10);
        int hourR2 = random.nextInt(10);
        cronTemplate = cronTemplate.replace("{r1}", r1 + "");
        cronTemplate = cronTemplate.replace("{r2}", r2 + "");
        cronTemplate = cronTemplate.replace("{r3}", r3 + "");
        cronTemplate = cronTemplate.replace("{hour_r1}", hourR1 + "");
        cronTemplate = cronTemplate.replace("{hour_r2}", hourR2 + "");
        return cronTemplate;
    }


    /**
     * 如果编辑该规则，那么一定规则处于关闭状态。
     * <p>
     * 1 规则只有1个，所有的只是修改
     * 2 将前端传过来的参数替换到config_template中的占位符，然后更新到config中
     * 3 如果是离线任务，提交启动配置更新启动配置start_config
     * 4 todo 离线定时周期前端只会传周期类型  天 时 周 月 ，需要根据类型计算cron表达式。
     */
    public FilterOpertorVO createAnalyzerInstance(Map<String, Object> map, FilterOperator filterOperator) {
        //启动任务类型 实时或者离线任务 tag是必须传的参数，不可能出现空指针。
        if (map.get("tag") == null) {
            throw new RuntimeException("前端tag不能够为空，离线请传offline，在线传online");
        }
        String tag = map.get("tag").toString();
        //数据源数量检查消息
        String checkSourceNumMsg = checkSourceNum(tag, filterOperator);
        if (StringUtils.isNotEmpty(checkSourceNumMsg)) {
            //抛出异常
            throw new RuntimeException(checkSourceNumMsg);
        }
        filterOperator.setTag(tag);
        //离线启动参数
        Map<String, Object> startConfig = (Map) map.get("startConfig");
        if (startConfig != null) {
            generateCronExpression(startConfig);
            filterOperator.setStartConfig(gson.toJson(startConfig));
        } else {
            //非离线任务
            filterOperator.setStartConfig("");
        }
        //参数配置
        String filterConfigTemplate = filterOperator.getFilterConfigTemplate();
        //  说明有参数，说明规则配置了参数的。
        if (StringUtils.isNotEmpty(filterConfigTemplate)) {
            Object paramValue = map.get("paramValue");
            //前端有可能不配置参数就直接保存了
            if (paramValue != null) {
                //替换config_template中的占位符后得到filter_config
                String filterConfig = replacefilterConfigTemplate(map, filterConfigTemplate);
                //启动配置
                filterOperator.setFilterConfig(filterConfig);
                filterOperator.setParamValue(gson.toJson(paramValue));
            }
            map.remove("guid");
            map.remove("code");
            map.remove("tag");
            map.remove("startConfig");
        }
        //更新数据
        save(filterOperator);
        FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
        filterOpertorVO.setStatus(false);
        return filterOpertorVO;
    }

    private Long getFilterOperatorInstanceVersion(FilterOperator filterOperator) {
        String modelId = filterOperator.getModelId();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("modelId", modelId));
        conditions.add(QueryCondition.eq("ruleType", FilterOperator.INSTANCE));
        Long size = count(conditions);
        size++;
        return size;
    }

    public FilterOpertorVO createAnalyzerInstanceByCode(Map<String, Object> map) {
        String code = map.get("code").toString();
        List<FilterOperator> filterOperatorList = getFilterOperators(code);
        if (filterOperatorList.size() == 1) {
            FilterOperator filterOperator = filterOperatorList.get(0);
            return createAnalyzerInstance(map, filterOperator);

        } else {
            throw new RuntimeException("分析器code值不唯一");
        }
    }

    /**
     * 检查数据源数量，离线任务的数据源只能有1个
     *
     * @param tag            任务类型标记
     * @param filterOperator 规则
     */
    private String checkSourceNum(String tag, FilterOperator filterOperator) {
        //实时在线的不用校验了
        if ("online".equals(tag)) {
            return null;
        }
        //离线的
        String filterConfig = filterOperator.getFilterConfigTemplate();
        if (StringUtils.isEmpty(filterConfig)) {
            return null;
        }
        FilterConfigObject filterConfigObject = gson.fromJson(filterConfig, FilterConfigObject.class);
        Tables[][] tables = filterConfigObject.getTables();
        if (tables.length > 1) {
            return "离线任务不支持多个数据源，请重新绑定其他规则！";
        }
        return null;
    }

    /**
     * 动态参数替换，有2个地方，原来只有1个地方，一个地方是规则那里，另外一个地方是时间窗那里也需要有动态参数，这里需要进行调整。
     * 规则里面是在exchange里面，
     *
     * @param filterConfigTemplate 模板
     * @param map                  参数值
     */
    private String replacefilterConfigTemplate(Map<String, Object> map, String filterConfigTemplate) {
        FilterConfigObject filterConfigObject = new Gson().fromJson(filterConfigTemplate, FilterConfigObject.class);
        Exchanges[][] exchanges = filterConfigObject.getExchanges();
        for (int i = 0; i < exchanges.length; i++) {
            for (int j = 0; j < (exchanges[i]).length; j++) {
                Exchanges exchange = exchanges[i][j];
                String options = exchange.getOptions();
                if (StringUtils.isNotEmpty(options)) {
                    LogicOperator logicOperator = new Gson().fromJson(options, LogicOperator.class);
                    dealExchangeParams(exchange, logicOperator);
                }
            }
        }
        filterConfigTemplate = processTemplate((new Gson()).toJson(filterConfigObject), map);
        filterConfigTemplate = filterConfigTemplate.replaceAll("#@#", "\\${");
        return filterConfigTemplate;
    }

    private void dealExchangeParams(Exchanges exchange, LogicOperator logicOperator) {
        ExpVO expVO = logicOperator.getExp();
        if (logicOperator.getExp() != null && !expVO.getValueType().equals("params") && expVO.getValue().matches("\\$\\{\\w+\\}")) {
            expVO.setValue(expVO.getValue().replace("${", "#@#"));
        }

        exchange.setOptions((new Gson()).toJson(logicOperator));
        List<LogicOperator> logicOperatorList = logicOperator.getFilters();
        for (LogicOperator logicOperator1 : logicOperatorList) {
            dealExchangeParams(exchange, logicOperator1);
        }
    }

    public static String processTemplate(String template, Map<String, Object> params) {
        Matcher mm = Pattern.compile(CommonFilterOperatorService.PARAM_PATTERN).matcher(template);
        Map<String, Object> paramValue = (Map) params.get("paramValue");
        StringBuffer sb = new StringBuffer();
        while (mm.find()) {
            //这个就是匹配中占位符的，比如占位符是${key}
            String param = mm.group();
            Object value = paramValue.get(param);
            if (value != null) {
                mm.appendReplacement(sb, value.toString());
            }
        }
        mm.appendTail(sb);
        String result = sb.toString();
        Matcher matcher = Pattern.compile(CommonFilterOperatorService.PARAM_PATTERN).matcher(result);
        if (matcher.find()) {
            throw new RuntimeException("参数错误");
        }
        return result;
    }

    public Result<FilterOpertorVO> startAnalyzerInstance(Map<String, Object> map) {
        String guid = map.get("guid").toString();
        FilterOperator filterOperator = getOne(guid);
        String filterConfigTemplate = filterOperator.getFilterConfigTemplate();
        filterConfigTemplate = replacefilterConfigTemplate(map, filterConfigTemplate);
        filterOperator.setFilterConfig(filterConfigTemplate);
        startOperatorJob(filterOperator);
        updateFilterOperator(filterOperator, "1");
        FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
        return ResultUtil.success(filterOpertorVO);
    }

    public Result<FilterOpertorVO> stopAnalyzerInstance(Map<String, Object> map) {
        String guid = map.get("guid").toString();
        FilterOperator filterOperator = getOne(guid);
        stopOperatorJob(filterOperator);
        updateFilterOperator(filterOperator, "0");
        FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
        return ResultUtil.success(filterOpertorVO);
    }

    public Result<FilterOpertorVO> editAnalyzerInstanceByGuid(Map<String, Object> map) {
        String guid = map.get("guid").toString();
        FilterOperator filterOperator = getOne(guid);
        map.remove("guid");
        FilterOpertorVO filterOpertorVO = createAnalyzerInstance(map, filterOperator);
        return ResultUtil.success(filterOpertorVO);
    }


    private FilterOpertorVO editAnalyzerInstance(Map<String, Object> map, FilterOperator filterOperator) {
        editAnalyzerInstanceOnly(map, filterOperator);
        if (filterOperator.isStatus()) {
            List<FilterOperator> filterOperatorList = new ArrayList<>();
            filterOperatorList.add(filterOperator);
            restartStartStatusOperator(filterOperatorList);
        }
        FilterOpertorVO filterOpertorVO = getFilterOperatorVO(filterOperator);
        return filterOpertorVO;

    }

    private FilterOperator editAnalyzerInstanceOnly(Map<String, Object> map, FilterOperator filterOperator) {
        String filterConfigTemplate = filterOperator.getFilterConfigTemplate();
        filterConfigTemplate = replacefilterConfigTemplate(map, filterConfigTemplate);
        String outputs = processTemplate(filterOperator.getOutputs(), map);
        filterOperator.setFilterConfig(filterConfigTemplate);
        filterOperator.setParamValue(new Gson().toJson(map));
        filterOperator.setOutputs(outputs);
        filterOperator.setTag(FilterOperator.PERMIT);
        Boolean status = filterOperator.isStatus();
        updateFilterOperator(filterOperator, status ? "1" : "0");
        return filterOperator;
    }

    public Result<FilterOpertorVO> editAnalyzerInstanceByCode(Map<String, Object> map) {
        String code = map.get("code").toString();
        List<FilterOperator> filterOperatorList = getFilterOperators(code);
        if (filterOperatorList.size() == 1) {
            FilterOperator filterOperator = filterOperatorList.get(0);
            map.remove("code");
            FilterOpertorVO filterOpertorVO = editAnalyzerInstance(map, filterOperator);
            return ResultUtil.success(filterOpertorVO);
        } else {
            throw new RuntimeException("分析器code值不唯一");
        }
    }

    public List<FilterOperator> getFileOperatorByDataSource(String dataSourceLabel) {
        List<EventTable> eventTableList = eventTabelService.getEventTableByTitle(dataSourceLabel);
        if (eventTableList.size() == 1) {
            EventTable eventTable = eventTableList.get(0);
            String eventId = eventTable.getId();
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.like("sourceIds", eventId));
            conditions.add(QueryCondition.eq("deleteFlag", true));
            conditions.add(QueryCondition.eq("ruleType", FilterOperator.MODEL));
            List<FilterOperator> filterOperatorList = findAll(conditions);
            return filterOperatorList;
        } else {
            throw new RuntimeException("事件类型不唯一");
        }

    }

    public Boolean stopOperatorByRiskEventId(String riskEventId, List<String> ids, String startType) {
        logger.info("#####################stopOperatorByRiskEventId riskEventId={}", riskEventId);
        String title = "";
        String[] riskEventIdArr = riskEventId.split("_");
        if ("category".equals(startType)) {
            EventCategory eventCategory = eventCategoryService.getOne(riskEventIdArr[0]);
            //二级事件分类
            title = eventCategory.getTitle();
            if (riskEventIdArr.length > 1) {
                title = eventCategory.getTitle() + "_" + riskEventIdArr[1];
            }
        } else {
            String[] sourceIdArr = riskEventIdArr[0].split(",");
            List<String> titles = new ArrayList<>();
            for (String sourceIdStr : sourceIdArr) {
                EventTable eventTable = eventTabelService.getOne(sourceIdStr);
                String title1 = eventTable.getLabel();
                if (riskEventIdArr.length > 1) {
                    title1 = eventTable.getLabel() + "_" + riskEventIdArr[1];
                }
                titles.add(title1);
            }
            title = String.join(",", titles);
        }

        if (StringUtils.isNotBlank(title)) {
            String[] arr = title.split(",");
            for (String str : arr) {
                stopJobByJobName(str);
            }
        }
        return true;

    }


    public List<String> getFlinkCodes(String riskEventId) {
        List<String> allList = new ArrayList<>();
        List<String> eventCategoryIdList = getChildEventCategorys(riskEventId);
        for (String id : eventCategoryIdList) {
            List<QueryCondition> queryConditions = new ArrayList<>();
            queryConditions.add(QueryCondition.eq("riskEventId", id));
            queryConditions.add(QueryCondition.eq("deleteFlag", true));
            List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(queryConditions);
            List<String> codes = riskEventRuleList.stream().map(item -> item.getId()).collect(Collectors.toList());
            allList.addAll(codes);
        }
        return allList;
    }

    public List<String> getStartFlinkCodes(String riskEventId) {
        List<String> eventCategoryIdList = getChildEventCategorys(riskEventId);
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.in("riskEventId", eventCategoryIdList));
        queryConditions.add(QueryCondition.eq("deleteFlag", true));
        queryConditions.add(QueryCondition.eq("started", "1"));
        List<String> onlineRuleIdList = commonFilterOperatorService.getOnlineRuleIdList();
        queryConditions.add(QueryCondition.in("id", onlineRuleIdList));
        List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(queryConditions);
        List<String> codes = riskEventRuleList.stream().map(item -> item.getId()).collect(Collectors.toList());
        return codes;
    }

    /**
     * 计算当前开启后的总槽位数
     *
     * @param guidGroup
     * @return
     */
    public int sumExistSlot(Map<String, List<String>> guidGroup) {
        //查询所有的二级事件分类
        List<Map<String, Object>> list = eventCategoryDao.getGetSecondLevelEvent();
        int parallelismSum = 0;
        //计算已使用的槽位
        for (Map<String, Object> map : list) {
            //遍历修改规则和分析器状态
            List<String> codes = new ArrayList<>();
            String riskEventId = map.get("id").toString();
            List<String> eventCategoryIdList = getChildEventCategorys(riskEventId);
            //这一类事件下，已开启的规则
            for (String id : eventCategoryIdList) {
                List<QueryCondition> conditions = new ArrayList<>();
                conditions.add(QueryCondition.eq("riskEventId", id));
                conditions.add(QueryCondition.eq("deleteFlag", true));
                conditions.add(QueryCondition.eq("started", "1"));
                List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(conditions);
                List<String> analysisIds = riskEventRuleList.stream().map(item -> item.getAnalysisId()).collect(Collectors.toList());
                codes.addAll(analysisIds);
            }
            //这一类事件下，需要开启的规则
            if (guidGroup.containsKey(riskEventId)) {
                codes.addAll(guidGroup.get(riskEventId));
            }
            int parallelism = getParallelism(codes.size());

            parallelismSum += parallelism;
        }
        return parallelismSum;
    }

    private List<String> getChildEventCategorys(String eventId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("parentId", eventId));
        List<EventCategory> categoryList = eventCategoryService.findAll(queryConditions);
        return categoryList.stream().map(item -> item.getId()).collect(Collectors.toList());
    }

    public void stopByEventId(String eventId) {
        EventCategory secondLevelEventCategory = eventCategoryService.getOne(eventId);
        String title = secondLevelEventCategory.getTitle();
        if (StringUtils.isNotBlank(title)) {
            stopJobByJobName(title);
        }
    }

    public void pushFlinkStartTaskQueue(Map<String, List<String>> stringListMap) {
        flinkTaskJob.dealFlinkTask(stringListMap);
    }

    /**
     * 获取flink任务列表
     *
     * @return
     */
    public List<String> getJobList() {
        String flink_home_path = flinkConfiguration.getFlink_home_path();
//        File file = new File(flink_home_path);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }
        String cmd_path = flink_home_path + "/bin/flink" + " " + "list";
        boolean remote_flag = flinkConfiguration.isRemote_flag();
        if (remote_flag) {
            cmd_path = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + cmd_path;
        }
        String exe_shell = cmd_path;
        List<String> list = ShellExecuteScript.queryExecuteCmd(exe_shell);
        return list;
    }

    public List<String> getStartFlinkCodesBySourceId(String sourceId) {
        List<String> riskEventIds = getRuleJoinFilter(sourceId);
        return riskEventIds;
    }

    public List<String> getRuleJoinFilter(String sourceId) {
        List<String> riskList = new ArrayList<>();
        String[] sourceArr = sourceId.split(",");
        List<String> filterCodes = new ArrayList<>();
        for (String source : sourceArr) {
            List<QueryCondition> filterConditions = new ArrayList<>();
            filterConditions.add(QueryCondition.eq("deleteFlag", true));
            filterConditions.add(QueryCondition.like("sourceIds", "\"" + source + "\""));
            //以前的实时任务tag是空的，离线任务是一定有值的。测试发现如果值是null的话，用不等于查询不出来。
            List<QueryCondition> subQueryCondtion = new ArrayList<>();
            subQueryCondtion.add(QueryCondition.isNull("tag"));
            subQueryCondtion.add(QueryCondition.eq("tag", "online"));
            filterConditions.add(QueryCondition.or(subQueryCondtion));
            List<FilterOperator> filterOperators = findAll(filterConditions);
            if (CollectionUtils.isNotEmpty(filterOperators)) {
                List<String> filterCodeList = filterOperators.stream().map(FilterOperator::getCode).collect(Collectors.toList());
                filterCodes.addAll(filterCodeList);
            }
        }
        if (CollectionUtils.isEmpty(filterCodes)) {
            return riskList;
        }
        List<QueryCondition> ruleFilterConditions = new ArrayList<>();
        ruleFilterConditions.add(QueryCondition.in("filterCode", filterCodes));
        ruleFilterConditions.add(QueryCondition.eq("isStarted", "1"));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterConditions);
        if (CollectionUtils.isEmpty(ruleFilters)) {
            return riskList;
        }
        List<String> riskCodes = ruleFilters.stream().map(RuleFilter::getRuleId).collect(Collectors.toList());
        //TODO 筛选出需要启动的规则
        List<QueryCondition> riskConditions = new ArrayList<>();
        riskConditions.add(QueryCondition.eq("started", "1"));
        riskConditions.add(QueryCondition.eq("deleteFlag", true));
        riskConditions.add(QueryCondition.in("id", riskCodes));
        List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(riskConditions);
        if (CollectionUtils.isEmpty(riskEventRules)) {
            return riskList;
        }
        List<String> risks = riskEventRules.stream().map(RiskEventRule::getId).collect(Collectors.toList());
        riskList.addAll(risks);
        return riskList;
    }

    public String getJobNameTitle(String sources) {
        String[] sourceArr = sources.split(",");
        List<String> sourceIdList = new ArrayList<>();
        for (String sourceId : sourceArr) {
            EventTable eventTable = eventTabelService.getOne(sourceId);
            if (eventTable != null) {
                String title = eventTable.getLabel();
                if (StringUtils.isBlank(title)) {
                    title = sourceId;
                }
                sourceIdList.add(title);
            }
        }
        return StringUtils.join(sourceIdList, ",");
    }

    public List<FilterOperator> queryFilterByRuleId(String id) {
        List<QueryCondition> ruleParam = new ArrayList<>();
        ruleParam.add(QueryCondition.eq("isStarted", "1"));
        ruleParam.add(QueryCondition.eq("ruleId", id));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleParam);
        if (CollectionUtils.isNotEmpty(ruleFilters)) {
            List<String> filterCodes = ruleFilters.stream().map(RuleFilter::getFilterCode).collect(Collectors.toList());
            List<QueryCondition> filterParam = new ArrayList<>();
            filterParam.add(QueryCondition.eq("deleteFlag", true));
            filterParam.add(QueryCondition.in("code", filterCodes));
            List<FilterOperator> filterOperators = findAll(filterParam);
            return filterOperators;
        }

        return null;
    }

    /**
     * 通过规则获取数据源ID
     *
     * @param filterOperator
     * @return
     */
    public String getSourceIds(List<FilterOperator> filterOperator) {
        String key = "";
        if (CollectionUtils.isNotEmpty(filterOperator)) {
            List<String> sources = new ArrayList<>();
            filterOperator.stream().forEach(item -> {
                List<String> sourceIds = JSONArray.parseArray(item.getSourceIds(), String.class);
                if (CollectionUtils.isNotEmpty(sourceIds)) {
                    for (String sourceId : sourceIds) {
                        EventTable eventTable = eventTabelService.getOne(sourceId);
                        if (eventTable != null) {
                            sources.add(sourceId);
                        }
                    }
                }

            });
            key = String.join(",", sources.stream().distinct().collect(Collectors.toList()));
        }
        return key;
    }

    /**
     * 启动任务
     *
     * @param sourceId       启动类型ID
     * @param riskEventRules
     */
    public void startJob(String sourceId, List<String> riskEventRules, String startType) {
        int num = ruleFlinkTypeService.getRuleFlinkStartNum();
        List<String> filterCodes = new ArrayList<>();
        if (riskEventRules.size() > num) {
            int totalNum = riskEventRules.size();
            int pageNum = totalNum % num == 0 ? totalNum / num : totalNum / num + 1;
            int fromIndex, toIndex;
            for (int i = 0; i < pageNum; i++) {
                fromIndex = i * num;
                toIndex = Math.min(totalNum, fromIndex + num);
                List<String> subData = riskEventRules.subList(fromIndex, toIndex);
                Map<String, List<String>> map = new HashMap<>();
                for (String ruleId : subData) {
                    if (startType.equals(RuleTypeConstant.CATEGORY)) {
                        filterCodes = getStartFilterByRuleId(ruleId, true);
                    } else {
                        filterCodes = getStartFilterByRuleId(ruleId, true, sourceId);
                    }
                    if (CollectionUtils.isNotEmpty(filterCodes)) {
                        filterCodes = filterCodes.stream().distinct().collect(Collectors.toList());
                        map.put(ruleId, filterCodes);
                    }
                }
                String guids = String.join(",", subData);
                if (!map.isEmpty()) {
                    startOperatorJobGroup(sourceId + "_" + (i + 1), map, startType);
                    putFlinkTaskToMap(sourceId + "_" + (i + 1), subData);
                    logger.info("sourceId={},guids:{}，num={}", sourceId + "_" + (i + 1), guids, num);
                }
            }
        } else {
            Map<String, List<String>> map = new HashMap<>();
            for (String ruleId : riskEventRules) {
                if (startType.equals(RuleTypeConstant.CATEGORY)) {
                    filterCodes = getStartFilterByRuleId(ruleId, true);
                } else {
                    filterCodes = getStartFilterByRuleId(ruleId, true, sourceId);
                }
                if (CollectionUtils.isNotEmpty(filterCodes)) {
                    filterCodes = filterCodes.stream().distinct().collect(Collectors.toList());
                    map.put(ruleId, filterCodes);
                }
            }
            String guids = String.join(",", riskEventRules);
            logger.info("guids:{}，num={}", guids, num);
            if (!map.isEmpty()) {
                startOperatorJobGroup(sourceId, map, startType);
                putFlinkTaskToMap(sourceId, riskEventRules);
            }
        }
    }

    /**
     * todo:该方法存在重复后续进行优化，先解决漏洞
     *
     * @param sourceId       启动类型ID
     * @param riskEventRules
     */
    public void startInitJob(String sourceId, List<String> riskEventRules, String startType, List<String> jobIdList) {
        int num = ruleFlinkTypeService.getRuleFlinkStartNum();
        List<String> filterCodes = new ArrayList<>();
        if (riskEventRules.size() > num) {
            int totalNum = riskEventRules.size();
            int pageNum = totalNum % num == 0 ? totalNum / num : totalNum / num + 1;
            int fromIndex, toIndex;
            for (int i = 0; i < pageNum; i++) {
                fromIndex = i * num;
                toIndex = Math.min(totalNum, fromIndex + num);
                List<String> subData = riskEventRules.subList(fromIndex, toIndex);
                Map<String, List<String>> map = new HashMap<>();
                for (String ruleId : subData) {
                    if (startType.equals(RuleTypeConstant.CATEGORY)) {
                        filterCodes = getStartFilterByRuleId(ruleId, true);
                    } else {
                        filterCodes = getStartFilterByRuleId(ruleId, true, sourceId);
                    }
                    if (CollectionUtils.isNotEmpty(filterCodes)) {
                        filterCodes = filterCodes.stream().distinct().collect(Collectors.toList());
                        map.put(ruleId, filterCodes);
                    }
                }
                String guids = String.join(",", subData);
                if (!map.isEmpty()) {
                    //解决场景问题20231019:当flink没有重启，api-alarmdeal重启后出现的flinkTaskMap为空的问题。
                    if (CollectionUtils.isEmpty(jobIdList)) {
                        startOperatorJobGroup(sourceId + "_" + (i + 1), map, startType);
                    }
                    putFlinkTaskToMap(sourceId + "_" + (i + 1), subData);
                    logger.info("sourceId={},guids:{}，num={}", sourceId + "_" + (i + 1), guids, num);
                }
            }
        } else {
            Map<String, List<String>> map = new HashMap<>();
            for (String ruleId : riskEventRules) {
                if (startType.equals(RuleTypeConstant.CATEGORY)) {
                    filterCodes = getStartFilterByRuleId(ruleId, true);
                } else {
                    filterCodes = getStartFilterByRuleId(ruleId, true, sourceId);
                }
                if (CollectionUtils.isNotEmpty(filterCodes)) {
                    filterCodes = filterCodes.stream().distinct().collect(Collectors.toList());
                    map.put(ruleId, filterCodes);
                }
            }
            String guids = String.join(",", riskEventRules);
            logger.info("guids:{}，num={}", guids, num);
            //解决场景问题20231019:当flink没有重启，api-alarmdeal重启后出现的flinkTaskMap为空的问题。
            if (!map.isEmpty()) {
                if (CollectionUtils.isEmpty(jobIdList)) {
                    startOperatorJobGroup(sourceId, map, startType);
                }
                putFlinkTaskToMap(sourceId, riskEventRules);
            }
        }
    }

    /**
     * 校验策略规则涉及到的维表是否存在数据
     *
     * @param filterOperatorGroupStartVO
     * @return
     */
    public List<String> checkFilterBaseLineData(FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        List<String> result = new ArrayList<>();
        // 获取启动的策略
        String guids = filterOperatorGroupStartVO.getGuids();
        String[] guidArr = guids.split(",");
        for (String guid : guidArr) {
            // 通过策略查询规则
            List<String> filterCodes = getFilterCodes(guid);

            if (CollectionUtils.isNotEmpty(filterCodes)) {
                for (String filterCode : filterCodes) {
                    // 通过规则，查询涉及到的维表
                    Set<String> dimensions = getBaseLineDataForFilter(filterCode);
                    // 判断维表是否存在数据
                    boolean isCount = checkFilterCode(dimensions, filterCode, guid);
                    // 不存在，则添加策略
                    if (!isCount) {
                        result.add(guid);
                    }
                }
            }
        }
        return result;
    }

    public boolean checkFilterCode(Set<String> dimensions, String filterCode, String ruleId) {
        if (CollectionUtils.isNotEmpty(dimensions)) {
            for (String dimension : dimensions) {
                if (!dimension.startsWith("baseline") && !dimension.startsWith("base_line")) {
                    return true;
                }
                int baselineDataCount = queryBaseLineDataCountByFilterCode(dimension);
                if (baselineDataCount > 0) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 通过规则code统计维表中该规则的数据
     * 判断是否大于0
     * @param baseline
     * @return
     */
    public int queryBaseLineDataCountByFilterCode(String baseline) {
        if(baseline.startsWith("baseline")){
            int baseLineResult = getBaseLineResult(baseline);
            return baseLineResult;
        }else if(baseline.startsWith("base_line")){
            int baseLineProcess = getBaseLineProcess(baseline);
            return baseLineProcess;
        }else{
            return 0;
        }
    }


    /**
     * 判断是否存在维表数据
     * @param pattern
     * @return
     */
    private List<String> findTableNamesByPattern(String pattern) {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = '数据库名' AND table_name = ?";
        return jdbcTemplate.query(sql, new Object[]{pattern}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return resultSet.getString("table_name");
            }
        });
    }

    /**
     * 从基线过程表当中获得进度
     * @param baseline
     * @return
     */
    private int getBaseLineProcess(String baseline) {
        //查看学习进度
        List<String> tableNamesByPattern = findTableNamesByPattern(baseline);
        if(tableNamesByPattern.size()==0){
            return 0;
        }
        String sql = "select max(process) as process  from {0} ORDER BY insert_time desc;";
        sql = sql.replace("{0}", baseline);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if(list.size()==0){
            return 0;
        }else {
            Object process = list.get(0).get("process");
            if(process==null){
                return 0;
            }
            Float result = (Float)process;
            if(result<1.0){
                return 0;
            }else{
                return 1;
            }
        }
    }

    /**
     * 从baseline当中获取数据
     * @param baseline
     * @return
     */
    private int getBaseLineResult(String baseline) {
        String sql = "select count(1) as count from {0} ;";
        sql = sql.replace("{0}", baseline);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (CollectionUtils.isNotEmpty(list)) {
            return Integer.valueOf(String.valueOf(list.get(0).get("count")));
        }
        return 0;
    }

    /**
     * 通过规则ID，查询涉及到的维表信息
     *
     * @param filterCode
     * @return
     */
    public Set<String> getBaseLineDataForFilter(String filterCode) {
        FilterOpertorVO filterOpertorVO = getFilterOpertorVOByAnlysisId(filterCode);
        Set<String> dimensions = getDimensionTableNames(filterOpertorVO);
        return dimensions;
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

    /**
     * 通过策略id查询规则
     *
     * @param riskId
     * @return
     */
    public List<String> getFilterCodes(String riskId) {
        List<QueryCondition> ruleCondiotions = new ArrayList<>();
        ruleCondiotions.add(QueryCondition.eq("ruleId", riskId));
        ruleCondiotions.add(QueryCondition.eq("isStarted", "1"));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleCondiotions);
        List<String> filterCodes = ruleFilters.stream().map(RuleFilter::getFilterCode).collect(Collectors.toList());
        return filterCodes;
    }

    public List<String> startFilterCheckFilterBaseLineData(FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        // 判断维表数据是否配置
        List<String> ruleIds = checkFilterBaseLineData(filterOperatorGroupStartVO);
        List<String> ruleIdList = ruleIds.stream().distinct().collect(Collectors.toList());
        return ruleIdList;
    }

    public String checkRuleFilterBaseLineData(List<String> ruleIds) {
        if (CollectionUtils.isNotEmpty(ruleIds)) {
            List<String> ruleNames = new ArrayList<>();
            for (String ruleId : ruleIds) {
                riskEventRuleService.changeRiskEventRuleOnlyStatus(ruleId, "1");
                RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
                ruleNames.add(riskEventRule.getName());
            }
            if (CollectionUtils.isNotEmpty(ruleNames)) {
                logger.warn("当前策略[{}]存在维表未配置数据，请先配置，再行启动！", String.join(",", ruleNames));
                String result = "当前策略[" + String.join(",", ruleNames) + "]存在维表未配置数据，请先配置，再行启动！";
                return result;
            }
        }
        return null;
    }

    public void startFilterOperatorGroupByName(List<String> guids, List<String> ruleIdList) {
        // 计算差集
        List<String> cj = guids.stream().filter(item -> !ruleIdList.contains(item)).collect(Collectors.toList());
        logger.info("规则差集：{},guids={},ruleIdList={}", String.join(",", cj), String.join(",", guids), String.join(",", ruleIdList));
        if (CollectionUtils.isNotEmpty(cj)) {
            for (String ruleId : cj) {
                // 策略状态设置为已启动
                riskEventRuleService.changeRiskEventRuleOnlyStatus(ruleId, "1");
            }
            FilterOperatorGroupStartVO filterOperatorGroupStartVO = new FilterOperatorGroupStartVO();
            filterOperatorGroupStartVO.setGuids(String.join(",", cj));
            Map<String, List<String>> guidGroup = ruleGroupBySecondLevel(filterOperatorGroupStartVO, "_start");
            //按二级事件id分组启动规则
            pushFlinkStartTaskQueue(guidGroup);
        }

    }


    public String getRuleFilterSourceStatus(FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        String guid = filterOperatorGroupStartVO.getGuids();
        if (StringUtils.isEmpty(guid)) {
            return null;
        }
        String[] guidArr = guid.split(",");
        String sql = "select t2.source as source from rule_filter t1,filter_operator t2 where t1.filter_code = t2.code and t1.isStarted = 1 and t2.delete_flag =1 and rule_id in ('{0}')";
        String guidsStr = String.join("','", guidArr);
        sql = sql.replace("{0}", guidsStr);
        List<Map<String, Object>> sourceList = jdbcTemplate.queryForList(sql);
        List<String> sourceIds = new ArrayList<>();
        for (Map<String, Object> map : sourceList) {
            String sourceStr = String.valueOf(map.get("source"));
            List<String> sources = gson.fromJson(sourceStr, new TypeToken<List<String>>() {
            }.getType());
            sourceIds.addAll(sources);
        }
        sourceIds = sourceIds.stream().distinct().collect(Collectors.toList());

        List<String> msgs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceIds)) {
            for (String sourceId : sourceIds) {
                String msg = filterSourceStatusService.getFilterSourceStatusMsgByRedis(sourceId);
                if (StringUtils.isNotBlank(msg)) {
                    msgs.add(msg);
                }
            }
        }
        return String.join(",", msgs);
    }

    public boolean stopFlinkJobByEventId(FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        //将实时任务和离线任务分离开来 filterOperatorGroupStartVO中的guids已经剔除了离线的
        List<String> offlineRuleIdList = commonFilterOperatorService.filterOfflineFlinkJob(filterOperatorGroupStartVO);
        if (offlineRuleIdList.size() > 0) {
            //停止离线flink任务就是将离线定时任务移除调度周期即可
            commonFilterOperatorService.removeOfflineFlinkJobByRuleIdList(offlineRuleIdList);
        }
        if (StringUtils.isNotEmpty(filterOperatorGroupStartVO.getGuids())) {
            //实时flink任务
            //遍历修改规则和分析器状态
            Map<String, List<String>> guidGroup = ruleGroupBySecondLevel(filterOperatorGroupStartVO, "_stop");
            //我后续再弄

            pushFlinkStartTaskQueue(guidGroup);
        }
        return true;
    }

    public Map<String, List<String>> ruleGroupBySecondLevel(FilterOperatorGroupStartVO filterOperatorGroupStartVO, String action) {
        // 获取当前启动方式
        String startType = ruleFlinkTypeService.getRuleFlinkStart();

        //二级事件分类id
        String riskEventId = filterOperatorGroupStartVO.getRiskEventId();
        //告警规则guid
        String guids = filterOperatorGroupStartVO.getGuids();
        //规则关联数据源id
        Integer sourceId = filterOperatorGroupStartVO.getSourceId();

        if (STARTTYPE_DB.equals(startType)) {
            // 数据源启动
            Map<String, List<String>> guidGroup = putStartTaskToJobForDb(guids, action, sourceId);
            logger.info("数据源{} 传入参数：{}", action, gson.toJson(guidGroup));
            return guidGroup;
        } else {
            // 分类启动
            Map<String, List<String>> guidGroup = putStartTaskToJobForCategory(riskEventId, guids, action);
            logger.info("分类{} 传入参数：{}", action, gson.toJson(guidGroup));
            return guidGroup;
        }
    }

    /**
     * put数据源任务
     *
     * @param guids
     * @param action
     * @return
     */
    public Map<String, List<String>> putStartTaskToJobForDb(String guids, String action, Integer sourceId) {
        Map<String, List<String>> guidGroup = new HashMap<>();
        if (StringUtils.isBlank(guids)) {
            throw new AlarmDealException(ResultCodeEnum.Field_VALIDATE_ERROR.getCode(), "参数异常，guid为空");
        }
        String[] guidArr = guids.split(",");
        for (String guid : guidArr) {
            List<FilterOperator> filterOperators = queryFilterByRuleId(guid);
            //有的策略没有绑定规则
            if (filterOperators == null) {
                return guidGroup;
            }
            for (FilterOperator filterOperator : filterOperators) {
                //20231010修改：前端不会传sourceId,这个比较存在问题，从而导致界面启动guidGroup永远是空的。
                List<FilterOperator> filterOperatorList = new ArrayList<>();
                filterOperatorList.add(filterOperator);
                String key = getSourceIds(filterOperatorList);
                List<String> list = new ArrayList<>();
                if (StringUtils.isNotBlank(key)) {
                    key = key + action;
                    if (guidGroup.containsKey(key)) {
                        list = guidGroup.get(key);
                    }
                    list.add(guid);
                    list = list.stream().distinct().collect(Collectors.toList());
                    guidGroup.put(key, list);
                }
            }
        }
        return guidGroup;
    }


    /**
     * put 分类任务
     *
     * @param riskEventId
     * @param guids
     * @param action
     * @return
     */
    public Map<String, List<String>> putStartTaskToJobForCategory(String riskEventId, String guids, String action) {
        Map<String, List<String>> guidGroup = new HashMap<>();
        if (StringUtils.isNotBlank(riskEventId)) {
            List<String> guidList = getFlinkCodes(riskEventId);
            guidGroup.put(riskEventId + action, guidList);
        } else if (StringUtils.isNotBlank(guids)) {
            String[] guidArr = guids.split(",");
            //将所有的guid，按二级事件分类分组
            for (String guid : guidArr) {
                RiskEventRule riskEventRule = riskEventRuleService.getOne(guid);
                changeRiskRuleStatus(riskEventRule, action);
                riskEventId = riskEventRule.getRiskEventId();
                riskEventId = getSecondLevelEventId(riskEventId);
                if (StringUtils.isNotBlank(riskEventId)) {
                    //某个二级事件分类下的规则guid集合
                    List<String> list = new ArrayList<>();
                    String key = riskEventId + action;
                    if (guidGroup.containsKey(key)) {
                        list = guidGroup.get(key);
                    }
                    list.add(guid);
                    list = list.stream().distinct().collect(Collectors.toList());
                    guidGroup.put(key, list);
                }
            }
        }
        return guidGroup;
    }

    public void changeRiskRuleStatus(RiskEventRule riskEventRule, String action) {
        if ("_stop".equals(action)) {
            riskEventRule.setStarted("0");
        } else {
            riskEventRule.setStarted("1");
        }
        riskEventRuleService.save(riskEventRule);
    }

    private String getSecondLevelEventId(String riskEventId) {
        EventCategory eventCategory = eventCategoryService.getOne(riskEventId);
        String codeLevel = eventCategory.getCodeLevel();
        int length = codeLevel.split("/").length;
        riskEventId = eventCategory.getParentId();
        if (length > 3) {
            getSecondLevelEventId(riskEventId);
        }
        return riskEventId;
    }

}
