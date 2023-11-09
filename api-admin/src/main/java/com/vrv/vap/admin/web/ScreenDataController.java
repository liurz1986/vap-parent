package com.vrv.vap.admin.web;

import com.vrv.vap.admin.mapper.BasePersonZjgMapper;
import com.vrv.vap.admin.mapper.BaseSecurityDomainMapper;
import com.vrv.vap.admin.service.ScreenDataService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/screen/data")
@Api(value = "大屏数据提供", tags = "大屏数据提供")
public class ScreenDataController extends ApiController {

    @Autowired
    private ScreenDataService screenDataService;

    @Resource
    BasePersonZjgMapper basePersonZjgMapper;

    @Resource
    BaseSecurityDomainMapper domianMapper;

    @ApiOperation(value = "信息概览")
    @GetMapping("/info")
    public VData getNetflowInfo() {
        return this.vData(screenDataService.getInfo());
    }

    @ApiOperation(value = "部门人员排行")
    @GetMapping("/org/person/top")
    public VData getOrgPersonTop() {
        return this.vData(basePersonZjgMapper.getOrgPersonTop());
    }

    @ApiOperation(value = "人员总数")
    @GetMapping("/person/num")
    public VData getPersonNum() {
        return this.vData(basePersonZjgMapper.selectAll().size());
    }

    @ApiOperation(value = "安全域IP段排行")
    @GetMapping("/domain/ip/top")
    public VData getDomainIpSegmentTop() {
        return this.vData(domianMapper.getDomainIpSegmentTop());
    }


}
