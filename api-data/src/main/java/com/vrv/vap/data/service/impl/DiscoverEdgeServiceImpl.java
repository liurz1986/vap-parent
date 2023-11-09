package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.DiscoverEdgeMapper;
import com.vrv.vap.data.model.DiscoverEdge;
import com.vrv.vap.data.service.BaseCacheServiceImpl;
import com.vrv.vap.data.service.DiscoverEdgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class DiscoverEdgeServiceImpl extends BaseCacheServiceImpl<DiscoverEdge> implements DiscoverEdgeService {
    @Resource
    private DiscoverEdgeMapper discoverEdgeMapper;

}
