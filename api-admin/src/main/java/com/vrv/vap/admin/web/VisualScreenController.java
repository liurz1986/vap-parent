package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.VisualScreen;
import com.vrv.vap.admin.service.VisualScreenService;
import com.vrv.vap.admin.vo.VisualScreenQuery;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

/**
 * @author lilang
 * @date 2019/12/11
 * @description 标准化大屏控制器
 */
@RestController
@RequestMapping(path = "/visualScreen")
public class VisualScreenController extends ApiController {

    @Autowired
    private VisualScreenService visualScreenService;

    @GetMapping
    @ApiOperation("获取所有大屏")
    public Result getAllScreen() {
        return this.vData(visualScreenService.findAll());
    }


    @PutMapping
    @ApiOperation("添加标准化大屏")
    public VData addVisualDetail(@RequestBody VisualScreen visualScreen) {
        visualScreenService.save(visualScreen);
        return this.vData(visualScreen);
    }

    @PostMapping
    @ApiOperation("查询标准化大屏")
    public VList queryVisualDetail(@RequestBody VisualScreenQuery visualScreenQuery) {
        Example example = this.pageQuery(visualScreenQuery,VisualScreen.class);
        return this.vList(visualScreenService.findByExample(example));
    }

    @PatchMapping
    @ApiOperation("修改标准化大屏")
    public Result updateVisualDetail(@RequestBody VisualScreen visualScreen) {
        Integer id = visualScreen.getId();
        if (id == null) {
            return this.result(false);
        }
        int result = visualScreenService.updateSelective(visualScreen);
        return this.result(result == 1);
    }

    @DeleteMapping
    @ApiOperation("删除标准化大屏")
    public Result delVisualDetail(@RequestBody DeleteQuery param) {
        String ids  = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        int result = visualScreenService.deleteByIds(ids);
        return this.result(result >= 1);
    }
}
