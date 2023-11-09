package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.Report;
import com.vrv.vap.admin.model.VisualReportCatalog;
import com.vrv.vap.admin.service.ReportService;
import com.vrv.vap.admin.service.VisualReportCatalogService;
import com.vrv.vap.admin.vo.ReportQuery;
import com.vrv.vap.admin.vo.VisualReportCatalogVo;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2020/7/23
 * @description 报告控制器
 */
@RestController
@RequestMapping(path = "/report")
public class ReportController extends ApiController {

    @Resource
    ReportService reportService;

    @Resource
    VisualReportCatalogService visualReportCatalogService;

    @ApiOperation("获取所有报告")
    @SysRequestLog(description="获取所有报告", actionType = ActionType.SELECT)
    @GetMapping
    public Result getAllReport() {
        return this.vData(reportService.findAll());
    }

    @ApiOperation("获取所有报告")
    @SysRequestLog(description="获取所有报告类别", actionType = ActionType.SELECT)
    @GetMapping(value = "/catalog")
    public Result getAllCatalog() {
        List<VisualReportCatalog> visualReportCatalogs = visualReportCatalogService.findAll();
        Example example = new Example(Report.class);
        example.excludeProperties("themeId","uiState","param","timeRestore","dataset");
        List<Report> reports = reportService.findByExample(example);
        List<VisualReportCatalogVo> visualReportCatalogVos = new ArrayList<>();
        visualReportCatalogs.stream().forEach(reportCatalog->{
            VisualReportCatalogVo visualReportCatalogVo = new VisualReportCatalogVo();
            BeanUtils.copyProperties(reportCatalog,visualReportCatalogVo);
            visualReportCatalogVo.setChildren(reports.stream().filter(p->reportCatalog.getId()==p.getCatalogId() ).collect(Collectors.toList()));
            visualReportCatalogVos.add(visualReportCatalogVo);
        });

        return this.vData(visualReportCatalogVos);
    }

    @ApiOperation("查询报告")
    @SysRequestLog(description="查询报告", actionType = ActionType.SELECT)
    @PostMapping
    public VList queryReport(@RequestBody ReportQuery reportQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(reportQuery, Report.class);
        return this.vList(reportService.findByExample(example));
    }

    @ApiOperation("添加报告")
    @SysRequestLog(description="添加报告", actionType = ActionType.ADD)
    @PutMapping
    public VData addReport(@RequestBody Report report) {
        int result = reportService.save(report);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(report,"添加报告");
        }
        return this.vData(report);
    }

    @ApiOperation("修改报告")
    @SysRequestLog(description="修改报告", actionType = ActionType.UPDATE)
    @PatchMapping
    public Result updateReport(@RequestBody Report report) {
        Report reportSec = reportService.findById(report.getId());
        if (report == null || report.getId() == null) {
            return this.result(false);
        }
        int result = reportService.updateSelective(report);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(reportSec,report,"修改报告");
        }
        return this.result(result == 1);
    }

    @ApiOperation("删除报告")
    @SysRequestLog(description="删除报告", actionType = ActionType.DELETE)
    @DeleteMapping
    public Result deleteReport(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<Report> reportList = reportService.findByids(ids);
        int result = reportService.deleteByIds(ids);
        if (result > 0) {
            reportList.forEach(report -> {
                SyslogSenderUtils.sendDeleteSyslog(report,"删除报告");
            });
        }
        return this.result(result >= 1);
    }
}
