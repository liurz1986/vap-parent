package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.DealTotalReponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.DealTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.DealReportService;
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
 * @date 2022年01月12日 16:53
 */
@RestController
@RequestMapping("/report/deal")
@Api(description = "报表-预警、督办、协办")
public class DealReportController extends BaseController {

    @Autowired
    private DealReportService dealReportService;

    @PostMapping("/event/total")
    @ApiOperation(value = "预警、督办、协办概要信息", notes = "")
    @SysRequestLog(description = "报表-预警、督办、协办-预警、督办、协办概要信息", actionType = ActionType.SELECT, manually = false)
    public Result<DealTotalReponse> total(@RequestBody RequestBean item) {
        return ResultUtil.success(dealReportService.queryDealTotal(item));
    }

    @PostMapping("/event/{type}")
    @ApiOperation(value = "督办任务信息列表", notes = "")
    @SysRequestLog(description = "报表-预警、督办、协办-督办任务信息列表", actionType = ActionType.SELECT, manually = false)
    public Result<List<DealTypeResponse>> type(@RequestBody RequestBean item, @PathVariable("type") String type) {
        return ResultUtil.successList(dealReportService.queryDealListByType(item, type));
    }
}
