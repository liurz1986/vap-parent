package com.vrv.vap.data.controller;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.service.ScreenDataService;
import com.vrv.vap.data.vo.CommonRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/screen/data")
@Api(value = "大屏数据提供", tags = "大屏数据提供")
public class ScreenDataController extends ApiController {

    @Autowired
    private ScreenDataService screenDataService;

    @ApiOperation(value = "流量统计")
    @PostMapping("/netflow/count")
    public VData getNetflowInfo(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getNetflowInfo(query));
    }

    @ApiOperation(value = "流量趋势")
    @PostMapping("/netflow/trend")
    public VData getDataTrend(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataTrend(query));
    }

    @ApiOperation(value = "流量ip统计排行")
    @PostMapping("/netflow/top")
    public VData getDataTop(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataTop(query));
    }

    @ApiOperation(value = "流量协议统计")
    @PostMapping("/netflow/protocol")
    public VData getDataProtocol(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataProtocol(query));
    }

    @ApiOperation(value = "流量实时统计")
    @PostMapping("/netflow/new")
    public VData getDataNetflowNew(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataNetflowNew(query));
    }

    @ApiOperation(value = "应用访问排行")
    @PostMapping("/app/visit/top")
    public VData getDataAppVisitTop(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataAppVisitTop(query));
    }

    @ApiOperation(value = "应用访问来源排行")
    @PostMapping("/app/visit/src/top")
    public VData getDataAppVisitSrcTop(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataAppVisitSrcTop(query));
    }

    @ApiOperation(value = "终端登录情况排行")
    @PostMapping("/terminal/login/top")
    public VData getDataTerminalLoginTop(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataTerminalLoginTop(query));
    }

    @ApiOperation(value = "终端登录趋势")
    @PostMapping("/terminal/login/trend")
    public VData getDataTerminalLoginTrend(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataTerminalLoginTrend(query));
    }

    @ApiOperation(value = "用户操作类型排行")
    @PostMapping("/operate/type/top")
    public VData getDataUserLogTop(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataUserLogTop(query));
    }

    @ApiOperation(value = "应用访问人数")
    @PostMapping("/visit/num")
    public VData getDataVisitNum(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataVisitNum(query));
    }

    @ApiOperation(value = "攻击日志概览")
    @PostMapping("/attack/info")
    public VData getDataAttackInfo(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataAttackInfo(query));
    }

    @ApiOperation(value = "攻击来源排行")
    @PostMapping("/attack/ip/top")
    public VData getDataAttackIpTop(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataAttackIpTop(query));
    }

    @ApiOperation(value = "最新攻击事件")
    @PostMapping("/attack/new")
    public VData getDataAttackNew(@RequestBody CommonRequest query) {
        return this.vData(screenDataService.getDataAttackNew(query));
    }

}
