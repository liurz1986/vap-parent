package com.vrv.vap.alarmdeal.business.flink.controller;

/**
 * @author: 梁国露
 * @since: 2022/12/30 14:31
 * @description:
 */

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkOverviewService;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkTaskManagerService;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description="flink管理页面")
@RestController
@RequestMapping("/flinkmanage")
public class FlinkOverviewController {

    @Autowired
    private FlinkOverviewService flinkOverviewService;

    @Autowired
    private FlinkTaskManagerService flinkTaskManagerService;

    @GetMapping("/taskmanagers")
    @ApiOperation(value = "任务配置", notes = "")
    @SysRequestLog(description="任务配置", actionType = ActionType.SELECT,manually=false)
    public JSONObject taskmanagers() {
        JSONObject res = flinkTaskManagerService.taskmanagers();
        return res;
    }

    @GetMapping("/config")
    @ApiOperation(value = "首页配置", notes = "")
    @SysRequestLog(description="首页配置", actionType = ActionType.SELECT,manually=false)
    public JSONObject config() {
        JSONObject res = flinkOverviewService.config();
        return res;
    }

    @GetMapping("/overview")
    @ApiOperation(value = "首页概述", notes = "")
    @SysRequestLog(description="首页概述", actionType = ActionType.SELECT,manually=false)
    public JSONObject overview() {
        JSONObject res = flinkOverviewService.overview();
        return res;
    }

    @GetMapping("/job/overview")
    @ApiOperation(value = "首页任务列表概述", notes = "")
    @SysRequestLog(description="首页任务列表概述", actionType = ActionType.SELECT,manually=false)
    public JSONObject joboverview() {
        JSONObject res = flinkOverviewService.joboverview();
        return res;
    }

}
