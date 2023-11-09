package com.vrv.vap.admin.service;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.vo.EsSearchQuery;
import com.vrv.vap.admin.vo.ReportConfig;
import com.vrv.vap.base.BaseService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Created by Main on 2019/07/24.
 */
public interface BaseReportService extends BaseService<BaseReport> {

    boolean importConfig(ReportConfig reportConfig);

    Page queryPersonPrint(EsSearchQuery esSearchQuery);

    Page queryOrgPrint(@RequestBody EsSearchQuery esSearchQuery);

    Page queryPersonImPrint(@RequestBody EsSearchQuery esSearchQuery);

    Page queryOrgImPrint(@RequestBody EsSearchQuery esSearchQuery);

    Page queryAsset(@RequestBody EsSearchQuery esSearchQuery);

    Page queryByLogType(@RequestBody EsSearchQuery esSearchQuery);

    Page queryTrend(@RequestBody EsSearchQuery esSearchQuery);

    Page queryVirusSysStatus(@RequestBody EsSearchQuery esSearchQuery);

    Page queryVirusTop(@RequestBody EsSearchQuery esSearchQuery);

    Page queryVirusDeal(@RequestBody EsSearchQuery esSearchQuery);

    Page queryVirusDetail(@RequestBody EsSearchQuery esSearchQuery);

    Map<String, Object> queryChangeInfo(@RequestBody EsSearchQuery esSearchQuery);
}
