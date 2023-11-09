package com.vrv.vap.alarmdeal.business.flink.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkJobService;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 15:38
 * @description:
 */
@Api(description="flink管理页面")
@RestController
@RequestMapping("/flinkmanage/jobs")
public class FlinkJobController {

    @Autowired
    private FlinkJobService flinkJobService;

    @GetMapping("/{id}")
    @ApiOperation(value = "Job详情", notes = "")
    @SysRequestLog(description="Job详情", actionType = ActionType.SELECT,manually=false)
    public JSONObject info(@PathVariable("id") String id) {
        JSONObject res =flinkJobService.info(id);
        return res;
    }

    @GetMapping("/{id}/exceptions")
    @ApiOperation(value = "Job详情异常信息", notes = "")
    @SysRequestLog(description="Job详情异常信息", actionType = ActionType.SELECT,manually=false)
    public JSONObject exceptions(@PathVariable("id") String id) {
        JSONObject res =flinkJobService.exceptions(id);
        return res;
    }

    @GetMapping("/{id}/checkpoints")
    @ApiOperation(value = "Job详情检查点信息", notes = "")
    @SysRequestLog(description="Job详情检查点信息", actionType = ActionType.SELECT,manually=false)
    public JSONObject checkpoints(@PathVariable("id") String id) {
        JSONObject res =flinkJobService.checkpoints(id);
        return res;
    }

    @GetMapping("/{id}/config")
    @ApiOperation(value = "Job详情配置信息", notes = "")
    @SysRequestLog(description="Job详情配置信息", actionType = ActionType.SELECT,manually=false)
    public JSONObject config(@PathVariable("id") String id) {
        JSONObject res =flinkJobService.config(id);
        return res;
    }

    @GetMapping("/{id}/yarn-cancel")
    @ApiOperation(value = "Job 退出", notes = "")
    @SysRequestLog(description="Job 退出", actionType = ActionType.SELECT,manually=false)
    public JSONObject cancel(@PathVariable("id") String id) {
        JSONObject res =flinkJobService.cancel(id);
        return res;
    }

    @GetMapping("/{id}/vertices/{name}")
    @ApiOperation(value = "Job 单任务详情", notes = "")
    @SysRequestLog(description="Job 单任务详情", actionType = ActionType.SELECT,manually=false)
    public JSONObject vertices(@PathVariable("id") String id,@PathVariable("name") String name) {
        JSONObject res =flinkJobService.vertices(id,name);
        return res;
    }

    @GetMapping("/{id}/vertices/{name}/taskmanagers")
    @ApiOperation(value = "Job 单任务详情", notes = "")
    @SysRequestLog(description="Job 单任务详情", actionType = ActionType.SELECT,manually=false)
    public JSONObject taskmanagers(@PathVariable("id") String id,@PathVariable("name") String name) {
        JSONObject res =flinkJobService.taskmanagers(id,name);
        return res;
    }

    @GetMapping("/{id}/vertices/{name}/backpressure")
    @ApiOperation(value = "Job 单任务压力详情", notes = "")
    @SysRequestLog(description="Job 单任务压力详情", actionType = ActionType.SELECT,manually=false)
    public JSONObject backpressure(@PathVariable("id") String id,@PathVariable("name") String name) {
        JSONObject res =flinkJobService.backpressure(id,name);
        return res;
    }

    @GetMapping("/{id}/vertices/{name}/metrics")
    @ApiOperation(value = "Job 单任务指标配置", notes = "")
    @SysRequestLog(description="Job 单任务指标配置", actionType = ActionType.SELECT,manually=false)
    public JSONArray metrics(@PathVariable("id") String id, @PathVariable("name") String name, HttpServletRequest request) {
        String param = request.getParameter("get");
        JSONArray res =flinkJobService.metrics(id,name,param);
        return res;
    }

}
