package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.service.XgsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class XgsController {

    @Autowired
    private XgsService xgsService;

    @GetMapping("/whitelist/statistic")
    @ApiOperation("获取whitelist资产统计")
    public VData<Map<String, Object>> queryWhiteListStatistic() {
        return VoBuilder.vd(xgsService.queryWhiteListStatistic());
    }
}
