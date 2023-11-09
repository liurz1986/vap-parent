package com.vrv.vap.admin.web;

import com.vrv.vap.admin.service.RelService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.Rel;
import com.vrv.vap.admin.vo.RelQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 图形探索映射索引字段映射表维护接口
 * Created by lizj on 2018/7/11.
 */
@RestController
@RequestMapping(path = "/rel")
public class RelController extends ApiController {

    @Autowired
    RelService relService;

    /**
     * 查询映射列表
     */
    @PostMapping
    @ApiOperation("查询映射列表")
    @SysRequestLog(description = "查询映射列表",actionType = ActionType.SELECT)
    public VList<Rel> queryRel(@RequestBody RelQuery param) {
        Example example = this.pageQuery(param, Rel.class);
        return this.vList(relService.findByExample(example));
    }

    /**
     * 查询映射列表（全部）
     */
    @GetMapping
    @ApiOperation("查询映射列表（全部）")
    @SysRequestLog(description = "查询全部映射列表",actionType = ActionType.SELECT)
    public VData<List<Rel>> queryRelAll() {
        return this.vData(relService.findAll());
    }

    /**
     * 添加映射
     */
    @PutMapping
    @ApiOperation("添加映射")
    @SysRequestLog(description = "添加映射",actionType = ActionType.ADD)
    public Result addRel(@RequestBody Rel param) {
        int result = relService.save(param);
        if (result == 1) {
            return this.vData(param);
        }
        return this.result(false);
    }

    /**
     * 修改映射
     */
    @PatchMapping
    @ApiOperation("修改映射")
    @SysRequestLog(description = "修改映射",actionType = ActionType.UPDATE)
    public Result updateRel(@RequestBody Rel param) {
        int result = relService.updateSelective(param);
        return this.result(result == 1);
    }

    /**
     * 删除映射
     */
    @DeleteMapping
    @ApiOperation("删除映射")
    @SysRequestLog(description = "删除映射",actionType = ActionType.DELETE)
    public Result deleteRel(@RequestBody DeleteQuery param) {
        int result = relService.deleteByIds(param.getIds());
        return this.result(result == 1);
    }

}
