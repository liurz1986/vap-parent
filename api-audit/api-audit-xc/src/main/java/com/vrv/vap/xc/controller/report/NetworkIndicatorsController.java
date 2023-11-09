package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.NetWorkCaseModel;
import com.vrv.vap.xc.model.NetWorkCountModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.NetworkIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-网络通联指标
 */
@RestController
@RequestMapping("/report/network")
public class NetworkIndicatorsController {

    @Resource
    private NetworkIndicatorsService networkIndicatorsService;

    /** 1.3.应用系统访问情况
     * 各个应用系统按部门统计访问情况
     */
    @PostMapping("/statisticsAppAccess")
    public VList<NetWorkCountModel> statisticsAppAccess(@RequestBody ReportParam model) {
        return networkIndicatorsService.statisticsAppAccess(model);
    }

    /** 1.19.网络互联情况
     * 统计每个互联单位访问应用系统频率、下载文件数量和上传文件数量
     */
    @PostMapping("/statisticsAppFile")
    public VList<NetWorkCaseModel> statisticsAppFile(@RequestBody ReportParam model) {
        return networkIndicatorsService.statisticsAppFile(model);
    }
}
