package com.vrv.vap.data.controller;


import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.Report;
import com.vrv.vap.data.model.ReportCatalog;
import com.vrv.vap.data.model.ReportTheme;
import com.vrv.vap.data.service.ReportCatalogService;
import com.vrv.vap.data.service.ReportService;
import com.vrv.vap.data.service.ReportThemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/report")
@Api(value = "【报告】报表/分类/皮肤 管理", tags = "【报告】报表/分类/皮肤 管理")
public class ReportController extends ApiController {

    @Autowired
    ReportService reportService;

    @Autowired
    ReportCatalogService reportCatalogService;

    @Autowired
    ReportThemeService reportThemeService;

    @ApiOperation(value = "获取指定报表")
    @GetMapping(value = "/{id}")
    public VData<Report> getReportById(@PathVariable("id") Integer id) {

        return this.vData(reportService.findById(id));
    }

    @ApiOperation(value = "获取所有报表")
    @GetMapping
    public VData<List<Report>> getAllReport() {
        Example example = new Example(Report.class);
        example.excludeProperties("ui", "param", "dataset");
        return this.vData(reportService.findByExample(example));
    }

    @ApiOperation(value = "查询报表")
    @PostMapping
    public VList<Report> queryReport(@RequestBody Query query) {
        Example example = this.pageQuery(query, Report.class);
        return this.vList(reportService.findByExample(example));
    }

    @ApiOperation(value = "新增报表")
    @PutMapping
    public VData<Report> addReport(@RequestBody Report report) {
        int result = reportService.save(report);
        if (result == 1) {
            return this.vData(report);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "修改报表")
    @PatchMapping
    public Result editReport(@RequestBody Report report) {
        int result = reportService.updateSelective(report);
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除报表（支持批量）")
    @DeleteMapping
    public Result delReport(@RequestBody DeleteQuery delete) {
        int result = reportService.deleteByIds(delete.getIds());
        return this.result(result > 0);
    }

    @ApiOperation(value = "获取全部分类")
    @GetMapping(value = "/catalog")
    public VData<List<ReportCatalog>> getCatalogs() {
        return this.vData(reportCatalogService.findAll());
    }

    @ApiOperation(value = "获取全部皮肤")
    @GetMapping(value = "/theme")
    public VData<List<ReportTheme>> getThemes() {
        return this.vData(reportThemeService.findAll());
    }


    @ApiOperation(value = "查询皮肤")
    @PostMapping(value = "/theme")
    public VList<ReportTheme> queryThemes(Query query) {
        Example example = this.pageQuery(query, ReportTheme.class);
        return this.vList(reportThemeService.findByExample(example));
    }

    @ApiOperation(value = "新增皮肤")
    @PutMapping(value = "/theme")
    public VData<ReportTheme> addReportTheme(@RequestBody ReportTheme reportTheme) {
        int result = reportThemeService.save(reportTheme);
        if (result == 1) {
            return this.vData(reportTheme);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "修改皮肤")
    @PatchMapping(value = "/theme")
    public Result editReportTheme(@RequestBody ReportTheme reportTheme) {
        int result = reportThemeService.updateSelective(reportTheme);
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除皮肤（支持批量）")
    @DeleteMapping(value = "/theme")
    public Result delReportTheme(@RequestBody DeleteQuery delete) {
        int result = reportThemeService.deleteByIds(delete.getIds());
        return this.result(result > 0);
    }


}
