package com.vrv.vap.admin.service;


import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.model.Dashboard;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DashboardService extends BaseService<Dashboard> {

    void cancelFirstPage();

    int delUnmatchedWidgets(List<Dashboard> DashboardList);

    Result linkShareLogin(HttpServletRequest request, String account, String password);

    int updateTopDashboard(Dashboard dashboard);
}
