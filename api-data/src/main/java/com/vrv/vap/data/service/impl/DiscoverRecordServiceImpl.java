package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.DiscoverRecordMapper;
import com.vrv.vap.data.model.DiscoverRecord;
import com.vrv.vap.data.service.DiscoverRecordService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class DiscoverRecordServiceImpl extends BaseServiceImpl<DiscoverRecord> implements DiscoverRecordService {
    @Resource
    private DiscoverRecordMapper discoverRecordMapper;

}
