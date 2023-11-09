package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RuleFilterRequest;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.SyncRequest;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.res.SyncRes;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilterVo;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Exchanges;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Tables;
import com.vrv.vap.alarmdeal.business.analysis.vo.ParamsColumns;
import com.vrv.vap.alarmdeal.business.analysis.vo.RiskRuleEditVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterOpertorVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.frameworks.feign.FeignCache;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventRuleParams;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.Attach;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableFieldService;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventAlarmSettingService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.DimensionTableColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventCategoryUrgeTreeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventLogFieldVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventLogTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ParamsColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ParamsContent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ParamsData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.QueryParam;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RelateSqlVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RiskEventRuleQueryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RiskRuleListVO;
import com.vrv.vap.alarmdeal.frameworks.util.RedissonSingleUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Api(description = "风险事件规则")
@RestController
@RequestMapping(value = "/riskEventRule")
public class RiskEventRuleController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(RiskEventRuleController.class);

    @Autowired
    private RiskEventRuleService riskEventRuleService;

    @Autowired
    private MapperUtil mapper;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private EventAlarmSettingService eventAlarmSettingService;

    @Autowired
    private EventCategoryService eventCategoryService;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private AlarmAnalysisService alarmAnalysisService;

    @Autowired
    private DimensionTableFieldService dimensionTableFieldService;

    @Autowired
    private FeignCache feignCache;

    @Autowired
    private DimensionTableService dimensionTableService;

    @Autowired
    private RuleFilterService ruleFilterService;

