package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementAggService;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmDealAggregationRow;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 告警事件查询（聚合）
 *
 * @author liangguolu
 * @date 2021年12月24日 14:11
 */
@RestController
@RequestMapping("/AlarmEventManagement/Aggregation")
@Api(description = "告警事件管理(聚合模式)")
public class AlarmEventManagementAggController extends BaseController {

    @Autowired
    private AlarmEventManagementAggService alarmEventManagementAggService;

    @PostMapping("/getAlarmDealPager")
    @ApiOperation(value = "获得告警事件(聚合)列表", notes = "")
    @SysRequestLog(description="告警事件管理(聚合模式)-获得告警事件(聚合)列表", actionType = ActionType.SELECT,manually=false)
    public PageRes<AlarmDealAggregationRow> getAlarmDealAggetStatisticsCountgregationPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementAggService.getAlarmDealAggetStatisticsCountgregationPager(query);
    }

    @PostMapping("/getStatisticsCount")
    @ApiOperation(value = "统计数量", notes = "")
    @SysRequestLog(description="告警事件管理(聚合模式)-统计数量", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getStatisticsCount(@RequestBody EventDetailQueryVO query ) {
        return ResultUtil.success(alarmEventManagementAggService.getStatisticsCount(query));
    }

    @PostMapping("createReportFile")
    @ApiOperation(value = "生成导出文件")
    @SysRequestLog(description="告警事件管理(聚合模式)-生成导出文件", actionType = ActionType.EXPORT,manually=false)
    public Result<String> createReportFile(@RequestBody EventDetailQueryVO query, HttpServletRequest request) {
        return ResultUtil.success(alarmEventManagementAggService.createReportFile(query, request));
    }

    @GetMapping("downloadReportFile/{fileName}")
    @ApiOperation(value = "导出文件")
    @SysRequestLog(description="告警事件管理(聚合模式)-导出文件", actionType = ActionType.EXPORT,manually=false)
    public void downloadReportFile(@PathVariable String fileName, HttpServletRequest request,
                                   HttpServletResponse response) {
        alarmEventManagementAggService.downloadReportFile(fileName, request, response);
    }

    @PostMapping("/getEventNameOfConcern/{top}")
    @ApiOperation(value = "获取关注事件统计", notes = "")
    @SysRequestLog(description="告警事件管理(聚合模式)-获取关注事件统计", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getEventNameOfConcern(@RequestBody EventDetailQueryVO query,@PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementAggService.getEventNameOfConcern(query, top));
    }

    @PostMapping("/getDistinctEventName/{top}")
    @ApiOperation(value = "获取所有事件名称（不判断数据权限）", notes = "   top 为查询的数量，为0时取全部")
    @SysRequestLog(description="告警事件管理(聚合模式)-获取所有事件名称（不判断数据权限）", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>> getDistinctEventName(@RequestBody EventDetailQueryVO query,@PathVariable("top") Integer top) {
        return ResultUtil.success(alarmEventManagementAggService.getDistinctEventName(query, top));
    }

    @PostMapping("/getAllAlarmDealPager")
    @ApiOperation(value = "获得告警事件列表（不判断数据权限）", notes = "")
    @SysRequestLog(description="告警事件管理(聚合模式)-获得告警事件列表（不判断数据权限）", actionType = ActionType.SELECT,manually=false)
    public PageRes_ES<AlarmEventAttribute> getAllAlarmDealPager(@RequestBody EventDetailQueryVO query) {
        return alarmEventManagementAggService.getAllAlarmDealPager(query);
    }
}
