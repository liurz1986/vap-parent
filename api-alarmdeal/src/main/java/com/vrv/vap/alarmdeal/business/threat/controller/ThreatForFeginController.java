package com.vrv.vap.alarmdeal.business.threat.controller;

import com.vrv.vap.alarmdeal.business.threat.bean.ThreatLevelManage;
import com.vrv.vap.alarmdeal.business.threat.bean.ThreatManage;
import com.vrv.vap.alarmdeal.business.threat.bean.ThreatRateManage;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.ThreatTimeReq;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.ThreatVulRes;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatLevelManageService;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatManageService;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatRateManageService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/11/10 10:36
 * @description:
 */
@Api(description="威胁数据")
@RestController
@RequestMapping(value = "/threat")
public class ThreatForFeginController {
    @Autowired
    private ThreatManageService threatManageService;

    @Autowired
    private ThreatRateManageService threatRateManageService;

    @Autowired
    private ThreatLevelManageService threatLevelManageService;

    @PostMapping(value = "/threatManageInfoByIp")
    @ApiOperation(value="通过IP获取威胁信息",notes="")
    public Result<List<ThreatManage>> getThreatManageInfo(@RequestBody Map<String,Object> param) {

        // 查询最新的数据
        List<ThreatManage> list =threatManageService.getThreatDataByIp(param);
        return ResultUtil.successList(list);
    }

    @PostMapping(value = "/getThreatDataByIpTimes")
    @ApiOperation(value="通过时间和ip查询威胁与漏洞",notes="")
    public Result<ThreatVulRes> getThreatDataByIpTimes(@RequestBody ThreatTimeReq threatTimeReq){
        ThreatVulRes result = threatManageService.getThreatDataByIpTimes(threatTimeReq);
        return ResultUtil.success(result);
    }

    @PostMapping(value = "/threatManageByIp")
    @ApiOperation(value="通过IP获取威胁值",notes="")
    public Result<List<ThreatManage>> gethreatLibraryPager(@RequestBody Map<String,Object> param) {

        // 查询最新的数据
        List<ThreatManage> list =threatManageService.getThreatData(param);
        return ResultUtil.successList(list);
    }

    @GetMapping(value = "/getThreatRateAll")
    @ApiOperation(value="查询全量威胁频率配置",notes="")
    public Result<List<ThreatRateManage>> getThreatRateAll() {

        List<ThreatRateManage> list = threatRateManageService.findAll();
        return ResultUtil.successList(list);
    }

    @GetMapping(value = "/getThreatLevelAll")
    @ApiOperation(value="查询全量威胁等级配置",notes="")
    public Result<List<ThreatLevelManage>> getThreatLevelAll() {
        List<ThreatLevelManage> list = threatLevelManageService.findAll();
        return ResultUtil.successList(list);
    }



}
