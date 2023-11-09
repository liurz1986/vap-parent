package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.VisualReportModel;
import com.vrv.vap.admin.service.VisualReportModelService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
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
* @Description (VisualReportModel相关接口)
* @Version
*/
@RestController
@Api(value = "VisualReportModel")
@RequestMapping("/visualReportModel")
public class VisualReportModelController extends ApiController {

    @Autowired
    private VisualReportModelService visualReportModelService;

    /**
    * 获取所有数据--VisualReportModel
    */
    @ApiOperation(value = "获取所有VisualReportModel")
    @GetMapping
    public VData< List<VisualReportModel>> getAllVisualReportModel() {
        List<VisualReportModel> list = visualReportModelService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加VisualReportModel")
    @PutMapping
    public Result addVisualReportModel(@RequestBody VisualReportModel visualReportModel) {
        int result = visualReportModelService.save(visualReportModel);
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改VisualReportModel", hidden = false)
    @PatchMapping
    public Result updateVisualReportModel(@RequestBody VisualReportModel  visualReportModel) {
        int result = visualReportModelService.update(visualReportModel);
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除VisualReportModel")
    @DeleteMapping
    public Result delVisualReportModel(@RequestBody DeleteQuery deleteQuery) {
        int result = visualReportModelService.deleteByIds(deleteQuery.getIds());
        return this.result(result == 1);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询VisualReportModel（分页）")
    @PostMapping
    public VList<VisualReportModel> queryVisualReportModel(@RequestBody Query queryVo) {
        Example example = this.pageQuery(queryVo, VisualReportModel.class);
        List<VisualReportModel> list =  visualReportModelService.findByExample(example);
        return this.vList(list);
    }
}