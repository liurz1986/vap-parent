package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEvent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEventData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEventList;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.HostAuditService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.ThreeInOneService;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.Results;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 功能描述
 * 主机审计Controller类
 *
 * @author wudi
 * @date 2022年04月20日 16:52
 */
@RestController
@RequestMapping("/report/threeInOne")
@Api(description = "报表-三合一")
public class ThreeInOneController extends BaseController {

    @Autowired
    private ThreeInOneService threeInOneService;

    @PostMapping("/event")
    @ApiOperation(value = "三合一违规事件", notes = "")
    @SysRequestLog(description = "三合一违规事件", actionType = ActionType.SELECT, manually = false)
    public Results<ThreeInOneEventData, List<ThreeInOneEventList>> event(@RequestBody RequestBean requestBean) {
        ThreeInOneEvent threeInOneEvent = threeInOneService.searchThreeInOneEvent(requestBean);
        ThreeInOneEventData data = threeInOneEvent.getData();
        List<ThreeInOneEventList> list = threeInOneEvent.getList();
        return ResultUtil.successListAndData(data,list);
    }

    @PostMapping("/terminalChange")
    @ApiOperation(value = "主机审计终端变更事件", notes = "")
    @SysRequestLog(description = "主机审计终端变更事件", actionType = ActionType.SELECT, manually = false)
    public Results<ThreeInOneEventData, List<ThreeInOneEventList>> terminalChange(@RequestBody RequestBean requestBean) {
        ThreeInOneEvent threeInOneEvent = threeInOneService.searchThreeInOneChange(requestBean);
        ThreeInOneEventData data = threeInOneEvent.getData();
        List<ThreeInOneEventList> list = threeInOneEvent.getList();
        return ResultUtil.successListAndData(data,list);
    }


}
