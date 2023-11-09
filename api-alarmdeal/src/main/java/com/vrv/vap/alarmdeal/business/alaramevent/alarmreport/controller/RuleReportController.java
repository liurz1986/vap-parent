package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.DealReportService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.EventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.RuleReportService;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 策略规则报表
 *
 * @author tyj
 * @date 2023年08月104日 16:53
 */
@RestController
@RequestMapping("/report/rule")
@Api(description = "策略规则报表")
public class RuleReportController extends BaseController {

    @Autowired
    private RuleReportService ruleReportService;
    @Autowired
    private EventService eventService;
    @PostMapping("/info")
    @ApiOperation(value = "监管事件分析", notes = "")
    @SysRequestLog(description = "监管事件分析", actionType = ActionType.SELECT, manually = false)
    public Result<EventRuleTotalResponse> info(@RequestBody RequestBean item) {
        EventRuleTotalResponse eventRuleTotalResponse=new EventRuleTotalResponse();
        EventTotalResponse eventTotalResponse = eventService.queryEventTotal(item);
        BeanUtils.copyProperties(eventTotalResponse,eventRuleTotalResponse);
        return ResultUtil.success(ruleReportService.queryTotal(eventRuleTotalResponse));
    }
    @PostMapping("/isStarted")
    @ApiOperation(value = "运行中策略统计", notes = "")
    @SysRequestLog(description = "策略运行状态统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTypeResponse>> isStarted() {
        return ResultUtil.successList(ruleReportService.isStarted());
    }
    @PostMapping("/statistics")
    @ApiOperation(value = "策略运行状态统计--列表", notes = "")
    @SysRequestLog(description = "策略运行状态统计--列表", actionType = ActionType.SELECT, manually = false)
    public Result<List<RuleTypeResponse>> statistics() {
        return ResultUtil.successList(ruleReportService.statistics());
    }
}
