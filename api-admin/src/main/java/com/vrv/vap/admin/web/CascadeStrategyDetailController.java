package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.CascadeStrategyDetail;
import com.vrv.vap.admin.service.CascadeStrategyDetailService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author lilang
 * @date 2021/3/26
 * @description 策略详情控制器
 */
@RequestMapping(path = "/cascade/policy/detail")
@RestController
public class CascadeStrategyDetailController extends ApiController {

    @Resource
    CascadeStrategyDetailService cascadeStrategyDetailService;

    @ApiOperation("获取策略详情列表")
    @GetMapping
    public Result getStrategyDetailList() {
        return this.vData(cascadeStrategyDetailService.findAll());
    }

    @ApiOperation("添加策略详情")
    @PutMapping
    public VData addStrategyDetail(@RequestBody CascadeStrategyDetail strategyDetail) {
        cascadeStrategyDetailService.save(strategyDetail);
        return this.vData(strategyDetail);
    }

    @ApiOperation("修改策略详情")
    @PatchMapping
    public Result updateStrategyDetail(@RequestBody CascadeStrategyDetail strategyDetail) {
        if (strategyDetail == null || strategyDetail.getId() == null) {
            return this.result(false);
        }
        int result = cascadeStrategyDetailService.updateSelective(strategyDetail);
        return this.result(result == 1);
    }

    @ApiOperation("删除策略详情")
    @DeleteMapping
    public Result deleteStrategyDetail(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        int result = cascadeStrategyDetailService.deleteByIds(ids);
        return this.result(result >= 1);
    }
}
