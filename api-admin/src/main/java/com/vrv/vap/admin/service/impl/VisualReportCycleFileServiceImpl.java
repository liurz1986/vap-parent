package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.VisualReportCycleFileMapper;
import com.vrv.vap.admin.model.VisualReportCycleFile;
import com.vrv.vap.admin.service.VisualReportCycleFileService;
import com.vrv.vap.admin.vo.VisualReportCatalogQuery;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * Created by CodeGenerator on 2020/09/10.
 */
@Service
@Transactional
public class VisualReportCycleFileServiceImpl extends BaseServiceImpl<VisualReportCycleFile> implements VisualReportCycleFileService {
    @Resource
    private VisualReportCycleFileMapper visualReportCycleFileMapper;

    @Override
    public List<Map> getReportTrend(VisualReportCatalogQuery visualReportCatalogQuery) {
        if(visualReportCatalogQuery.getStartTime()==null){
            visualReportCatalogQuery.setStartTime(TimeTools.getNowBeforeByDay(30));
        }
        if(visualReportCatalogQuery.getEndTime()==null){
            visualReportCatalogQuery.setEndTime(TimeTools.getNowBeforeByDay2(0));
        }

        return visualReportCycleFileMapper.getReportTrend(visualReportCatalogQuery);
    }
}
