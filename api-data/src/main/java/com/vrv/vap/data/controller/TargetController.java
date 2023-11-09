package com.vrv.vap.data.controller;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.mapper.BaseReportInterfaceMapper;
import com.vrv.vap.data.model.BaseReportInterface;
import com.vrv.vap.data.service.BaseReportInterfaceService;
import com.vrv.vap.data.util.TargetInterfaceUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/target")
@Api(value = "仪表板指标接口", tags = "仪表板指标接口")
public class TargetController extends ApiController {

    @Autowired
    private BaseReportInterfaceService baseReportInterfaceService;

    @Resource
    private BaseReportInterfaceMapper baseReportInterfaceMapper;

    @ApiOperation(value = "获取全部指标配置")
    @GetMapping
    @SysRequestLog(description = "获取全部指标配置", actionType = ActionType.SELECT)
    public Result getAll() {
        return this.vList(baseReportInterfaceService.findAll());
    }

    @ApiOperation(value = "获取指标数据")
    @PostMapping("/{id}")
    @SysRequestLog(description = "获取指标数据", actionType = ActionType.SELECT)
    public Map getTargetData(@RequestBody Map<String, Object> paramMap, @PathVariable String id) {
        BaseReportInterface baseReportInterface = baseReportInterfaceMapper.selectByPrimaryKey(id);
        return TargetInterfaceUtil.getDataFromInterface(baseReportInterface, paramMap);
    }
}
