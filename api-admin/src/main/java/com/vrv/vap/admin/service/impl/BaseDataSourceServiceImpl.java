package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.BaseDataSourceMapper;
import com.vrv.vap.admin.mapper.BaseReportMapper;
import com.vrv.vap.admin.model.BaseDataSource;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.service.BaseDataSourceService;
import com.vrv.vap.admin.service.BaseReportService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by Main on 2019/07/24.
 */
@Service
@Transactional
public class BaseDataSourceServiceImpl extends BaseServiceImpl<BaseDataSource> implements BaseDataSourceService {
    @Resource
    private BaseDataSourceMapper baseDataSourceMapper;


}
