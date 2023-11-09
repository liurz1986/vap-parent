package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.controller;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmCountRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetDetailVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmQuery;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmQueryService;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmQueryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ThreatIntelligenceVO;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alarmQuery")
@Api(description = "告警页面查询")
public class AlarmQueryController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AlarmQueryController.class);

    @Autowired
    private AlarmQueryService alarmQueryService;

    @Autowired
    private WarnResultForESService warnResultForEsService;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private AssetService assetService;

    private static final String[] ATTACK_LINES = {"1", "2", "3", "4", "5", "6", "7"};


    @ApiOperation(value = "增加查询条件", notes = "")
    @PutMapping("condition")
    @SysRequestLog(description = "告警页面查询-增加查询条件", actionType = ActionType.ADD, manually = false)
    public Result<AlarmQuery> condition(@RequestBody AlarmQueryVO alarmQueryVO) {
        User user = SessionUtil.getCurrentUser();
        if (user != null) {
            Integer userId = user.getId();
            AlarmQuery alarmQuery = new AlarmQuery();
            alarmQuery.setGuid(UUIDUtils.get32UUID());
            alarmQuery.setQueryCondition(alarmQueryVO.getQueryCondition());
            alarmQuery.setQueryName(alarmQueryVO.getQueryName());
            alarmQuery.setUserId(userId);
            alarmQuery.setCreateTime(new Date());
            alarmQueryService.save(alarmQuery);
            return ResultUtil.success(alarmQuery);
        } else {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "未查到登录信息");
        }
    }

    @ApiOperation(value = "删除查询条件", notes = "")
    @DeleteMapping("condition/{guid}")
    @SysRequestLog(description = "告警页面查询-删除查询条件", actionType = ActionType.DELETE, manually = false)
    public Result<Boolean> deletecCondition(@PathVariable("guid") String guid) {
        alarmQueryService.delete(guid);
        return ResultUtil.success(true);
    }

    @ApiOperation(value = "查询条件分页查询", notes = "")
    @PostMapping("queryConditionPager")
    @SysRequestLog(description = "告警页面查询-查询条件分页查询", actionType = ActionType.SELECT, manually = false)
    public PageRes<AlarmQuery> queryConditionPager(@RequestBody AlarmQueryVO alarmQueryVO) {
        PageRes<AlarmQuery> alarmQueryPageRes = alarmQueryService.queryConditionPager(alarmQueryVO);
        return alarmQueryPageRes;
    }

    @ApiOperation(value = "按攻击阶段统计")
    @PostMapping("getAlarmResultByAttackLine")
    @SysRequestLog(description = "告警页面查询-按攻击阶段统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String, Object>>> getAlarmResultByAttackLine(@RequestBody AnalysisVO analysisVO) {
        List<Map<String, Object>> list = warnResultForEsService.getStasticsByRelateField(analysisVO, "attackLine", 10);
        List<String> stringList = new ArrayList<>(Arrays.asList(ATTACK_LINES));
        for (Map<String, Object> map : list) {
            int index = Integer.valueOf(map.get("attackLine").toString()) - 1;
            stringList.set(index, "attackLine");
        }
        for (String str : stringList) {
            if (!"attackLine".equals(str)) {
                Map<String, Object> map = new HashMap<>();
                map.put("attackLine", str);
                map.put("doc_count", 0);
                list.add(map);
            }
        }
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;
    }

    @ApiOperation(value = "数据来源列表查询")
    @GetMapping("queryDataSourceList")
    @SysRequestLog(description = "告警页面查询-数据来源列表查询", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTable>> queryDataSourceList() {
        Result<List<EventTable>> eventTableList = eventTabelService.getAllEventTable();
        return eventTableList;
    }

    @ApiOperation(value = "告警日志按类型和时间分组筛选")
    @GetMapping("getLogByTime/{guid}")
    @SysRequestLog(description = "告警页面查询-告警日志按类型和时间分组筛选", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String, Object>>> getLogByTypeAndTime(@PathVariable("guid") String guid) {
        List<Map<String, Object>> list = warnResultForEsService.getAlarmLogByTime(guid);
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;

    }

    @ApiOperation(value = "告警关联资产")
    @GetMapping("getAssets/{guid}")
    @SysRequestLog(description = "告警页面查询-告警关联资产", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String, Object>>> getAssets(@PathVariable("guid") String guid) {
        WarnResultLogTmpVO warnResultLogTmpVO = warnResultForEsService.getAlarmById(guid);
        List<Map<String, Object>> assetList = new ArrayList<>();
        String guids = warnResultLogTmpVO.getAssetInfo().get("assetguids").toString();
        if (StringUtils.isEmpty(guids) && StringUtils.isNotEmpty(warnResultLogTmpVO.getDstIps())) {
            AssetDetailVO assetResult = assetService.getOneAssetDetailByIp(warnResultLogTmpVO.getDstIps());
            constructAssetList(assetList, assetResult);
        } else if (StringUtils.isNotEmpty(guids)) {
            List<String> stringList = Arrays.asList(guids.split(","));
            for (String id : stringList) {
                try {
                    AssetDetailVO assetResult = assetService.getAssetDetail(id);
                    constructAssetList(assetList, assetResult);
                } catch (Exception e) {
                    logger.info("feign接口异常：{}", e.getMessage());
                }
            }
        }

        return ResultUtil.success(assetList);
    }

    private void constructAssetList(List<Map<String, Object>> assetList, AssetDetailVO assetResult) {
        if (assetResult != null) {
            Asset asset = assetResult.getAsset();
            AssetType assetType = assetResult.getAssetType();
            if (asset != null && assetType != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("assetType", assetResult.getAssetType().getName());
                map.put("name", assetResult.getAsset().getName());
                map.put("ip", assetResult.getAsset().getIp());
                map.put("worth", assetResult.getAsset().getWorth());
                map.put("location", assetResult.getAsset().getLocation());
                map.put("employeeCode1", assetResult.getAsset().getEmployeeCode1());
                assetList.add(map);
            }
        }
    }

    /**
     *
     */
    @ApiOperation("通过原始日志获取威胁情报")
    @GetMapping("getThreatByAlarmId/{guid}")
    @SysRequestLog(description = "告警页面查询-通过原始日志获取威胁情报", actionType = ActionType.SELECT, manually = false)
    public Result<ThreatIntelligenceVO> getThreatByAlarmId(@PathVariable("guid") String guid) {
        ThreatIntelligenceVO threatIntelligenceVO = warnResultForEsService.getThreatIntelligenceVO(guid);
        return ResultUtil.success(threatIntelligenceVO);
    }

    @ApiOperation(value = "告警原始日志导出")
    @PostMapping("/exportAlarmLogs")
    @SysRequestLog(description = "告警页面查询-告警原始日志导出", actionType = ActionType.EXPORT, manually = false)
    public Result<String> exportAlarmLogs(@RequestBody Map<String, Object> map) {
        Result<String> result = warnResultForEsService.exportAlarmLogs(map);
        return result;
    }

    @GetMapping("/downAlarmLogsExcel/{fileName:.+}")
    @ApiOperation(value = "fileName", notes = "告警原始浏览器导出")
    @SysRequestLog(description = "告警页面查询-告警原始浏览器导出", actionType = ActionType.EXPORT, manually = false)
    public void test(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        FileUtil.downLoadFile(fileName, fileConfiguration.getFilePath(), response);

    }

    @ApiOperation(value = "获得告警等级分布统计（终端告警级别分布统计）", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "ip", required = true, dataType = "String"),
            @ApiImplicitParam(name = "start_time", value = "开始时间", required = true, dataType = "String"),
            @ApiImplicitParam(name = "end_time", value = "结束时间", required = true, dataType = "String")
    })
    @PostMapping(value = "/getAssetAlarmByWeight")
    @SysRequestLog(description = "告警页面查询-获得告警等级分布统计（终端告警级别分布统计）", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String, Object>>> getAlarmByWeight(@RequestBody AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = warnResultForEsService.getCondition(analysisVO);
        List<Map<String, Object>> list = warnResultForEsService.getWeightMaps(conditions);
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;
    }

    @ApiOperation(value = "获得告警统计", notes = "")
    @PostMapping(value = "/getAlarmCount")
    @SysRequestLog(description = "告警页面查询-获得告警统计", actionType = ActionType.SELECT, manually = false)
    public Result<AlarmCountRes> getAlarmCountRes(@RequestBody RequestBean req){
        AlarmCountRes result = alarmQueryService.getAlarmCountRes(req);
        return  ResultUtil.success(result);
    }


}
