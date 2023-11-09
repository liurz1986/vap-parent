package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.ReportTheme;
import com.vrv.vap.admin.service.ReportThemeService;
import com.vrv.vap.admin.vo.ReportThemeQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lilang
 * @date 2020/8/24
 * @description 报告主题控制器
 */
@RestController
@RequestMapping(path = "/reportTheme")
public class ReportThemeController extends ApiController {

    @Resource
    ReportThemeService reportThemeService;

    @ApiOperation("获取所有报告主题")
    @GetMapping
    public Result getAllReportTheme() {
        return this.vData(reportThemeService.findAll());
    }

    @ApiOperation("查询报告主题")
    @PostMapping
    @SysRequestLog(description="查询报告主题", actionType = ActionType.SELECT)
    public VList queryReportTheme(@RequestBody ReportThemeQuery reportThemeQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(reportThemeQuery, ReportTheme.class);
        return this.vList(reportThemeService.findByExample(example));
    }

    @ApiOperation("添加报告主题")
    @PutMapping
    @SysRequestLog(description="添加报告主题", actionType = ActionType.ADD)
    public VData addReportTheme(@RequestBody ReportTheme reportTheme) {
        int result = reportThemeService.save(reportTheme);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(reportTheme,"添加报告主题");
        }
        return this.vData(reportTheme);
    }

    @ApiOperation("修改报告主题")
    @PatchMapping
    @SysRequestLog(description="修改报告主题", actionType = ActionType.UPDATE)
    public Result updateReport(@RequestBody ReportTheme reportTheme) {
        if (reportTheme == null || reportTheme.getId() == null) {
            return this.result(false);
        }
        ReportTheme reportThemeSec = reportThemeService.findById(reportTheme.getId());
        int result = reportThemeService.updateSelective(reportTheme);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(reportThemeSec,reportTheme,"修改报告主题");
        }
        return this.result(result == 1);
    }

    @ApiOperation("删除报告主题")
    @DeleteMapping
    @SysRequestLog(description="删除报告主题", actionType = ActionType.DELETE)
    public Result deleteReportTheme(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<ReportTheme> reportThemeList = reportThemeService.findByids(param.getIds());
        int result = reportThemeService.deleteByIds(ids);
        if (result > 0) {
            reportThemeList.forEach(reportTheme -> {
                SyslogSenderUtils.sendDeleteSyslog(reportTheme,"删除报告主题");
            });
        }
        return this.result(result >= 1);
    }
}
