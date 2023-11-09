package com.vrv.vap.data.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.data.model.Dashboard;

import java.util.List;

public interface DashboardMapper extends BaseMapper<Dashboard> {

    @Override
    List<Dashboard> selectAll();
}