package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.SuperviseStatusSubmit;
import com.vrv.vap.admin.service.SuperviseStatusSubmitService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.*;
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
 * @Author CodeGenerator
 * @CreateTime 2021/08/05
 * @Description (SuperviseStatusSubmit相关接口)
 * @Version
 */
@RestController
@Api(value = "SuperviseStatusSubmit")
@RequestMapping("/superviseStatusSubmit")
public class SuperviseStatusSubmitController extends ApiController {

    @Autowired
    private SuperviseStatusSubmitService superviseStatusSubmitService;

    /**
     * 获取所有数据--SuperviseStatusSubmit
     */
    @ApiOperation(value = "获取所有SuperviseStatusSubmit")
    @GetMapping
    public VData< List<SuperviseStatusSubmit>> getAllSuperviseStatusSubmit() {
        List<SuperviseStatusSubmit> list = superviseStatusSubmitService.findAll();
        return this.vData(list);
    }

    /**
     * 添加
     **/
    @ApiOperation(value = "添加SuperviseStatusSubmit")
    @PutMapping
    public Result addSuperviseStatusSubmit(@RequestBody SuperviseStatusSubmit superviseStatusSubmit) {
        int result = superviseStatusSubmitService.save(superviseStatusSubmit);
        return this.result(result == 1);
    }

    /**
     * 修改
     **/
    @ApiOperation(value = "修改SuperviseStatusSubmit")
    @PatchMapping
    public Result updateSuperviseStatusSubmit(@RequestBody SuperviseStatusSubmit  superviseStatusSubmit) {
        int result = superviseStatusSubmitService.update(superviseStatusSubmit);
        return this.result(result == 1);
    }

    /**
     * 删除
     **/
    @ApiOperation(value = "删除SuperviseStatusSubmit")
    @DeleteMapping
    public Result delSuperviseStatusSubmit(@RequestBody DeleteQuery deleteQuery) {
        int result = superviseStatusSubmitService.deleteByIds(deleteQuery.getIds());
        return this.result(result == 1);
    }
    /**
     * 查询（分页）
     */
    @ApiOperation(value = "查询SuperviseStatusSubmit（分页）", hidden = false)
    @PostMapping(value="/queryPage")
    public VList<SuperviseStatusSubmit> querySuperviseStatusSubmit(@RequestBody Query queryVo) {
        Example example = this.pageQuery(queryVo, SuperviseStatusSubmit.class);
        List<SuperviseStatusSubmit> list =  superviseStatusSubmitService.findByExample(example);
        return this.vList(list);
    }
}