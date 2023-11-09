package com.vrv.vap.data.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.data.mapper.DashboardMapper;
import com.vrv.vap.data.model.Dashboard;
import com.vrv.vap.data.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class DashboardServiceImpl extends BaseServiceImpl<Dashboard> implements DashboardService {
    @Resource
    private DashboardMapper dashboardMapper;


    @Override
    public List<Dashboard> findByExample(Example example) {
        example.excludeProperties("ui");
        return super.findByExample(example);
    }
}
