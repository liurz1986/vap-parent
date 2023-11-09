package com.vrv.vap.xc.controller.report;


import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.AssetCountModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.BaseInfoIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-基础信息指标
 */
@RestController
@RequestMapping("/report/baseInfo")
public class BaseInfoIndicatorsController {

    @Resource
    private BaseInfoIndicatorsService baseInfoIndicatorsService;

    /**
     * 2.1.资产变更审批情况
     * 统计新增资产类型和数量
     *
     * @param model
     * @return
     */
    @PostMapping("/statisticsAssetTypeNum")
    public VList<AssetCountModel> statisticsAssetTypeNum(@RequestBody ReportParam model) {
        return baseInfoIndicatorsService.statisticsAssetTypeNum(model);
    }

    /**
     * 2.1.资产变更审批情况
     * 按类型统计SM网资产总数、新增数量
     *
     * @param model
     * @return
     */
    @PostMapping("/statisticsAssetTypeTotal")
    public VList<AssetCountModel> statisticsAssetTypeTotal(@RequestBody ReportParam model) {
        return baseInfoIndicatorsService.statisticsAssetTypeTotal(model);
    }
}
