package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.Entity;
import com.vrv.vap.admin.service.EntityService;
import com.vrv.vap.admin.vo.EntityQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 实体定义维护接口
 * Created by lizj on 2018/7/10.
 */
@RestController
@RequestMapping(path = "/entity")
public class EntityController extends ApiController {

    @Autowired
    EntityService entityService;

    /**
     * 查询实体列表
     */
    @PostMapping
    @ApiOperation("查询实体列表")
    @SysRequestLog(description = "查询实体列表",actionType = ActionType.SELECT)
    public VList<Entity> queryEntity(@RequestBody EntityQuery param) {
        Example example = this.pageQuery(param, Entity.class);
        return this.vList(entityService.findByExample(example));
    }

    /**
     * 查询实体列表（全部）
     */
    @GetMapping
    @ApiOperation("查询实体列表（全部）")
    @SysRequestLog(description = "查询实体列表（全部）",actionType = ActionType.SELECT)
    public VData<List<Entity>> queryEntityAll() {
        return this.vData(entityService.findAll());
    }

    /**
     * 添加实体
     */
    @PutMapping
    @ApiOperation("添加实体")
    @SysRequestLog(description = "添加实体",actionType = ActionType.ADD)
    public Result addEntity(@RequestBody Entity param) {
        int result = entityService.save(param);
        if (result == 1) {
            return this.vData(param);
        }
        return this.result(false);
    }

    /**
     * 修改实体
     */
    @PatchMapping
    @ApiOperation("修改实体")
    @SysRequestLog(description = "修改实体",actionType = ActionType.UPDATE)
    public Result updateEntity(@RequestBody Entity param) {
        int result = entityService.updateSelective(param);
        return this.result(result == 1);
    }

    /**
     * 删除实体
     */
    @DeleteMapping
    @ApiOperation("删除实体")
    @SysRequestLog(description = "删除实体",actionType = ActionType.DELETE)
    public Result deleteEntity(@RequestBody DeleteQuery param) {
        int result = entityService.deleteByIds(param.getIds());
        return this.result(result == 1);
    }

}
