package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.VisualReportCycleFile;
import com.vrv.vap.admin.vo.VisualReportCatalogQuery;
import com.vrv.vap.base.BaseMapper;

import java.util.List;
import java.util.Map;

public interface VisualReportCycleFileMapper extends BaseMapper<VisualReportCycleFile> {
    List<Map> getReportTrend(VisualReportCatalogQuery visualReportCle);
}