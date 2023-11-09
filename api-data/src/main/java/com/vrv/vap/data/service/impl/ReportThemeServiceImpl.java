package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.ReportThemeMapper;
import com.vrv.vap.data.model.ReportTheme;
import com.vrv.vap.data.service.ReportThemeService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class ReportThemeServiceImpl extends BaseServiceImpl<ReportTheme> implements ReportThemeService {
    @Resource
    private ReportThemeMapper reportThemeMapper;

}
