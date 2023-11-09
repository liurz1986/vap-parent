package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.Edge;
import com.vrv.vap.admin.service.EdgeService;
import com.vrv.vap.admin.vo.EdgeQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 探索关系维护接口
 * Created by lizj on 2018/7/11.
 */
@RestController
@RequestMapping(path = "/edge")
public class EdgeController extends ApiController {

    @Autowired
    EdgeService edgeService;

    /**
     * 查询探索关系列表
     */
    @PostMapping
    @ApiOperation("查询探索关系列表")
    @SysRequestLog(description = "查询探索关系列表",actionType = ActionType.SELECT)
    public VList<Edge> queryEdge(@RequestBody EdgeQuery param) {
        Example example = this.pageQuery(param, Edge.class);
        return this.vList(edgeService.findByExample(example));
    }

    /**
     * 查询探索关系列表（全部）
     */
    @GetMapping
    @ApiOperation("查询探索关系列表（全部）")
    @SysRequestLog(description = "查询探索关系列表（全部）",actionType = ActionType.SELECT)
    public VData<List<Edge>> queryEdgeAll() {
        return this.vData(edgeService.findAll());
    }

    /**
     * 添加探索关系
     */
    @PutMapping
    @ApiOperation("添加探索关系")
    @SysRequestLog(description = "添加探索关系",actionType = ActionType.ADD)
    public Result addEdge(@RequestBody Edge param) {
        int result = edgeService.save(param);
        if (result == 1) {
            return this.vData(param);
        }
        return this.result(false);
    }

    /**
     * 修改探索关系
     */
    @PatchMapping
    @ApiOperation("修改探索关系")
    @SysRequestLog(description = "修改探索关系",actionType = ActionType.UPDATE)
    public Result updateEdge(@RequestBody Edge param) {
        int result = edgeService.updateSelective(param);
        return this.result(result == 1);
    }

    /**
     * 删除探索关系
     */
    @DeleteMapping
    @ApiOperation("删除探索关系")
    @SysRequestLog(description = "删除探索关系",actionType = ActionType.DELETE)
    public Result deleteEdge(@RequestBody DeleteQuery param) {
        int result = edgeService.deleteByIds(param.getIds());
        return this.result(result == 1);
    }

}
