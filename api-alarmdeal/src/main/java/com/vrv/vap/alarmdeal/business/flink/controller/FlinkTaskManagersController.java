package com.vrv.vap.alarmdeal.business.flink.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkTaskManagerService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: 梁国露
 * @since: 2022/12/30 15:11
 * @description:
 */
@Api(description="flink管理页面")
@RestController
@RequestMapping("/flinkmanage/taskmanagers")
public class FlinkTaskManagersController {

    @Autowired
    private FlinkTaskManagerService flinkTaskManagerService;

    @GetMapping("/{id:.+}")
    @ApiOperation(value = "任务配置详情", notes = "")
    @SysRequestLog(description="任务配置详情", actionType = ActionType.SELECT,manually=false)
    public JSONObject taskmanagersByid(@PathVariable("id") String id) {
        JSONObject res = flinkTaskManagerService.taskmanagersByid(id);
        return res;
    }

    @GetMapping("/{id:.+}/metrics")
    @ApiOperation(value = "任务配置指标详情", notes = "")
    @SysRequestLog(description="任务配置指标详情", actionType = ActionType.SELECT,manually=false)
    public JSONArray taskmanagersBymetrics(@PathVariable("id") String id, HttpServletRequest request) {
        String get = request.getParameter("get");
        JSONArray res = flinkTaskManagerService.taskmanagersBymetrics(id,get);
        return res;
    }

    @GetMapping("/{id:.+}/logs")
    @ApiOperation(value = "任务配置日志", notes = "")
    @SysRequestLog(description="任务配置日志", actionType = ActionType.SELECT,manually=false)
    public JSONObject taskmanagersBylogs(@PathVariable("id") String id) {
        JSONObject res = flinkTaskManagerService.taskmanagersBylogs(id);
        return res;
    }

    @GetMapping("/{id:.+}/log")
    @ApiOperation(value = "任务配置日志", notes = "")
    @SysRequestLog(description="任务配置日志", actionType = ActionType.SELECT,manually=false)
    public String taskmanagersBylog(@PathVariable("id") String id,HttpServletRequest request) {
        String res = flinkTaskManagerService.taskmanagersBylog(id);
        String name = request.getParameter("name");
        if(StringUtils.isNotBlank(name)){
            res = flinkTaskManagerService.taskmanagersGetlogsByName(id,name);
        }
        return res;
    }

    @GetMapping("/{id:.+}/thread-dump")
    @ApiOperation(value = "任务配置线程转储详情", notes = "")
    @SysRequestLog(description="任务配置线程转储详情", actionType = ActionType.SELECT,manually=false)
    public JSONObject taskmanagersBythreaddump(@PathVariable("id") String id) {
        JSONObject res = flinkTaskManagerService.taskmanagersBythreaddump(id);
        return res;
    }
}
