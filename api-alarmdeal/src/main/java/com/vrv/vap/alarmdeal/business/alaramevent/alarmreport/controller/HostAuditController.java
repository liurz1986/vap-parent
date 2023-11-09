package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.HostAuditService;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.Results;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述
 * 主机审计Controller类
 *
 * @author wudi
 * @date 2022年04月20日 16:52
 */
@RestController
@RequestMapping("/report/hostAudit")
@Api(description = "报表-主机审计")
public class HostAuditController extends BaseController {

    @Autowired
    private HostAuditService hostAuditService;

    @PostMapping("/event")
    @ApiOperation(value = "主机审计违规事件", notes = "")
    @SysRequestLog(description = "主机审计违规事件", actionType = ActionType.SELECT, manually = false)
    public Results<StaticData, List<StaticsList>> event(@RequestBody RequestBean requestBean) {
        HostAuditEventResponse hostAuditEventResponse = hostAuditService.searchHostAuditEvent(requestBean);
        StaticData data = hostAuditEventResponse.getData();
        List<StaticsList> list = hostAuditEventResponse.getList();
       return ResultUtil.successListAndData(data,list);
    }

    @PostMapping("/terminalChange")
    @ApiOperation(value = "主机审计终端变更事件", notes = "")
    @SysRequestLog(description = "主机审计终端变更事件", actionType = ActionType.SELECT, manually = false)
    public Results<TerminalData, List<TerminalList>> terminalChange(@RequestBody RequestBean requestBean) {
        HostAuditTerminalStrategy hostAuditTerminalStrategy = hostAuditService.searchHostAuditTerminalEvent(requestBean);
        TerminalData data = hostAuditTerminalStrategy.getData();
        List<TerminalList> list = hostAuditTerminalStrategy.getList();
        return ResultUtil.successListAndData(data,list);
    }


}