//    @Autowired
//    private RedisUtil redisUtil;

    @Autowired
    private RedissonSingleUtil redissonSingleUtil;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    /**
     * 告警规则列表页面
     *
     * @param riskEventRuleQueryVO
     * @param pageReq
     * @return
     */
    @PostMapping(value = "/riskEventRulePager")
    @ApiOperation(value = "获得规则列表", notes = "")
    @SysRequestLog(description = "策略信息-获得策略列表", actionType = ActionType.SELECT, manually = false)
    public PageRes<RiskRuleListVO> getRiskEventRulePager(@RequestBody RiskEventRuleQueryVO riskEventRuleQueryVO,
                                                         PageReq pageReq) {
        pageReq.setCount(riskEventRuleQueryVO.getCount_());
        pageReq.setStart(riskEventRuleQueryVO.getStart_());
        pageReq.setOrder("createdTime");
        pageReq.setBy("desc");
        PageRes<RiskRuleListVO> pageRes = riskEventRuleService.getRiskEventRulePager(riskEventRuleQueryVO, pageReq.getPageable());

        return pageRes;
    }

    @GetMapping(value = "/riskEventRuleFilterData/{id}")
    @ApiOperation(value = "通过策略获取规则信息", notes = "")
    @SysRequestLog(description = "策略信息-通过策略获取规则信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<RuleFilterVo>> riskEventRuleFilterData(@PathVariable("id") String id) {
        List<RuleFilterVo> res = riskEventRuleService.riskEventRuleFilterData(id);
        return ResultUtil.successList(res);
    }

    @GetMapping(value = "/riskEventRuleList")
    @ApiOperation(value = "获得规则集合", notes = "")
    @SysRequestLog(description = "策略信息-获得策略集合", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String, Object>>> riskEventRuleList() {
        List<RiskEventRule> list = riskEventRuleService.findAll();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (RiskEventRule riskEventRule : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("guid", riskEventRule.getId());
            map.put("name", riskEventRule.getName());
            mapList.add(map);
        }
        Result<List<Map<String, Object>>> result = ResultUtil.successList(mapList);
        return result;
    }

    @PostMapping(value = "/riskEventRuleCount")
    @ApiOperation(value = "规则数量", notes = "")
    @SysRequestLog(description = "策略信息-策略数量", actionType = ActionType.SELECT, manually = false)
    public Result<Long> riskEventRuleCount() {
        long count = riskEventRuleService.count();
        Result<Long> result = ResultUtil.success(count);
        return result;
    }

    /**
     * 添加告警规则
     *
     * @param riskRuleEditVO
     * @return
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增告警规则", notes = "")
    @SysRequestLog(description = "策略信息-新增告警策略", actionType = ActionType.ADD, manually = false)
    public Result<RiskRuleListVO> addRiskEventRule(@RequestBody RiskRuleEditVO riskRuleEditVO) {
        Result<RiskRuleListVO> result = riskEventRuleService.addRiskEventRule(riskRuleEditVO);
        return result;
    }

    /**
     * 编辑告警规则
     *
     * @param riskRuleEditVO
     * @return
     */
    @PostMapping(value = "/edit")
    @ApiOperation(value = "编辑告警规则", notes = "")
    @SysRequestLog(description = "策略信息-编辑告警策略", actionType = ActionType.UPDATE, manually = false)
    public Result<RiskRuleListVO> editRiskEventRule(@RequestBody RiskRuleEditVO riskRuleEditVO) {
        Result<RiskRuleListVO> result = riskEventRuleService.editRiskEventRule(riskRuleEditVO);
        return result;
    }

    /**
     * 删除告警规则
     *
     * @param riskRuleIdVO
     * @return
     */
    @PostMapping(value = "/del")
    @ApiOperation(value = "删除告警规则", notes = "")
    @SysRequestLog(description = "策略信息-删除告警策略", actionType = ActionType.DELETE, manually = false)
    public Result<Boolean> delRiskEventRules(@RequestBody RiskRuleIdVO riskRuleIdVO) {
        List<String> ids = riskRuleIdVO.getIds();
        Result<Boolean> result = riskEventRuleService.delRiskEventRules(ids);
        return result;
    }

    /**
     * 告警规则复制
     */
    @GetMapping(value = "/copyRiskEventRule/{guid}")
    @ApiOperation(value = "复制告警规则", notes = "")
    @SysRequestLog(description = "策略信息-复制告警策略", actionType = ActionType.ADD, manually = false)
    public Result<RiskRuleListVO> copyRiskEventRule(@PathVariable("guid") String guid) {
        Result<RiskRuleListVO> result = riskEventRuleService.copyRiskEventRule(guid);
        return result;
    }

    /**
     * 改变告警规则状态
     *
     * @param riskRuleIdVO
     * @return
     */
    @PostMapping(value = "/changeRiskEventRuleStatus")
    @ApiOperation(value = "改变告警规则状态", notes = "")
    @SysRequestLog(description = "策略信息-改变告警策略状态", actionType = ActionType.UPDATE, manually = false)
    public Result<RiskRuleListVO> changeRiskEventRuleStatus(@RequestBody RiskRuleIdVO riskRuleIdVO) {
        Result<RiskRuleListVO> result = riskEventRuleService.changeRiskEventRule(riskRuleIdVO);
        return result;
    }

    /**
     * 判断rulecode是否重复
     *
     * @param riskRuleEditVO
     * @return
     */
    @PostMapping(value = "/judgeRuleCodeIsRepeat")
    @ApiOperation(value = "判断rulecode是否重复", notes = "")
    @SysRequestLog(description = "策略信息-判断rulecode是否重复", actionType = ActionType.SELECT, manually = false)
    public Result<Boolean> judgeRuleCodeIsRepeat(@RequestBody RiskRuleEditVO riskRuleEditVO) {
        String ruleCode = riskRuleEditVO.getRuleCode();
        String id = riskRuleEditVO.getId();
        Result<Boolean> result = riskEventRuleService.judgeRuleCodeIsRepeat(ruleCode, id);
        return result;
    }

    @PostMapping(value = "/getEventLogTable")
    @ApiOperation(value = "选择过滤日志表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagName", value = "标签", required = true, dataType = "String")
    })
    @SysRequestLog(description = "策略信息-选择过滤日志表", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventLogTable>> getEventLogTable(@RequestBody Map<String, Object> map) {
        Object tagNameObj = map.get("tagName");
        if (tagNameObj != null) {
            String tagName = tagNameObj.toString();
            Result<List<EventLogTable>> result = riskEventRuleService.getEventLogTable(tagName);
            return result;
        } else {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "tag标签不能为空");
        }
    }

    @PostMapping(value = "/getEventLogFieldVO")
    @ApiOperation(value = "获得事件日志对应的字段", notes = "")
    @SysRequestLog(description = "策略信息-获得事件日志对应的字段", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventLogFieldVO>> getEventLogFieldVO(@RequestBody Map<String, Object> map) {
        if (map.containsKey("tableName") && map.get("tableName") != null && map.containsKey("sourceType") && map.get("sourceType") != null) {
            String tableName = map.get("tableName").toString();
            String sourceType = map.get("sourceType").toString();
            Result<List<EventLogFieldVO>> result = riskEventRuleService.getEventLogFieldVO(tableName, sourceType);
            return result;
        } else {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "查询参数出现错误，请检查");
        }
    }

    @PostMapping(value = "/getDynamicRelateSql")
    @ApiOperation(value = "获得动态关联sql", notes = "")
    @SysRequestLog(description = "策略信息-获得动态关联sql", actionType = ActionType.SELECT, manually = false)
    public Result<String> getDynamicRelateSql(@RequestBody Map<String, Object> map) {
        Object tableNameObject = map.get("tableName");
        Object relateListObject = map.get("relateList");
        if (tableNameObject != null && relateListObject != null) {
            String tableName = tableNameObject.toString();
            List<Map<String, Object>> list = (List<Map<String, Object>>) relateListObject;
            List<RelateSqlVO> mapList = mapper.mapList(list, RelateSqlVO.class);
            String dynamicRelateSql = riskEventRuleService.getDynamicRelateSql(tableName, mapList);
            Result<String> result = ResultUtil.success(dynamicRelateSql);
            return result;
        }
        return ResultUtil.success("");
    }

    @GetMapping(value = "/getOrginalLogPath/{tableName}")
    @ApiOperation(value = "获得原始日志路径", notes = "")
    @SysRequestLog(description = "策略信息-获得原始日志路径", actionType = ActionType.SELECT, manually = false)
    public String getOrginalLogPath(@PathVariable String tableName) {
        String orginalLogPath = riskEventRuleService.getOrginalLogPath(tableName);
        return orginalLogPath;
    }

    @GetMapping(value = "/getTagsByRuleId/{ruleId}")
    @ApiOperation(value = "ruleId", notes = "根据规则Id获得对应标签")
    @SysRequestLog(description = "策略信息-根据规则Id获得对应标签", actionType = ActionType.SELECT, manually = false)
    public Result<String> getTagsByRuleId(@PathVariable String ruleId) {
        RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
        String knowledgeTag = riskEventRule.getKnowledgeTag();
        Result<String> result = ResultUtil.success(knowledgeTag);
        return result;
    }

    /**
     * 告警规则下载 guids   passwd
     */
    @PostMapping("exportRiskEventRule")
    @ApiOperation(value = "guids", notes = "告警规则下载")
    @SysRequestLog(description = "策略信息-告警策略下载", actionType = ActionType.EXPORT, manually = false)
    public Result<String> exportRiskEventRule() {
        Result<String> result = riskEventRuleService.exportRiskEventRule();
        return result;
    }

    /**
     * 告警规则浏览器下载
     */
    @GetMapping("/downRiskEventRuleExcel/{fileName:.+}")
    @ApiOperation(value = "fileName", notes = "告警规则浏览器导出")
    @SysRequestLog(description = "策略信息-告警策略浏览器导出", actionType = ActionType.EXPORT, manually = false)
    public void test(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        logger.info("path:{}", fileConfiguration.getFilePath());
        FileUtil.downLoadFile(fileName, fileConfiguration.getFilePath(), response);

    }

    /**
     * 导入告警规则信息
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/importRiskEventRule")
    @ApiOperation(value = "导入告警规则信息", notes = "")
    @SysRequestLog(description = "策略信息-导入告警策略信息", actionType = ActionType.IMPORT, manually = false)
    public Result<Boolean> importFlowFile(@RequestParam("file") CommonsMultipartFile file) {
        Result<Boolean> result = null;
        result = riskEventRuleService.importRiskEventRuleInfo(file);
        return result;
    }

    @PutMapping({"/createRuleInstance"})
    @ApiOperation(value = "创建规则实例", notes = "")
    @SysRequestLog(description = "策略信息-创建策略实例", actionType = ActionType.ADD, manually = false)
    public Result<RiskRuleListVO> createRuleInstance(@RequestBody Map<String, Object> map) {
        RiskRuleListVO riskRuleListVO = riskEventRuleService.createRuleInstance(map);
        return ResultUtil.success(riskRuleListVO);
    }

    @PostMapping({"/editRuleInstance"})
    @ApiOperation(value = "编辑规则实例", notes = "")
    @SysRequestLog(description = "策略信息-编辑策略实例", actionType = ActionType.UPDATE, manually = false)
    public Result<RiskRuleListVO> editRuleInstance(@RequestBody Map<String, Object> map) {
        RiskRuleListVO riskRuleListVO = riskEventRuleService.editRuleInstance(map);
        return ResultUtil.success(riskRuleListVO);
    }

    /**
     * 告警规则下载 guids   passwd
     */
    @RequestMapping(value = "AlarmEventDealSetting",method = RequestMethod.POST,produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "处置督促信息保存", notes = "处置督促信息保存")
    @ResponseBody
    @SysRequestLog(description = "策略信息-处置督促信息保存", actionType = ActionType.ADD, manually = false)
    public Result<List<EventAlarmSetting>> alarmEventDealSetting(@RequestBody List<EventAlarmSetting> items) {
        logger.info("处置督促信息保存 alarmEventDealSetting start");
        Iterable<EventAlarmSetting> save = eventAlarmSettingService.save(items);
        List<EventAlarmSetting> result = new ArrayList<>();
        save.forEach(item -> {
            if (StringUtils.isEmpty(item.getGuid())) {
                item.setGuid(UUIDUtils.get32UUID());
            }
            result.add(item);
        });

        List<EventAlarmSetting> eventCategoryList = eventAlarmSettingService.queryAllEventAlarmSetting();
        if(CollectionUtils.isNotEmpty(eventCategoryList)){
            // 查询新数据后，更新缓存
            Map<String, List<EventAlarmSetting>> eventAlarmSettingMap = eventCategoryList.parallelStream().collect(Collectors.groupingBy(EventAlarmSetting::getGuid));
            CommomLocalCache.put("eventAlarmSetting",eventAlarmSettingMap,2, TimeUnit.HOURS);
        }
        return ResultUtil.success(result);
    }

    /**
     * 获得事件分类树
     *
     * @return
     */
    @GetMapping("/getEventCategoryTree")
    @ApiOperation(value = "获得事件分类树", notes = "")
    @SysRequestLog(description = "策略信息-获得事件分类树", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventCategoryUrgeTreeVO>> getEventCategoryTree() {

        // 获得规则类和模型类
        Sort sort = Sort.by(Sort.Direction.ASC, "orderNum");
        List<EventCategory> list = eventCategoryService.findAll(sort);
        List<EventCategoryUrgeTreeVO> mapList = mapper.mapList(list, EventCategoryUrgeTreeVO.class);

        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("linkType", "category"));
        List<EventAlarmSetting> findAll = eventAlarmSettingService.findAll(conditions);
        mapList.forEach(category -> {
            category.setNodeType("category");
            for (EventAlarmSetting setting : findAll) {
                if (category.getKey().equals(setting.getLinkGuid())) {
                    category.setUrgeInfo(setting);
                    break;
                }
            }
        });

        List<EventCategoryUrgeTreeVO> buildTree = buildTree(mapList, "0");

        List<QueryCondition> cons = new ArrayList<>();
        cons.add(QueryCondition.eq("linkType", "rule"));
        List<EventAlarmSetting> ruleSeeting = eventAlarmSettingService.findAll(cons);

        appendRuleTreenode(buildTree, ruleSeeting);

        return ResultUtil.success(buildTree);
    }

    public void appendRuleTreenode(List<EventCategoryUrgeTreeVO> tree, List<EventAlarmSetting> ruleSeeting) {
        for (EventCategoryUrgeTreeVO vo : tree) {
            List<EventCategoryUrgeTreeVO> children = vo.getChildren();
            if (children != null && children.isEmpty()) {
                //补策略
                PageReq pageReq = new PageReq();
                pageReq.setCount(10000);
                pageReq.setStart(0);
                pageReq.setOrder("createdTime");
                pageReq.setBy("desc");
                RiskEventRuleQueryVO riskEventRuleQueryVO = new RiskEventRuleQueryVO();
                riskEventRuleQueryVO.setWarmType(vo.getCodeLevel());
                PageRes<RiskRuleListVO> pageRes = riskEventRuleService.getRiskEventRulePager(riskEventRuleQueryVO, pageReq.getPageable());
                if (pageRes != null) {
                    List<RiskRuleListVO> list = pageRes.getList();
                    if (list != null && !list.isEmpty()) {
                        children = new ArrayList<>();
                        vo.setChildren(children);
                        vo.setIsLeaf(true);
                        int index = 1;

                        for (RiskRuleListVO rule : list) {
                            EventCategoryUrgeTreeVO item = new EventCategoryUrgeTreeVO();
                            item.setNodeType("rule");
                            item.setKey(rule.getId());
                            item.setIsLeaf(false);
                            item.setParentId(vo.getKey());
                            item.setCodeLevel(vo.getCodeLevel());
                            item.setTitle(rule.getName());
                            item.setPriorityLevel(rule.getLevelstatus());
                            item.setOrderNum(index++);

                            for (EventAlarmSetting setting : ruleSeeting) {
                                if (rule.getId().equals(setting.getLinkGuid())) {
                                    item.setUrgeInfo(setting);
                                    break;
                                }
                            }

                            children.add(item);
                        }
                    } else {
                        vo.setIsLeaf(false);
                    }
                }
            } else {
                appendRuleTreenode(children, ruleSeeting);
            }
        }
    }

    public static List<EventCategoryUrgeTreeVO> buildTree(Collection<EventCategoryUrgeTreeVO> treeNodes, String topParentId) {
        List<EventCategoryUrgeTreeVO> result = new ArrayList<>();
        Map<String, EventCategoryUrgeTreeVO> tmp = new HashMap<>();
        for (EventCategoryUrgeTreeVO node : treeNodes) {
            tmp.put(node.getKey(), node);
            node.setIsLeaf(false);
            node.setChildren(new ArrayList<>());
        }

        for (EventCategoryUrgeTreeVO cNode : treeNodes) {
            String parentId = cNode.getParentId();
            if (parentId != null && parentId.equals(topParentId)) {
                //result.add(cNode);

                appendNodeForOrder(result, cNode);

            } else {
                EventCategoryUrgeTreeVO parentNode = tmp.get(parentId);
                if (parentNode != null) {
                    //parentNode.getChildren().add(cNode);
                    parentNode.setIsLeaf(true);
                    appendNodeForOrder(parentNode.getChildren(), cNode);
                }
            }
        }

        return result;
    }

    private static void appendNodeForOrder(List<EventCategoryUrgeTreeVO> result, EventCategoryUrgeTreeVO cNode) {
        if (result.isEmpty()) {
            result.add(cNode);
        } else {
            int index = 0;
            for (EventCategoryUrgeTreeVO item : result) {
                if (cNode.getOrderNum() <= item.getOrderNum()) {
                    break;
                } else {
                    index++;
                }
            }
            result.add(index, cNode);
        }
    }

    @GetMapping("/countStartRule")
    @SysRequestLog(description = "策略信息-统计启动策略", actionType = ActionType.SELECT, manually = false)
    public Result<Integer> countStartRule() {
        Integer sum = riskEventRuleService.countStartRule();
        return ResultUtil.success(sum);
    }

    @GetMapping("/getEventRuleParams/{ruleId}")
    @ApiOperation(value = "查询规则参数", notes = "查询规则参数")
    @SysRequestLog(description = "策略信息-查询策略规则参数", actionType = ActionType.SELECT, manually = false)
    public Result<EventRuleParams> getEventRuleParams(@PathVariable("ruleId") String ruleId) {
        EventRuleParams result = getRuleParams(ruleId);
        return ResultUtil.success(result);
    }

    private EventRuleParams getRuleParams(String ruleId) {
        EventRuleParams result = new EventRuleParams();
        result.setEventRuleId(ruleId);
        List<ParamsContent> paramsContents = new ArrayList<>();

        //查询数据库
        FilterOpertorVO filterOpertorVO = filterOperatorService.getFilterOpertorVOByAnlysisId(ruleId);
        if (filterOpertorVO != null) {
            FilterConfigObject filterConfig = filterOpertorVO.getFilterConfig();
            Tables[][] tables = filterConfig.getTables();
            Exchanges[][] exchanges = filterConfig.getExchanges();

            Set<String> dimensionTableNames = getDimensionTableNames(filterOpertorVO);

            logger.debug("dimensionTableNames:{}" + JSON.toJSONString(dimensionTableNames));
            for (String dimensionTableName : dimensionTableNames) {

                List<DimensionTableColumn> dimensionTableColumns = alarmAnalysisService.getDimensionTableColumns(dimensionTableName);
                List<DimensionTableColumn> colunmIntersection = getColunmIntersection(tables, exchanges,
                        dimensionTableColumns);
                logger.debug("colunmIntersection:{}" + JSON.toJSONString(colunmIntersection));
                ParamsContent paramsContent = getParamsContent(dimensionTableName, colunmIntersection);
                if (paramsContent != null) {
                    // 查看维表 是否存在数据 paramsContent
                    ParamsData paramsData = new ParamsData();

                    List<DataRow> dataRows = alarmAnalysisService.getDimensionTableData(dimensionTableName, ruleId,filterOpertorVO.getCode(), paramsContent.getParamsColumns().getDisplay());
                    List<DataRow> baselineDataRows = alarmAnalysisService.getBaselineDataRows(dimensionTableName);
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

        result.setParamsContents(paramsContents);

        result.setParamsType(0);

        if (paramsContents.isEmpty()) {
            result.setParamsType(0);
        } else if (paramsContents.size() == 1) {
            result.setParamsType(1);
        } else {
            result.setParamsType(2);
        }
        return result;
    }

    @PostMapping("/getEventRuleParamsNew")
    @ApiOperation(value = "查询规则参数", notes = "查询规则参数")
    @SysRequestLog(description = "策略信息-查询策略规则参数信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventRuleParams>> getEventRuleParamsNew(@RequestBody RuleFilterRequest ruleFilterRequest) {
        String ruleId = ruleFilterRequest.getRuleId();
        if(StringUtils.isBlank(ruleId)){
            EventRuleParams eventRuleParams = getRuleParams(ruleFilterRequest.getFilterCode());
            eventRuleParams.setFilterCode(ruleFilterRequest.getFilterCode());
            return ResultUtil.successList(Arrays.asList(eventRuleParams));
        }
        List<EventRuleParams> result = new ArrayList<>();
        //查询数据库
        List<QueryCondition> ruleParams = new ArrayList<>();
        ruleParams.add(QueryCondition.eq("ruleId",ruleId));
        ruleParams.add(QueryCondition.eq("filterCode",ruleFilterRequest.getFilterCode()));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleParams);
        if(CollectionUtils.isNotEmpty(ruleFilters)){
            List<String> filterCodes = ruleFilters.stream().map(RuleFilter::getFilterCode).distinct().collect(Collectors.toList());
            for(String filterCode:filterCodes){
                EventRuleParams eventRuleParams = getEventRuleParam(ruleId,filterCode);
                eventRuleParams.setEventRuleId(ruleId);
                eventRuleParams.setRuleCode(ruleId);
                result.add(eventRuleParams);
            }
        }
        return ResultUtil.successList(result);
    }

    private EventRuleParams getEventRuleParam(String ruleId,String filterCode){
        EventRuleParams result = new EventRuleParams();
        result.setFilterCode(filterCode);
        List<ParamsContent> paramsContents = riskEventRuleService.getParamsContents(ruleId,filterCode);
        result.setParamsContents(paramsContents);

        result.setParamsType(0);

        if (paramsContents.isEmpty()) {
            result.setParamsType(0);
        } else if (paramsContents.size() == 1) {
            result.setParamsType(1);
        } else {
            result.setParamsType(2);
        }
        return result;
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
                            if (StringUtils.isEmpty(attach.getOptions())) {
                                continue;
                            }
                            JSONObject options = JSONObject.parseObject(attach.getOptions());
                            String dimensionFieldName = options.getString("dimensionFieldName");
                            List<String> fieldNames = new ArrayList<>(Arrays.asList(dimensionFieldName.split(",")));
                            if (fieldNames.contains(column.getColumnName())) {
                                if (column.getColumnName().contains("user_type")) {
                                    logger.debug(attach.getOptions());
                                }
                                isHave = true;
                                break;
                            }
//                            if (attach.getOptions().contains("\"" + column.getColumnName() + "\"")
//                                    || attach.getOptions().contains("." + column.getColumnName() + "\"")) {
//                                if (column.getColumnName().contains("user_type")) {
//                                    logger.debug(attach.getOptions());
//                                }
//                                isHave = true;
//                                break;
//                            }
                        }
                    }
                }
            }

