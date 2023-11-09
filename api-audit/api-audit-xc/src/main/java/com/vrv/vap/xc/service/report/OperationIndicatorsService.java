package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.OperationModel;
import com.vrv.vap.xc.model.ReportParam;
import org.springframework.web.bind.annotation.RequestBody;

public interface OperationIndicatorsService {

    /**
     * 运维情况
     * 统计每个运维人员的运维对象类型、运维频率
     *
     * @param model
     * @return
     */
    VList<OperationModel> statisticsTypeFrequency(@RequestBody ReportParam model);
}
