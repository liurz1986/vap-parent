package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.EmpowerOutputDeviceModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.EmpowerIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-授权指标
 */
@RestController
@RequestMapping("/report/empower")
public class EmpowerIndicatorsController {

    @Resource
    private EmpowerIndicatorsService empowerIndicatorsService;

    /**
     * 授权输出设备情况
     * 统计授权输出的设备总数，各类输出设备数量，每个部门各类输出设备数量
     *
     * @param model
     * @return
     */
    @PostMapping("/statisticsEmpowerService")
    public VList<EmpowerOutputDeviceModel> statisticsEmpowerService(@RequestBody ReportParam model, boolean total) {
        return empowerIndicatorsService.statisticsEmpowerService(model, total);
    }
}
