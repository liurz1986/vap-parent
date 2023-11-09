package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.FocalResultResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.FocalTotalResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.FocalReportService;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 16:52
 */
@RestController
@RequestMapping("/report/focal")
@Api(description = "报表-重点事件监管分析")
public class FocalReportController extends BaseController {

    @Autowired
    private FocalReportService focalReportService;

    @PostMapping("/event/total")
    @ApiOperation(value = "重点事件监管统计", notes = "")
    @SysRequestLog(description = "报表-重点事件监管分析-重点事件监管统计", actionType = ActionType.SELECT, manually = false)
    public Result<FocalTotalResponse> total(@RequestBody RequestBean item) {
        return ResultUtil.success(focalReportService.queryFocalTotal(item));
    }

    @PostMapping("/event/{type}/{num}")
    @ApiOperation(value = "重点事件分类统计", notes = "")
    @SysRequestLog(description = "报表-重点事件监管分析-重点事件分类统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTypeResponse>> type(@RequestBody RequestBean item, @PathVariable("type") String type, @PathVariable("num") int num) {
        return ResultUtil.successList(focalReportService.queryFocalType(item,type,num));
    }

    @PostMapping("/event/result")
    @ApiOperation(value = "结论与建议", notes = "")
    @SysRequestLog(description = "报表-重点事件监管分析-结论与建议", actionType = ActionType.SELECT, manually = false)
    public Result<FocalResultResponse> result(@RequestBody RequestBean item) {
        return ResultUtil.success(focalReportService.queryFocalResult(item));
    }
}
