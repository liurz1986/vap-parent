package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.model.Report;
import com.vrv.vap.admin.service.ReportService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lilang
 * @date 2020/7/23
 * @description 报告实现类
 */
@Service
@Transactional
public class ReportServiceImpl extends BaseServiceImpl<Report> implements ReportService {

}
