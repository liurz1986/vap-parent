package com.vrv.vap.data.controller;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.service.IndividualService;
import com.vrv.vap.data.vo.CommonRequest;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/individual")
@Api(value = "个性化查询", tags = "个性化查询")
public class IndividualController extends ApiController {

    @Autowired
    IndividualService individualService;

    @ApiOperation("获取近24小时内日志总量")
    @PostMapping(path = "/daytime/total")
    @SysRequestLog(description = "获取区间段内日志趋势",actionType = ActionType.SELECT)
    public VData query24Total(@RequestBody CommonRequest query) {
        return this.vData(individualService.query24Total(query));
    }

    @ApiOperation("获取近24小时内日志趋势")
    @PostMapping(path = "/day/trend")
    @SysRequestLog(description = "获取区间段内日志趋势",actionType = ActionType.SELECT)
    public VData queryDayTrend(@RequestBody CommonRequest query) {
        return this.vData(individualService.query24Trend(query));
    }

}
