package com.vrv.vap.admin.mapper;


import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface BaseReportMapper extends BaseMapper<BaseReport> {
    int replaceInto(BaseReport baseReport);

    Page<Map<String, Object>> queryPersonByOrg();

    Page<Map<String, Object>> queryPersonBySecret();

    Page<Map<String, Object>> queryMonitorInfo();

    Page<Map<String, Object>> queryOrgBySecret();
}