package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.PrintModel;
import com.vrv.vap.xc.model.ReportParam;
import org.springframework.web.bind.annotation.RequestBody;

public interface PrintIndicatorsService {
    /**
     * 部门、人员输出情况
     * 按部门、人员统计打印文件数量、密级，并排序
     *
     * @param model
     * @return
     */
    VList<PrintModel> statisticsPrintFile(@RequestBody ReportParam model, String opType, String type, boolean total);
}
