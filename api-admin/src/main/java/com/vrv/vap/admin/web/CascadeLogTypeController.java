package com.vrv.vap.admin.web;

import com.vrv.vap.admin.service.CascadeLogTypeService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lilang
 * @date 2021/3/27
 * @description 日志类别菜单树
 */
@RestController
@RequestMapping(path = "/cascade/logtype")
public class CascadeLogTypeController extends ApiController {


    @Resource
    CascadeLogTypeService cascadeLogTypeService;

    @ApiOperation("获取所有日志类别")
    @GetMapping
    public Result getLogTypeList() {
        return this.vData(cascadeLogTypeService.findAll());
    }
}
