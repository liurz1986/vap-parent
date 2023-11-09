package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.RuleTypeConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.EventTaVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.IdTitleValue;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AppAlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.es.enums.ResultCodeEnum;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 告警事件查询（详情）
 *
 * @author liangguolu
 * @date 2021年12月22日 18:16
 */
@RestController
@RequestMapping("/AlarmEventManagement")
@Api(description = "告警事件管理")
public class AlarmEventManagementController extends BaseController {

    @Autowired
    private AlarmEventManagementService alarmEventManagementService;

    @PostMapping("/getAlarmDealPager")
    @ApiOperation(value = "获得告警事件列表", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-获得告警事件列表", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<AlarmEventAttributeVO> getAlarmDealPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementService.getAlarmDealPager(query);
    }

    @PostMapping("/getAssetOfConcern/{top}")
    @ApiOperation(value = "获取关注资产统计", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-获取关注资产统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAssetOfConcern(@RequestBody EventDetailQueryVO query,@PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementService.getAssetOfConcern(query,top));
    }

    @PostMapping("/setConcernIps")
    @ApiOperation(value = "设置关注的资产的ip", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-设置关注的资产的ip", actionType = ActionType.UPDATE,manually=false)
    public Result<Boolean> setConcernIps(@RequestBody Map<String, List<String>> param) {
        return ResultUtil.success(alarmEventManagementService.setConcernIps(param));
    }

    @GetMapping("/getConcernIps")
    @ApiOperation(value = "获取关注的资产的ip", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-获取关注的资产的ip", actionType = ActionType.SELECT,manually=false)
    public Result<List<String>> getConcernIps() {
        return ResultUtil.success(alarmEventManagementService.getConcernIps());
    }

    @PostMapping("/getStatisticsByAlarmRiskLevel")
    @ApiOperation(value = "按照事件等级统计数量", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按照事件等级统计数量", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getStatisticsByAlarmRiskLevel(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getStatisticsByAlarmRiskLevel(query));
    }

    @PostMapping("/getStatisticsByEventType/{top}")
    @ApiOperation(value = "按照事件类型分组统计", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按照事件类型分组统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<IdTitleValue>> getStatisticsByEventType(@RequestBody EventDetailQueryVO query,
                                                               @PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementService.getStatisticsByEventType(query, top));
    }

    @PostMapping("/getStatisticsByEventName/{top}")
    @ApiOperation(value = "按照事件名称分组统计", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按照事件名称分组统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getStatisticsByEventName(@RequestBody EventDetailQueryVO query,
                                                            @PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementService.getStatisticsByEventName(query, top));
    }

    @PostMapping("/getStatisticsByAlarmDealState/{top}")
    @ApiOperation(value = "按照事件处置状态分组统计", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按照事件处置状态分组统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<IdTitleValue>> getStatisticsByAlarmDealState(@RequestBody EventDetailQueryVO query,
                                                                    @PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementService.getStatisticsByAlarmDealState(query, top));
    }
    @PostMapping("/getAlarmEventLogsPage")
    @ApiOperation(value = "获得追溯原始日志", notes = "")
    @SysRequestLog(description = "告警事件管理(事件处置)-获得追溯原始日志", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<Map<String, Object>> getAlarmEventLogsPage(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementService.getAlarmEventLogsPage(query);
    }
    @PostMapping("/getStatisticsByDepartment/{top}")
    @ApiOperation(value = "按照部门分组统计", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按照部门分组统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getStatisticsByDepartment(@RequestBody EventDetailQueryVO query, @PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementService.getStatisticsByDepartment(query,top));
    }

    @PostMapping("/getEventCount")
    @ApiOperation(value = "统计数量", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-统计数量", actionType = ActionType.SELECT,manually=false)
    public Result<Long> getEventCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getEventCount(query));
    }

    @PostMapping(value = "/getAlarmTrend/{timeType}")
    @ApiOperation(value = "告警趋势", notes = "timeType统计模式：hour、day、month、year")
    @SysRequestLog(description="告警事件管理(事件处置)-告警趋势", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAlarmTrend(@RequestBody EventDetailQueryVO query,
                                                 @PathVariable("timeType") String timeType) {
        return ResultUtil.success(alarmEventManagementService.getAlarmTrend(query, timeType));
    }

    @GetMapping("/setAlarmEventMarkRead/{eventId}")
    @ApiOperation(value = "将事件标记为已读", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-将事件标记为已读", actionType = ActionType.UPDATE,manually=false)
    public Result<AlarmEventAttribute> setAlarmEventMarkRead(@PathVariable("eventId") String eventId) {
        return ResultUtil.success(alarmEventManagementService.setAlarmEventMarkRead(eventId));
    }

    @PostMapping("/setAlarmEventMarkRead")
    @ApiOperation(value = "将事件标记为已读", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-将事件标记为已读", actionType = ActionType.UPDATE,manually=false)
    public Result<List<AlarmEventAttribute>> setAlarmEventMarkRead(@RequestBody RiskRuleIdVO vo) {
        return ResultUtil.success(alarmEventManagementService.setAlarmEventMarkRead(vo));
    }

    @GetMapping("/getAlarmEvent/{eventId}")
    @ApiOperation(value = "查询单条事件信息", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-查询单条事件信息", actionType = ActionType.SELECT,manually=false)
    public Result<AlarmEventAttribute> getAlarmEvent(@PathVariable("eventId") String eventId) {
        return ResultUtil.success(alarmEventManagementService.getAlarmEvent(eventId));
    }

    @GetMapping("/getAlarmEventLogs/{eventId}")
    @ApiOperation(value = "获得追溯原始日志", notes = "")
    @SysRequestLog(description = "告警事件管理(事件处置)-获得追溯原始日志", actionType = ActionType.SELECT,manually=false)
    public Result<List<Map<String, Object>>> getAlarmEventLogs(@PathVariable("eventId") String eventId) {
        return ResultUtil.success(alarmEventManagementService.getAlarmEventLogs(eventId));
    }

    @GetMapping("/getAlarmEventLogs/{eventId}/{indexid}")
    @ApiOperation(value = "获得追溯原始日志", notes = "")
    @SysRequestLog(description = "告警事件管理(事件处置)-获得追溯原始日志", actionType = ActionType.SELECT,manually=false)
    public Result<List<Map<String, Object>>> getAlarmEventLogs(@PathVariable("eventId") String eventId, @PathVariable("indexid") String indexid) {
        return ResultUtil.success(alarmEventManagementService.getAlarmEventLogs(eventId, indexid));
    }

    @PostMapping("createReportFile")
    @ApiOperation(value = "生成导出文件")
    @SysRequestLog(description = "告警事件管理(事件处置)-生成导出文件", actionType = ActionType.SELECT,manually=false)
    public Result<String> createReportFile(@RequestBody EventDetailQueryVO query, HttpServletRequest request) {
        return ResultUtil.success(alarmEventManagementService.createReportFile(query, request));
    }

    @GetMapping("downloadReportFile/{fileName}")
    @ApiOperation(value = "导出文件")
    @SysRequestLog(description = "告警事件管理(事件处置)-导出文件", actionType = ActionType.SELECT,manually=false)
    public void downloadReportFile(@PathVariable String fileName, HttpServletRequest request,
                                   HttpServletResponse response) {
        alarmEventManagementService.downloadReportFile(fileName, request, response);
    }
    @PostMapping("/getAlarmDealAbnormalPager")
    @ApiOperation(value = "最新异常行为信息", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-最新异常行为信息", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<AlarmEventAttributeVO> getAlarmDealAbnormalPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementService.getAlarmDealAbnormalPager(query);
    }
    @PostMapping("/getAlarmDealAppAbnormalPager")
    @ApiOperation(value = "最新应用异常信息", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-最新应用异常信息", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<AppAlarmEventAttributeVO> getAlarmDealAppAbnormalPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementService.getAlarmDealAppAbnormalPager(query);
    }
    @PostMapping("/getAlarmDealUserAbnormalPager")
    @ApiOperation(value = "最新用户异常行为", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-最新用户异常行为", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<AppAlarmEventAttributeVO> getAlarmDealUserAbnormalPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementService.getAlarmDealUserAbnormalPager(query);
    }
    @PostMapping("/getAppEventCount")
    @ApiOperation(value = "统计应用告警数量", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-统计应用告警数量", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String,Long>> getAppEventCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getAppEventCount(query));
    }
    @PostMapping("/getEventAbnormalCount")
    @ApiOperation(value = "统计行为异常数量", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-统计行为异常数量", actionType = ActionType.SELECT,manually=false)
    public Result<Long> getEventAbnormalCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getEventAbnormalCount(query));
    }
    @PostMapping("/getEventUserAbnormalCount")
    @ApiOperation(value = "用户异常行为数", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-用户异常行为数", actionType = ActionType.SELECT,manually=false)
    public Result<Long> getEventUserAbnormalCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getEventUserAbnormalCount(query));
    }
    @PostMapping("/getEventAbnormalUserCount")
    @ApiOperation(value = "统计行为异常用户数量", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-统计行为异常用户数量", actionType = ActionType.SELECT,manually=false)
    public Result<Integer> getEventAbnormalUserCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getEventAbnormalUserCount(query));
    }
    @PostMapping("/getEventAbnormalAreaCount")
    @ApiOperation(value = "安全域事件数排名", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-安全域事件数排名", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getEventAbnormalAreaCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getEventAbnormalAreaCount(query));
    }
    @PostMapping("/getEventAbnormalOrgCount")
    @ApiOperation(value = "部门事件数排名", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-部门事件数排名", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getEventAbnormalOrgCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getEventAbnormalOrgCount(query));
    }
    @PostMapping("/getEventAbnormalTypeCount")
    @ApiOperation(value = "按异常行为类型统计发生次数", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按异常行为类型统计发生次数", actionType = ActionType.SELECT,manually=false)
    public  Result<List<NameValue>> getEventAbnormalTypeCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.successList(alarmEventManagementService.getEventAbnormalTypeCount(query));
    }
    @PostMapping("/getEventAbnormalTypeUserCount")
    @ApiOperation(value = "按异常行为类型统计用户数", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-按异常行为类型统计用户数", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getEventAbnormalTypeUserCount(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.successList(alarmEventManagementService.getEventAbnormalTypeUserCount(query));
    }
    @PostMapping(value = "/getAlarmLoginTrend")
    @ApiOperation(value = "客户端登录异常行为变化趋势")
    @SysRequestLog(description="告警事件管理(事件处置)-客户端登录异常行为变化趋势", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAlarmLoginTrend(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getAlarmTypeTrend(query, RuleTypeConstant.LOGIN_EXCEPTION));
    }
    @PostMapping(value = "/getAlarmUserAbnormalTrend")
    @ApiOperation(value = "用户异常行为变化")
    @SysRequestLog(description="告警事件管理(事件处置)-用户异常行为变化趋势", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAlarmUserAbnormalTrend(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getAlarmUserAbnormalTrend(query));
    }
    @PostMapping(value = "/getAlarmPrintTrend")
    @ApiOperation(value = "异常打印刻录行为变化趋势")
    @SysRequestLog(description="告警事件管理(事件处置)-异常打印刻录行为变化趋势", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAlarmPrintTrend(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getAlarmTypeTrend(query, RuleTypeConstant.PRINT_BURN_EXCEPTION));
    }
    @PostMapping(value = "/getAlarmOfflineTrend")
    @ApiOperation(value = "客户端不在线变化趋势")
    @SysRequestLog(description="告警事件管理(事件处置)-客户端不在线变化趋势", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAlarmOfflineTrend(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getAlarmTypeTrend(query, RuleTypeConstant.NOT_ONLINE_EXCEPTION));
    }
    @PostMapping(value = "/getAlarmOnDownTrend")
    @ApiOperation(value = "异常关机行为变化趋势")
    @SysRequestLog(description="告警事件管理(事件处置)-异常关机行为变化趋势", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getAlarmOnDownTrend(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.success(alarmEventManagementService.getAlarmTypeTrend(query, RuleTypeConstant.ABNORMAL_SHUTDOWN_EXCEPTION));
    }
    @PostMapping("/getAlarmEventPager")
    @ApiOperation(value = "最新事件", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-最新事件", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<AppAlarmEventAttributeVO> getAlarmEventPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementService.getAlarmEventPager(query);
    }
    @GetMapping("/getAllEventCount")
    @ApiOperation(value = "统计全部告警数量", notes = "")
    @SysRequestLog(description="告警事件管理(事件处置)-统计全部告警数量", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String,Long>> getAllEventCount() {
        return ResultUtil.success(alarmEventManagementService.getAllEventCount());
    }

    @PostMapping("/updateAlarmDealTest")
    @ApiOperation(value = "修改最新数据时间", notes = "")
    @SysRequestLog(description="修改最新数据时间", actionType = ActionType.SELECT,manually=false)
    public Result updateAlarmDealTest(@RequestBody Map<String, Integer> param) {
        return alarmEventManagementService.updateAlarmDealTest(param);
    }
    @PostMapping("/updateAlarmTypeTest")
    @ApiOperation(value = "修改事件类型测试", notes = "")
    @SysRequestLog(description="修改事件类型测试", actionType = ActionType.SELECT,manually=false)
    public Result updateAlarmTypeTest() {
        return alarmEventManagementService.updateAlarmTypeTest();
    }
    //各类型资产事件数量top10
    @PostMapping("/getAssetAlarmEventTop10")
    @ApiOperation(value = "各类型资产事件数量top10", notes = "")
    @SysRequestLog(description="各类型资产事件数量top10", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>>  getAssetAlarmEventTop10(@RequestBody Map<String, String> param) {
        String type = param.get("type");
        if (StringUtils.isBlank(type)){
            return ResultUtil.error(ResultCodeEnum.ERROR.getCode(), "各类型资产事件数量top10参数异常");
        }
        return ResultUtil.success(alarmEventManagementService.getAssetAlarmEventTop10(type));
    }

    @PostMapping("/test")
    @ApiOperation(value = "窃泄密任务测试", notes = "")
    @SysRequestLog(description="窃泄密任务测试", actionType = ActionType.SELECT,manually=false)
    public Result<List<Map<String,Map<String,Long>>>> test() {
        return ResultUtil.success(alarmEventManagementService.culStealLeakValue());
    }

    //异常资产数量
    @PostMapping("/abnormalAssetCount")
    @ApiOperation(value = "异常资产数量", notes = "")
    @SysRequestLog(description="异常资产数量", actionType = ActionType.SELECT,manually=false)
    public Result<Integer>  abnormalAssetCount() {
        return ResultUtil.success(alarmEventManagementService.abnormalAssetCount());
    }
    //异常资产数量趋势
    @PostMapping("/abnormalAssetCountTrend/{timeType}")
    @ApiOperation(value = "异常资产数量", notes = "")
    @SysRequestLog(description="异常资产数量", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>>  abnormalAssetCountTrend(@RequestBody EventDetailQueryVO query,
                                                    @PathVariable("timeType") String timeType) {
        return ResultUtil.success(alarmEventManagementService.abnormalAssetCountTrend(query,timeType));
    }
    //今日告警资产数量，重要资产数量
    @GetMapping("/dayAlarmWorthAssetCount")
    @ApiOperation(value = "今日告警资产数量，重要资产数量", notes = "")
    @SysRequestLog(description="今日告警资产数量，重要资产数量", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String,Integer>>  dayAlarmWorthAssetCount() {
        return ResultUtil.success(alarmEventManagementService.dayAlarmWorthAssetCount());
    }
    @GetMapping("/getIpGroup")
    @ApiOperation(value = "通过ip聚合统计事件数量", notes = "")
    @SysRequestLog(description="通过ip聚合统计事件数量", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String, Long>>  getIpGroup() {
        return ResultUtil.success(alarmEventManagementService.getIpGroup());
    }
    @PostMapping ("/getEventObject")
    @ApiOperation(value = "得到告警事件对象", notes = "")
    @SysRequestLog(description="通过ip聚合统计事件数量", actionType = ActionType.SELECT,manually=false)
    public Result<List<EventTaVo>>  getEventObject(@RequestBody EventDetailQueryVO query) {
        return ResultUtil.successList(alarmEventManagementService.getEventObject(query.getEventId()));
    }
}
