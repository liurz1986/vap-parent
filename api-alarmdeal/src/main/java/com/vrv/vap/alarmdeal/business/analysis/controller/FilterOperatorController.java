package com.vrv.vap.alarmdeal.business.analysis.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.analysis.model.DimensionTable;
import com.vrv.vap.alarmdeal.business.analysis.model.RuleModelOfAssetType;
import com.vrv.vap.alarmdeal.business.analysis.server.CommonFilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.RuleModelOfAssetTypeService;
import com.vrv.vap.alarmdeal.business.analysis.vo.FilterOperatorGroupStartVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterOpertorVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterPagerVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.ParamConfigVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/filterOperator")
public class FilterOperatorController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(FilterOperatorController.class);

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RuleModelOfAssetTypeService ruleModelOfAssetTypeService;

    @Autowired
    private RiskEventRuleService riskEventRuleService;
    @Autowired
    private CommonFilterOperatorService commonFilterOperatorService;


    @Value("${flink.slots}")
    private int slots;

    @GetMapping(value = "/getFilterOperatorVO/{analysisId}")
    @ApiOperation(value = "根据分析Id获得对应的数据实体数据", notes = "")
    @SysRequestLog(description = "根据分析Id获得对应的数据实体数据", actionType = ActionType.SELECT, manually = false)
    public Result<FilterOpertorVO> getFilterOperatorVO(@PathVariable String analysisId) {
        FilterOpertorVO filterOpertorVO = filterOperatorService.getFilterOpertorVOByAnlysisId(analysisId);
        Result<FilterOpertorVO> result = ResultUtil.success(filterOpertorVO);
        return result;
    }

    @PostMapping(value = "/filterOperator")
    @ApiOperation(value = "获得过滤分页列表", notes = "")
    @SysRequestLog(description = "获得过滤分页列表", actionType = ActionType.SELECT, manually = false)
    public PageRes<FilterOpertorVO> gethreatLibraryPager(@RequestBody FilterPagerVO filterPagerVO) {
        PageRes<FilterOpertorVO> pageRes = filterOperatorService.getFilterOperatorPager(filterPagerVO);
        return pageRes;
    }

    @PutMapping("/filterOperator")
    @ApiOperation(value = "添加规则过滤器", notes = "")
    @SysRequestLog(description = "添加规则过滤器", actionType = ActionType.ADD, manually = false)
    public Result<FilterOpertorVO> addFilterOperator(@RequestBody FilterOpertorVO filterOpertorVO) {
        Result<FilterOpertorVO> result = filterOperatorService.addFilterOperator(filterOpertorVO);
        return result;
    }

    @PatchMapping("/filterOperator")
    @ApiOperation(value = "编辑规则过滤器", notes = "")
    @SysRequestLog(description = "编辑规则过滤器", actionType = ActionType.UPDATE, manually = false)
    public Result<FilterOpertorVO> editFilterOperator(@RequestBody FilterOpertorVO filterOpertorVO) {
        Result<FilterOpertorVO> result = filterOperatorService.editFilterOperator(filterOpertorVO);
        return result;
    }

    @PostMapping("/editFilterOperatorName")
    @ApiOperation(value = "编辑规则过滤器名称", notes = "")
    @SysRequestLog(description = "编辑规则过滤器名称", actionType = ActionType.UPDATE, manually = false)
    public Result<FilterOpertorVO> editFilterOperatorName(@RequestBody FilterOpertorVO filterOpertorVO) {
        Result<FilterOpertorVO> result = filterOperatorService.editFilterOperatorName(filterOpertorVO);
        return result;
    }

    @DeleteMapping("/filterOperator")
    @ApiOperation(value = "删除规则过滤器", notes = "")
    @SysRequestLog(description = "删除规则过滤器", actionType = ActionType.DELETE, manually = false)
    public Result<Boolean> deleteFilterOperator(@RequestBody FilterOpertorVO filterOpertorVO) {
        Result<Boolean> result = filterOperatorService.deleteFilterOperator(filterOpertorVO);
        return result;
    }

    @PostMapping("/startFilterJob")
    @ApiOperation(value = "启动规则过滤器", notes = "")
    @SysRequestLog(description = "启动规则过滤器", actionType = ActionType.UPDATE, manually = false)
    public Result<FilterOpertorVO> startFilterJob(@RequestBody FilterOpertorVO filterOpertorVO) {
        logger.info("启动规则过滤器");
        Result<FilterOpertorVO> result = filterOperatorService.startFilterOperatorJob(filterOpertorVO);
        return result;
    }

    @PostMapping("/stopFilterJob")
    @ApiOperation(value = "停止规则过滤器", notes = "")
    @SysRequestLog(description = "停止规则过滤器", actionType = ActionType.UPDATE, manually = false)
    public Result<FilterOpertorVO> stopFilterJob(@RequestBody FilterOpertorVO filterOpertorVO) {
        Result<FilterOpertorVO> result = filterOperatorService.stopFilterOperatorJob(filterOpertorVO);
        return result;
    }

    @GetMapping("/getDimensionTables")
    @ApiOperation(value = "获得维表相关得信息", notes = "")
    @SysRequestLog(description = "获得维表相关得信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<DimensionTable>> getDimensionTables() {
        List<DimensionTable> list = filterOperatorService.getDimensionTables();
        Result<List<DimensionTable>> result = ResultUtil.success(list);
        return result;
    }


    @PutMapping({"/createAnalyzerInstance"})
    @ApiOperation(value = "创建分析器实例", notes = "")
    @SysRequestLog(description = "创建分析器实例", actionType = ActionType.ADD, manually = false)
    public Result<FilterOpertorVO> createAnalyzerInstance(@RequestBody Map<String, Object> map) {
        FilterOpertorVO filterOpertorVO = this.filterOperatorService.createAnalyzerInstanceByGuid(map);
        return ResultUtil.success(filterOpertorVO);
    }

    @PutMapping({"/createAnalyzerInstanceByCode"})
    @ApiOperation(value = "创建分析器实例", notes = "")
    @SysRequestLog(description = "创建分析器实例", actionType = ActionType.ADD, manually = false)
    public Result<FilterOpertorVO> createAnalyzerInstanceByCode(@RequestBody Map<String, Object> map) {
        String code = map.get("code").toString();
        FilterOpertorVO filterOpertorVO = new FilterOpertorVO();
        List<FilterOperator> filterOperatorList = filterOperatorService.getFilterOperators(code);
        if (filterOperatorList.size() == 1) {
            FilterOperator filterOperator = filterOperatorList.get(0);
            filterOpertorVO = filterOperatorService.createAnalyzerInstance(map, filterOperator);
            return ResultUtil.success(filterOpertorVO);
        } else {
            throw new RuntimeException("分析器模板不唯一");
        }
    }

    @PostMapping("/editAnalyzerInstance")
    @ApiOperation(value = "编辑分析器实例", notes = "")
    @SysRequestLog(description = "编辑分析器实例", actionType = ActionType.UPDATE, manually = false)
    public Result<FilterOpertorVO> editAnalyzerInstance(@RequestBody Map<String, Object> map) {
        Result<FilterOpertorVO> result = filterOperatorService.editAnalyzerInstanceByGuid(map);
        return result;
    }

    @GetMapping("/getRuleModel/{type}")
    @ApiOperation(value = "根据资产类型查询性能告警模板", notes = "")
    @SysRequestLog(description = "根据资产类型查询性能告警模板", actionType = ActionType.SELECT, manually = false)
    public Result<List<RuleModelOfAssetType>> getRuleDeploy(@PathVariable("type") String type) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("assetType", type));
        List<RuleModelOfAssetType> list = ruleModelOfAssetTypeService.findAll(conditions);
        return ResultUtil.success(list);
    }

    @GetMapping("/findAnalyzers/{dataSourceGroupName}")
    @ApiOperation(value = "根据事件表类型查询性能告警模板", notes = "")
    @SysRequestLog(description = "根据事件表类型查询性能告警模板", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String, Object>>> findAnalyzers(@PathVariable("dataSourceGroupName") String dataSourceGroupName) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<FilterOperator> filterOperatorList = filterOperatorService.getFileOperatorByDataSource(dataSourceGroupName);
        Gson gson = new Gson();
        for (FilterOperator filterOperator : filterOperatorList) {
            Map<String, Object> map = new HashMap<>();
            map.put("guid", filterOperator.getGuid());
            map.put("label", filterOperator.getLabel());
            map.put("desc", filterOperator.getDesc());
            map.put("code", filterOperator.getCode());
            String paramConfig = filterOperator.getParamConfig();
            List<ParamConfigVO> paramConfigVOList = gson.fromJson(paramConfig, new TypeToken<List<ParamConfigVO>>() {
            }.getType());
            List<String> keys = paramConfigVOList.stream().map(item -> item.getParamKey()).collect(Collectors.toList());
            map.put("param", keys);
            list.add(map);
        }
        return ResultUtil.success(list);

    }

    @GetMapping("/copyFilterOperator/{guid}")
    @ApiOperation(value = "复制分析器", notes = "")
    @SysRequestLog(description = "复制分析器", actionType = ActionType.ADD, manually = false)
    public Result<FilterOpertorVO> copyFilterOperator(@PathVariable("guid") String guid) {
        FilterOpertorVO filterOpertorVO = filterOperatorService.copyByGuid(guid);
        return ResultUtil.success(filterOpertorVO);

    }

    @PostMapping("/startFilterOperatorGroup")
    @ApiOperation(value = "flink任务组启动(同一槽位)，其实前端就传了策略id")
    @SysRequestLog(description = "flink任务组启动(同一槽位)", actionType = ActionType.UPDATE, manually = false)
    public Result<String> startFilterOperatorGroupByName(@RequestBody FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        // 修改状态
        for (String ruleId : filterOperatorGroupStartVO.getGuids().split(",")) {
            riskEventRuleService.changeRiskEventRuleOnlyStatus(ruleId, "1");
        }
        //离线和实时任务启动公共判断部分
        String msg = commonFilterOperatorService.getFilterOperatorParamValueStatus(filterOperatorGroupStartVO);
        if(StringUtils.isNotEmpty(msg)){
            //参数校验没有通过，不会启动flink任务
            return ResultUtil.success(msg);
        }
        //离线的策略id集合，经过改方法过滤后，实现离线任务和实时任务的分离，filterOperatorGroupStartVO中存的是离线任务
        List<String> offlineRuleIdList = commonFilterOperatorService.filterOfflineFlinkJob(filterOperatorGroupStartVO);
        if(offlineRuleIdList.size()>0){
            //添加离线任务
            commonFilterOperatorService.addOfflineFlinkJobByRuleIdList(offlineRuleIdList);
        }
        if (StringUtils.isNotEmpty(filterOperatorGroupStartVO.getGuids())) {
            // 判断数据源数据是否满足
            msg = filterOperatorService.getRuleFilterSourceStatus(filterOperatorGroupStartVO);
            if (StringUtils.isNotBlank(msg)) {
                logger.warn("由于{},导致数据源不满足条件，无法启动规则！", msg);
                String result = "由于" + msg + ",导致数据源不满足条件，无法启动规则！";
                return ResultUtil.success(result);
            }
            // 判断维表是否有数据
            List<String> ruleIds = filterOperatorService.startFilterCheckFilterBaseLineData(filterOperatorGroupStartVO);
            msg = filterOperatorService.checkRuleFilterBaseLineData(ruleIds);
            if (StringUtils.isNotBlank(msg)) {
                return ResultUtil.success(msg);
            }
            // 启动规则
            filterOperatorService.startFilterOperatorGroupByName(new ArrayList<>(Arrays.asList(filterOperatorGroupStartVO.getGuids().split(","))), ruleIds);
        }
        return ResultUtil.success(null);
    }

    @PostMapping("/checkFlinkSlots")
    @ApiOperation(value = "检查槽位数是否足够")
    @SysRequestLog(description = "检查槽位数是否足够", actionType = ActionType.SELECT, manually = false)
    public Result<Boolean> checkFlinkSlots(@RequestBody FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        //二级事件分类id
        Map<String, List<String>> guidGroup = filterOperatorService.ruleGroupBySecondLevel(filterOperatorGroupStartVO, "");
        //计算总槽位数
        int sum = filterOperatorService.sumExistSlot(guidGroup);
        if (sum > slots) {
            return ResultUtil.success(false);
        } else {
            return ResultUtil.success(true);
        }
    }

    /**
     * 停止任务
     */
    @PostMapping("/stopFlinkJobGroups")
    @ApiOperation(value = "停止告警规则组")
    @SysRequestLog(description = "停止告警规则组", actionType = ActionType.UPDATE, manually = false)
    public Result<Boolean> stopFlinkJobByEventId(@RequestBody FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        String guids = filterOperatorGroupStartVO.getGuids();
        boolean result = filterOperatorService.stopFlinkJobByEventId(filterOperatorGroupStartVO);
        for (String ruleId : guids.split(",")) {
            riskEventRuleService.changeRiskEventRuleOnlyStatus(ruleId, "0");
            //后续这里需要进行优化
            filterOperatorService.removeFlinkTaskMapByRuleId(ruleId);
        }
        return ResultUtil.success(result);
    }
}
