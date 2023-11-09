package com.vrv.vap.data.controller;


import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.Screen;
import com.vrv.vap.data.service.ScreenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/screen")
@Api(value = "【大屏】大屏/模板管理", tags = "【大屏】大屏/模板管理")
public class ScreenController extends ApiController {

    @Autowired
    ScreenService screenService;

    @ApiOperation(value = "获取全部大屏")
    @GetMapping
    public VData<List<Screen>> getAll() {
        Example example = new Example(Screen.class);
        example.excludeProperties("ui", "effect", "colorScheme");
        return this.vData(screenService.findByExample(example));
    }

    @ApiOperation(value = "获取指定大屏")
    @GetMapping(value = "/{id}")
    public VData<Screen> get(@PathVariable("id") Integer id) {
        return this.vData(screenService.findById(id));
    }

    @ApiOperation(value = "查询大屏")
    @PostMapping
    public VList<Screen> query(Query query) {
        Example example = this.pageQuery(query, Screen.class);
        return this.vList(screenService.findByExample(example));
    }

    @ApiOperation(value = "新增大屏")
    @PutMapping
    public VData<Screen> add(@RequestBody Screen screen) {
        int result = screenService.save(screen);
        if (result == 1) {
            return this.vData(screen);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "修改大屏")
    @PatchMapping
    public Result edit(@RequestBody Screen screen) {
        int result = screenService.updateSelective(screen);
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除大屏（支持批量）")
    @DeleteMapping
    public Result del(@RequestBody DeleteQuery delete) {
        int result = screenService.deleteByIds(delete.getIds());
        return this.result(result > 0);
    }


}
