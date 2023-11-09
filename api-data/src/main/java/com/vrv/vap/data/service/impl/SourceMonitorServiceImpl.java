package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.SourceMonitorMapper;
import com.vrv.vap.data.model.SourceMonitor;
import com.vrv.vap.data.service.SourceMonitorService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class SourceMonitorServiceImpl extends BaseServiceImpl<SourceMonitor> implements SourceMonitorService {
    @Resource
    private SourceMonitorMapper sourceMonitorMapper;

    @Override
    public SourceMonitor findLastBySourceId(Integer sourceId) {
        return sourceMonitorMapper.findLastBySourceId(sourceId);
    }
}
