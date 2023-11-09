package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.admin.service.StatusService;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lilang
 * @date 2020/10/28
 * @description 获取状态
 */
@RestController
public class StatusController extends ApiController {

    @Resource
    StatusService statusService;

    @GetMapping("/cluster/stats")
    @SysRequestLog(description = "获取ES集群状态",actionType = ActionType.SELECT)
    @ApiOperation("获取ES集群状态")
    public VData getEsClusterInfo() {
        return this.vData(statusService.getEsClusterInfo());
    }

    @GetMapping("/logstash/stats")
    @SysRequestLog(description = "获取logstash状态",actionType = ActionType.SELECT)
    @ApiOperation("获取logstash状态")
    public VData getLogStashInfo() {
        return this.vData(statusService.getLogStashInfo());
    }

    /**
     * 获取kafka运行数据
     *
     * @return VData
     */
    @GetMapping("/monitor/getKafkaData")
    @SysRequestLog(description = "获取kafka运行数据",actionType = ActionType.SELECT)
    @ApiOperation(value = "获取kafka运行数据")
    public VData getKafkaData() {
        return this.vData(statusService.extractKafkaData());
    }
}
