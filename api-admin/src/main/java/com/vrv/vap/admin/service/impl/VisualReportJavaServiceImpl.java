package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.VisualReportJavaMapper;
import com.vrv.vap.admin.model.VisualReportJava;
import com.vrv.vap.admin.service.VisualReportJavaService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2020/12/12.
 */
@Service
@Transactional
public class VisualReportJavaServiceImpl extends BaseServiceImpl<VisualReportJava> implements VisualReportJavaService {
    @Resource
    private VisualReportJavaMapper visualReportJavaMapper;

}
