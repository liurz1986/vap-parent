package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.VisualReportCycleFile;
import com.vrv.vap.admin.vo.VisualReportCatalogQuery;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by CodeGenerator on 2020/09/10.
 */
public interface VisualReportCycleFileService extends BaseService<VisualReportCycleFile> {

    List<Map> getReportTrend(VisualReportCatalogQuery visualReportCatalogQuery);

}
