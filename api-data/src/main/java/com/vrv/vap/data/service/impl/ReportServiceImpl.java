package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.ReportMapper;
import com.vrv.vap.data.model.Report;
import com.vrv.vap.data.service.ReportService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ReportServiceImpl extends BaseServiceImpl<Report> implements ReportService {

    @Resource
    private ReportMapper reportMapper;

    @Override
    public List<Report> findByExample(Example example) {
        example.excludeProperties("ui", "param", "dataset");
        return super.findByExample(example);
    }
}
