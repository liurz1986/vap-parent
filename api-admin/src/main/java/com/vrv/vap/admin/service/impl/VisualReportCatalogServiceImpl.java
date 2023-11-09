package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.mapper.VisualReportCatalogMapper;
import com.vrv.vap.admin.model.VisualReportCatalog;
import com.vrv.vap.admin.service.VisualReportCatalogService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2020/09/10.
 */
@Service
@Transactional
public class VisualReportCatalogServiceImpl extends BaseServiceImpl<VisualReportCatalog> implements VisualReportCatalogService {
    @Resource
    private VisualReportCatalogMapper visualReportCatalogMapper;

}
