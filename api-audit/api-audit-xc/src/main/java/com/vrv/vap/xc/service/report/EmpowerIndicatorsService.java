package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.EmpowerOutputDeviceModel;
import com.vrv.vap.xc.model.ReportParam;
import org.springframework.web.bind.annotation.RequestBody;


public interface EmpowerIndicatorsService {

    /**
     * 授权输出设备情况
     * 统计授权输出的设备总数，各类输出设备数量，每个部门各类输出设备数量
     *
     * @param model
     * @return
     */
    VList<EmpowerOutputDeviceModel> statisticsEmpowerService(@RequestBody ReportParam model, boolean total);
}
