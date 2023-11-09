package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.admin.model.DiscoverSqlInfo;
import com.vrv.vap.admin.service.DiscoverSqlInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
* @BelongsPackage com.vrv.vap.search.web
* @Author CodeGenerator
* @CreateTime 2020/10/28
* @Description (DiscoverSqlInfo相关接口)
* @Version
*/
@RestController
@Api(value = "DiscoverSqlInfo")
@RequestMapping("/sql/info")
public class DiscoverSqlInfoController extends ApiController {

    @Autowired
    private DiscoverSqlInfoService discoverSqlInfoService;

    /**
    * 获取所有数据--DiscoverSqlInfo
    */
    @ApiOperation(value = "获取所有DiscoverSqlInfo")
    @GetMapping
    public VData< List<DiscoverSqlInfo>> getAllDiscoverSqlInfo() {
        List<DiscoverSqlInfo> list = discoverSqlInfoService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加DiscoverSqlInfo")
    @PutMapping
    public Result addDiscoverSqlInfo(@RequestBody DiscoverSqlInfo discoverSqlInfo) {
        discoverSqlInfo.setCreateTime(new Date());
        int result = discoverSqlInfoService.save(discoverSqlInfo);
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改DiscoverSqlInfo", hidden = false)
    @PatchMapping
    public Result updateDiscoverSqlInfo(@RequestBody DiscoverSqlInfo  discoverSqlInfo) {
        int result = discoverSqlInfoService.update(discoverSqlInfo);
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除DiscoverSqlInfo")
    @DeleteMapping
    public Result delDiscoverSqlInfo(@RequestBody DeleteQuery deleteQuery) {
        int result = discoverSqlInfoService.deleteByIds(deleteQuery.getIds());
        return this.result(result == 1);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询DiscoverSqlInfo（分页）")
    @PostMapping
    public VList<DiscoverSqlInfo> queryDiscoverSqlInfo(@RequestBody Query queryVo) {
        Example example = this.pageQuery(queryVo, DiscoverSqlInfo.class);
        List<DiscoverSqlInfo> list =  discoverSqlInfoService.findByExample(example);
        return this.vList(list);
    }
}