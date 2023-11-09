package com.vrv.vap.admin.web;


import com.vrv.vap.admin.model.Report;
import com.vrv.vap.admin.model.VisualReportCatalog;
import com.vrv.vap.admin.model.VisualReportJava;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.service.ReportService;
import com.vrv.vap.admin.service.VisualReportCatalogService;
import com.vrv.vap.admin.service.VisualReportCycleFileService;
import com.vrv.vap.admin.service.VisualReportJavaService;
import com.vrv.vap.admin.vo.VisualReportCatalogJavaVo;
import com.vrv.vap.admin.vo.VisualReportCatalogVo;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @BelongsPackage com.sa.platform.bussiness.web
* @Author CodeGenerator
* @CreateTime 2020/09/10
* @Description (VisualReportCatalog相关接口)
* @Version
*/
@RestController
@Api(value = "VisualReportCatalog")
@RequestMapping("/report/catalog")
public class VisualReportCatalogController extends ApiController {

    @Autowired
    private VisualReportCatalogService visualReportCatalogService;
    @Autowired
    private VisualReportCycleFileService visualReportCycleFileService;

    @Autowired
    private VisualReportJavaService visualReportJavaService;

    @Resource
    ReportService reportService;

    @Value("${screen-capturer.report-type:node}")
    private String reportType;
    /**
    * 添加
    **/
    @ApiOperation(value = "添加VisualReportCatalog")
    @SysRequestLog(description="添加周期报表分类", actionType = ActionType.ADD)
    @PutMapping
    public Result addVisualReportCatalog(@RequestBody VisualReportCatalog visualReportCatalog) {
        int result = visualReportCatalogService.save(visualReportCatalog);
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改VisualReportCatalog", hidden = false)
    @SysRequestLog(description="修改周期报表分类", actionType = ActionType.UPDATE)
    @PatchMapping
    public Result updateVisualReportCatalog(@RequestBody VisualReportCatalog  visualReportCatalog) {
        int result = visualReportCatalogService.update(visualReportCatalog);
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除VisualReportCatalog")
    @SysRequestLog(description="删除周期报表分类", actionType = ActionType.DELETE)
    @DeleteMapping
    public Result delVisualReportCatalog(@RequestBody DeleteQuery deleteQuery) {
        int result = visualReportCatalogService.deleteByIds(deleteQuery.getIds());
        return this.result(result == 1);
    }


    @ApiOperation("获取所有报告")
    @SysRequestLog(description="获取所有报告类别", actionType = ActionType.SELECT)
    @GetMapping(value = "/children")
    public Result getAllCatalog() {
        List<VisualReportCatalog> visualReportCatalogs = visualReportCatalogService.findAll();
        if(StringUtils.isNotEmpty(reportType) && reportType.equals("java")) {
            List<VisualReportJava> list = visualReportJavaService.findAll();
            List<VisualReportCatalogJavaVo> visualReportCatalogJavaVos = new ArrayList<>();
            visualReportCatalogs.stream().forEach(reportCatalog -> {
                VisualReportCatalogJavaVo visualReportCatalogVo = new VisualReportCatalogJavaVo();
                BeanUtils.copyProperties(reportCatalog, visualReportCatalogVo);
                visualReportCatalogVo.setChildren(list.stream().filter(p -> reportCatalog.getId().equals(p.getCatalogId())).collect(Collectors.toList()));
                visualReportCatalogJavaVos.add(visualReportCatalogVo);
            });
            return this.vData(visualReportCatalogJavaVos);
        }else {
            Example example = new Example(Report.class);
            example.excludeProperties("themeId", "uiState", "param", "timeRestore", "dataset");
            List<Report> reports = reportService.findByExample(example);
            List<VisualReportCatalogVo> visualReportCatalogVos = new ArrayList<>();
            visualReportCatalogs.stream().forEach(reportCatalog -> {
                VisualReportCatalogVo visualReportCatalogVo = new VisualReportCatalogVo();
                BeanUtils.copyProperties(reportCatalog, visualReportCatalogVo);
                visualReportCatalogVo.setChildren(reports.stream().filter(p -> reportCatalog.getId().equals(p.getCatalogId())).collect(Collectors.toList()));
                visualReportCatalogVos.add(visualReportCatalogVo);
            });

            return this.vData(visualReportCatalogVos);
        }
    }

}