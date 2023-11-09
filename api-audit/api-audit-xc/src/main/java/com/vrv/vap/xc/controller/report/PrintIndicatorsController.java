package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.PrintModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.PrintIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-打印刻录指标
 */
@RestController
@RequestMapping("/report/print")
public class PrintIndicatorsController {

    @Resource
    private PrintIndicatorsService printIndicatorsService;

    /**
     * 部门输出情况
     * 按部门统计打印刻录文件数量、密级，并排序
     * @param model
     * @return
     */
    @PostMapping("/statisticsFileByOrg")
    public VList<PrintModel> statisticsFileByOrg(@RequestBody ReportParam model, String opType, boolean total) {
        return printIndicatorsService.statisticsPrintFile(model, opType, "org", total);
    }

    /**
     * 人员输出情况
     * 按人员统计打印刻录文件数量、密级，并排序
     * @param model
     * @return
     */
    @PostMapping("/statisticsFileByPerson")
    public VList<PrintModel> statisticsFileByPerson(@RequestBody ReportParam model, String opType, boolean total) {
        return printIndicatorsService.statisticsPrintFile(model, opType, "person", total);
    }
}
