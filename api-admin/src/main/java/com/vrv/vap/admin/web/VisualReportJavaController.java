package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.VisualReportJava;
import com.vrv.vap.admin.service.VisualReportCatalogService;
import com.vrv.vap.admin.service.VisualReportJavaService;
import com.vrv.vap.admin.vo.ReportJavaQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
* @BelongsPackage com.vrv.vap.search.web
* @Author CodeGenerator
* @CreateTime 2020/12/12
* @Description (VisualReportJava相关接口)
* @Version
*/
@RestController
@Api(value = "VisualReportJava")
@RequestMapping("/visualReportJava")
public class VisualReportJavaController extends ApiController {

    @Autowired
    private VisualReportJavaService visualReportJavaService;

    @Autowired
    private VisualReportCatalogService visualReportCatalogService;



    /**
    * 获取所有数据--VisualReportJava
    */
    @ApiOperation(value = "获取所有VisualReportJava")
    @GetMapping
    public VData< List<VisualReportJava>> getAllVisualReportJava() {
        List<VisualReportJava> list = visualReportJavaService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加VisualReportJava")
    @PutMapping
    public Result addVisualReportJava(@RequestBody VisualReportJava visualReportJava) {
        int result = visualReportJavaService.save(visualReportJava);
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改VisualReportJava", hidden = false)
    @PatchMapping
    public Result updateVisualReportJava(@RequestBody VisualReportJava  visualReportJava) {
        int result = visualReportJavaService.update(visualReportJava);
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除VisualReportJava")
    @DeleteMapping
    public Result delVisualReportJava(@RequestBody DeleteQuery deleteQuery) {
        int result = visualReportJavaService.deleteByIds(deleteQuery.getIds());
        return this.result(result == 1);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询VisualReportJava（分页）")
    @PostMapping
    public VList<VisualReportJava> queryVisualReportJava(@RequestBody ReportJavaQuery queryVo) {
        Example example = this.pageQuery(queryVo, VisualReportJava.class);
        List<VisualReportJava> list =  visualReportJavaService.findByExample(example);
        return this.vList(list);
    }
}