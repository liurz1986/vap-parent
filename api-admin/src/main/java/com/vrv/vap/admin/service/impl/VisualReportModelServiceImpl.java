package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.VisualReportModelMapper;
import com.vrv.vap.admin.model.VisualReportModel;
import com.vrv.vap.admin.service.VisualReportModelService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2020/12/12.
 */
@Service
@Transactional
public class VisualReportModelServiceImpl extends BaseServiceImpl<VisualReportModel> implements VisualReportModelService {
    @Resource
    private VisualReportModelMapper visualReportModelMapper;

}