//            for (Exchanges[] exchangArr : exchanges) {
//                if (isHave) {
//                    break;
//                }
//                for (Exchanges exchang : exchangArr) {
//                    if (isHave) {
//                        break;
//                    }
//                    if (StringUtils.isEmpty(exchang.getOptions())) {
//                        continue;
//                    }
//                    if (exchang.getOptions().contains("\"" + column.getColumnName() + "\"") || exchang.getOptions().contains("." + column.getColumnName() + "\"")) {
//                        if (column.getColumnName().contains("user_type")) {
//                            logger.debug(exchang.getOptions());
//                        }
//                        isHave = true;
//                        break;
//                    }
//                }
//
//            }

            if (isHave) {
                colunmIntersection.add(column);
            }

        }
        return colunmIntersection;
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

    private ParamsContent getParamsContent(String dimensionTableName, List<DimensionTableColumn> dimensionTableColumns) {
        if (!StringUtils.isEmpty(dimensionTableName)) {
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

                    if (StringUtils.isNotEmpty(dimensionTableField.getFormatType()) || StringUtils.isNotEmpty(dimensionTableField.getEnumType())) {

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
                            if (!StringUtils.isEmpty(dimensionTableField.getEnumType())) {

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

    @PostMapping("saveEventRuleParams")
    @ApiOperation(value = "保存规则参数", notes = "保存规则参数")
    @SysRequestLog(description = "策略信息-保存策略规则参数", actionType = ActionType.ADD, manually = false)
    public Result<EventRuleParams> saveEventRuleParams(@RequestBody EventRuleParams item) {

        List<ParamsContent> paramsContents = item.getParamsContents();
        if (paramsContents != null && !paramsContents.isEmpty()) {
            for (ParamsContent paramsContent : paramsContents) {

                ParamsData paramsData = paramsContent.getParamsData();

                if (StringUtils.isEmpty(paramsContent.getDimensionTableName())) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：维表名称为必传参数");
                }

                List<DataRow> display = paramsData.getDisplay();
                //通过 row  向表中添加数据
                alarmAnalysisService.saveDimensionTableData(paramsContent.getDimensionTableName(), item.getRuleCode(),item.getFilterCode(), display,false);
                // 清除redis缓存
                redissonSingleUtil.deleteByPrex(paramsContent.getDimensionTableName());
            }
        }

        return ResultUtil.success(item);
    }

    @PostMapping("/syncDimensionData")
    @ApiOperation(value = "手动同步维表数据", notes = "手动同步维表数据")
    @SysRequestLog(description = "策略信息-手动同步维表数据", actionType = ActionType.UPDATE, manually = false)
    public Result<Boolean> syncDimensionData(@RequestBody SyncRequest syncRequest){
        boolean isSave = riskEventRuleService.syncDimensionData(syncRequest);
        return ResultUtil.success(isSave);
    }

    @PutMapping("/syncDimension")
    @ApiOperation(value = "保存同步维表信息", notes = "保存同步维表信息")
    @SysRequestLog(description = "策略信息-保存同步维表信息", actionType = ActionType.ADD, manually = false)
    public Result<Boolean> saveSyncDimension(@RequestBody SyncRequest syncRequest){
        boolean isSave = riskEventRuleService.saveSyncDimension(syncRequest);
        return ResultUtil.success(isSave);
    }

    @PostMapping("/syncDimension")
    @ApiOperation(value = "查询同步维表信息", notes = "查询同步维表信息")
    @SysRequestLog(description = "策略信息-查询同步维表信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<SyncRes>> querySyncDimension(@RequestBody SyncRequest syncRequest){
        List<SyncRes> res = riskEventRuleService.querySyncDimension(syncRequest);
        return ResultUtil.success(res);
    }

}

