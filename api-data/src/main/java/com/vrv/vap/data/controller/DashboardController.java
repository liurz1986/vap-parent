package com.vrv.vap.data.controller;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.Dashboard;
import com.vrv.vap.data.service.DashboardService;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/dashboard")
@Api(value = "【仪表盘】仪表盘管理", tags = "【仪表盘】仪表盘管理")
public class DashboardController extends ApiController {

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("top", "{\"0\":\"否\",\"1\":\"是\"}");
        transferMap.put("timeRestore", "{\"1001\":\"今天\",\"1002\":\"本周\",\"1003\":\"本月\",\"1004\":\"本年\"," +
                "\"1005\":\"最近15分钟\",\"1006\":\"最近30分钟\",\"1007\":\"最近1小时\",\"1008\":\"最近4小时\"," +
                "\"1009\":\"最近12小时\",\"1010\":\"最近24小时\",\"1011\":\"最近3天\",\"1012\":\"最近7天\",\"1013\":\"最近30天\"," +
                "\"1014\":\"最近60天\",\"1015\":\"最近90天\",\"1016\":\"最近6月\",\"1017\":\"最近1年\"}");
    }


    @Autowired
    DashboardService dashboardService;

    @ApiOperation(value = "获取全部仪表盘")
    @GetMapping
    @SysRequestLog(description = "获取全部仪表盘", actionType = ActionType.SELECT)
    public VData<List<Dashboard>> getAll(HttpServletRequest request) {
        return this.vData(dashboardService.findAll());
    }

    @ApiOperation(value = "获取指定仪表盘")
    @GetMapping(value = "/{dashboardId}")
    @SysRequestLog(description = "获取指定仪表盘", actionType = ActionType.SELECT)
    public VData<Dashboard> get(@PathVariable("dashboardId") Integer dashboardId) {
        return this.vData(dashboardService.findById(dashboardId));
    }


    @ApiOperation(value = "新增仪表盘")
    @PutMapping
    @SysRequestLog(description = "新增仪表盘", actionType = ActionType.ADD)
    public VData<Dashboard> add(@RequestBody Dashboard dashboard) {
        dashboard.setTop(0);
        int result = dashboardService.save(dashboard);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(dashboard,"新增仪表盘",transferMap);
            return this.vData(dashboard);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "修改仪表盘")
    @PatchMapping
    @SysRequestLog(description = "修改仪表盘", actionType = ActionType.UPDATE)
    public Result edit(@RequestBody Dashboard dashboard) {
        Dashboard dashboardSec = dashboardService.findById(dashboard.getId());
        int result = dashboardService.updateSelective(dashboard);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(dashboardSec,dashboard,"修改仪表盘",transferMap);
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除仪表盘（支持批量）")
    @DeleteMapping
    @SysRequestLog(description = "删除仪表盘", actionType = ActionType.DELETE)
    public Result del(@RequestBody DeleteQuery delete) {
        List<Dashboard> dashboardList = dashboardService.findByids(delete.getIds());
        int result = dashboardService.deleteByIds(delete.getIds());
        if (result > 0) {
            dashboardList.forEach(dashboard -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(dashboard,"删除仪表盘",transferMap);
            });
        }
        return this.result(result > 0);
    }

}
