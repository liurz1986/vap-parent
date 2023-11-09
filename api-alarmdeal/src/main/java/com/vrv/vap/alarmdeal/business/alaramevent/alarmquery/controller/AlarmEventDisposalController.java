package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventDisposalService;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventDisposal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventUrgeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.QueryIdsVO;
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
 * 告警事件处置
 *
 * @author liangguolu
 * @date 2021年12月24日 15:19
 */
@RestController
@RequestMapping("/AlarmEventDisposal")
@Api(description = "告警事件处置")
public class AlarmEventDisposalController extends BaseController {

    @Autowired
    private AlarmEventDisposalService alarmEventDisposalService;

    @PostMapping("/appendAlarmEventUrge/{eventId}")
    @ApiOperation(value = "添加督促信息", notes = "")
    @SysRequestLog(description="告警事件处置-添加督促信息", actionType = ActionType.ADD,manually=false)
    public Result<AlarmEventAttribute> appendAlarmEventUrge(@RequestBody AlarmEventUrgeVO item, @PathVariable("eventId") String eventId) {
        return ResultUtil.success(alarmEventDisposalService.appendAlarmEventUrge(item, eventId));
    }

    @PostMapping("/appendAlarmEventUrges")
    @ApiOperation(value = "添加督促信息（批量）", notes = "")
    @SysRequestLog(description="告警事件处置-添加督促信息（批量）", actionType = ActionType.ADD,manually=false)
    public Result<List<AlarmEventAttribute>> appendAlarmEventUrges(@RequestBody QueryIdsVO vo) {
        return ResultUtil.success(alarmEventDisposalService.appendAlarmEventUrges(vo));
    }

    @GetMapping("/getAlarmEventDisposal/{guid}")
    @ApiOperation(value = "保存处置信息", notes = "")
    @SysRequestLog(description="告警事件处置-保存处置信息", actionType = ActionType.ADD,manually=false)
    public Result<AlarmEventDisposal> getAlarmEventDisposal(@PathVariable("guid") String guid) {
        return ResultUtil.success(alarmEventDisposalService.getAlarmEventDisposal(guid));
    }
}
