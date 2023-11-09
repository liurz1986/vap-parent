package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.SuperviseDataSubmit;
import com.vrv.vap.admin.service.SuperviseDataSubmitService;
import com.vrv.vap.admin.vo.supervise.SuperviseDataSubmitQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@Api(value = "SuperviseDataSubmit")
@RequestMapping("/superviseDataSubmit")
public class SuperviseDataSubmitController extends ApiController {

    @Autowired
    private SuperviseDataSubmitService superviseDataSubmitService;

    public static final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @ApiOperation(value = "查询所有级联上报数据")
    @GetMapping
    @SysRequestLog(description="查询所有级联上报数据", actionType = ActionType.SELECT)
    public VData<List<SuperviseDataSubmit>> getAllSuperviseDataSubmit() {
        List<SuperviseDataSubmit> list = superviseDataSubmitService.findAll();
        return this.vData(list);
    }

    @ApiOperation(value = "新增级联上报数据")
    @PutMapping
    @SysRequestLog(description="新增级联上报数据", actionType = ActionType.ADD)
    public Result addSuperviseDataSubmit(@RequestBody SuperviseDataSubmit superviseDataSubmit) {
        int result = superviseDataSubmitService.save(superviseDataSubmit);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(superviseDataSubmit,"新增级联上报数据");
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "修改级联上报数据")
    @PatchMapping
    @SysRequestLog(description="修改级联上报数据", actionType = ActionType.UPDATE)
    public Result updateSuperviseDataSubmit(@RequestBody SuperviseDataSubmit superviseDataSubmit) {
        SuperviseDataSubmit submitQuery = new SuperviseDataSubmit();
        submitQuery.setGuid(superviseDataSubmit.getGuid());
        SuperviseDataSubmit dataSubmitSec = superviseDataSubmitService.findOne(submitQuery);
        int result = superviseDataSubmitService.update(superviseDataSubmit);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(dataSubmitSec,superviseDataSubmit,"修改级联上报数据");
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除级联上报数据")
    @DeleteMapping
    @SysRequestLog(description="删除级联上报数据", actionType = ActionType.DELETE)
    public Result delSuperviseDataSubmit(@RequestBody DeleteQuery deleteQuery) {
        List<SuperviseDataSubmit> submitList = superviseDataSubmitService.findByids(deleteQuery.getIds());
        int result = superviseDataSubmitService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            submitList.forEach(superviseDataSubmit -> {
                SyslogSenderUtils.sendDeleteSyslog(superviseDataSubmit,"删除级联上报数据");
            });
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "查询级联上报数据（分页）")
    @PostMapping(value = "/queryPage")
    @SysRequestLog(description="查询级联上报数据", actionType = ActionType.SELECT)
    public VList<SuperviseDataSubmit> querySuperviseDataSubmit(@RequestBody SuperviseDataSubmitQuery queryVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(queryVo, SuperviseDataSubmit.class);
        List<SuperviseDataSubmit> list = superviseDataSubmitService.findByExample(example);
        return this.vList(list);
    }

    @GetMapping("/supervise/offLine/export")
    @ApiOperation(value = "离线数据导出")
    @SysRequestLog(description = "离线数据导出", actionType = ActionType.EXPORT)
    public void offLineExport(HttpServletRequest request, HttpServletResponse response) {
        SyslogSenderUtils.sendExportSyslog();
        String ids = request.getParameter("ids");
        superviseDataSubmitService.offLineExport(ids, response);
    }
}