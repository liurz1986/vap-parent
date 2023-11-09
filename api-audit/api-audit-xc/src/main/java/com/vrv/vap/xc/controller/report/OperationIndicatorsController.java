package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.OperationModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.OperationIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-运维指标
 */
@RestController
@RequestMapping("/report/operation")
public class OperationIndicatorsController {

    @Resource
    private OperationIndicatorsService operationIndicatorsService;

    /**
     * 运维情况
     * 统计每个运维人员的运维对象类型、运维频率
     * @param model
     * @return
     */
    @PostMapping("/statisticsTypeFrequency")
    public VList<OperationModel> statisticsTypeFrequency(@RequestBody ReportParam model) {
        return operationIndicatorsService.statisticsTypeFrequency(model);
    }
}
