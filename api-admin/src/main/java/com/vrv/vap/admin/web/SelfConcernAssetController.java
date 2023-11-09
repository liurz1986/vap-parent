package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.SelfConcernAsset;
import com.vrv.vap.admin.service.SelfConcernAssetService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.admin.vo.SelfConcernAssetQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
* @BelongsPackage com.vrv.vap.admin.web
* @Author The VAP Team
* @CreateTime 2023-09-14
* @Description (SelfConcernAsset相关接口)
* @Version
*/
@RestController
@Api(value = "SelfConcernAsset")
@RequestMapping("/selfConcernAsset")
public class SelfConcernAssetController extends ApiController {

    @Autowired
    private SelfConcernAssetService selfConcernAssetService;

    /**
    * 获取所有数据--SelfConcernAsset
    */
    @ApiOperation(value = "获取所有SelfConcernAsset")
    @GetMapping
    public VData< List<SelfConcernAsset>> getAllSelfConcernAsset() {
        List<SelfConcernAsset> list = selfConcernAssetService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加SelfConcernAsset")
    @PutMapping
    public Result addSelfConcernAsset(@RequestBody SelfConcernAsset selfConcernAsset) {
        int result = selfConcernAssetService.save(selfConcernAsset);
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改SelfConcernAsset", hidden = false)
    @PatchMapping
    public Result updateSelfConcernAsset(@RequestBody SelfConcernAsset  selfConcernAsset) {
        int result = selfConcernAssetService.update(selfConcernAsset);
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除SelfConcernAsset")
    @DeleteMapping
    public Result delSelfConcernAsset(@RequestBody DeleteQuery deleteQuery) {
        int result = selfConcernAssetService.deleteByIds(deleteQuery.getIds());
        return this.result(result == 1);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询SelfConcernAsset（分页）")
    @PostMapping
    public VList<SelfConcernAsset> querySelfConcernAsset(@RequestBody SelfConcernAssetQuery selfConcernAssetQuery) {
        Example example = this.pageQuery(selfConcernAssetQuery, SelfConcernAsset.class);
        List<SelfConcernAsset> list =  selfConcernAssetService.findByExample(example);
        return this.vList(list);
    }
}