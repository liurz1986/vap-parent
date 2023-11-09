package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Dashboard;
import com.vrv.vap.base.BaseMapper;

public interface DashboardMapper extends BaseMapper<Dashboard> {

    void cancelFirstPage();

    Integer findMaxTop();
}
