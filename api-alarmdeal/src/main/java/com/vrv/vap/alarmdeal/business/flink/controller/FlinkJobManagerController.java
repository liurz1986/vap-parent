package com.vrv.vap.alarmdeal.business.flink.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkJobManagerService;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 15:12
 * @description:
 */
@Api(description="flink管理页面")
@RestController
@RequestMapping("/flinkmanage/jobmanager")
public class FlinkJobManagerController {

    @Autowired
    private FlinkJobManagerService flinkJobManagerService;

    @GetMapping("/config")
    @ApiOperation(value = "Job配置", notes = "")
    @SysRequestLog(description="Job配置", actionType = ActionType.SELECT,manually=false)
    public JSONArray config() {
        JSONArray res =flinkJobManagerService.config();
        return res;
    }

    @GetMapping("/metrics")
    @ApiOperation(value = "Job指标", notes = "")
    @SysRequestLog(description="Job指标", actionType = ActionType.SELECT,manually=false)
    public JSONArray metrics(HttpServletRequest request) {
        String get = request.getParameter("get");
        JSONArray res =flinkJobManagerService.metrics(get);
        return res;
    }

    @GetMapping("/log")
    @ApiOperation(value = "Job日志", notes = "")
    @SysRequestLog(description="Job日志", actionType = ActionType.SELECT,manually=false)
    public JSONObject log() {
        JSONObject res =flinkJobManagerService.log();
        return res;
    }

    @GetMapping("/stdout")
    @ApiOperation(value = "Job输出", notes = "")
    @SysRequestLog(description="Job输出", actionType = ActionType.SELECT,manually=false)
    public String stdout() {
        String res =flinkJobManagerService.stdout();
        return res;
    }

    @GetMapping("/logs")
    @ApiOperation(value = "Job日志列表", notes = "")
    @SysRequestLog(description="Job日志列表", actionType = ActionType.SELECT,manually=false)
    public JSONObject logs() {
        JSONObject res =flinkJobManagerService.logs();
        return res;
    }
}
