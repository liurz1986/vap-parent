package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.NetWorkCaseModel;
import com.vrv.vap.xc.model.NetWorkCountModel;
import com.vrv.vap.xc.model.ReportParam;
import org.springframework.web.bind.annotation.RequestBody;

public interface NetworkIndicatorsService {

    /** 1.3.应用系统访问情况
     * 各个应用系统按部门统计访问情况
     */
   VList<NetWorkCountModel> statisticsAppAccess(@RequestBody ReportParam model);

    /** 1.19.网络互联情况
     * 统计每个互联单位访问应用系统频率、下载文件数量和上传文件数量
     */
    VList<NetWorkCaseModel> statisticsAppFile(@RequestBody ReportParam model);
}
