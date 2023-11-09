package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.ReportCatalogMapper;
import com.vrv.vap.data.model.ReportCatalog;
import com.vrv.vap.data.service.ReportCatalogService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by The VAP Team on 2021-09-26.
 */
@Service
@Transactional
public class ReportCatalogServiceImpl extends BaseServiceImpl<ReportCatalog> implements ReportCatalogService {
    @Resource
    private ReportCatalogMapper reportCatalogMapper;

}
