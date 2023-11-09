package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.DiscoverEntityMapper;
import com.vrv.vap.data.model.DiscoverEntity;
import com.vrv.vap.data.service.BaseCacheServiceImpl;
import com.vrv.vap.data.service.DiscoverEntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class DiscoverEntityServiceImpl extends BaseCacheServiceImpl<DiscoverEntity> implements DiscoverEntityService {
    @Resource
    private DiscoverEntityMapper discoverEntityMapper;

}
