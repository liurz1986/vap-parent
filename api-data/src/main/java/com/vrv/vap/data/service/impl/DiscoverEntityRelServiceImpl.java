package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.DiscoverEntityRelMapper;
import com.vrv.vap.data.model.DiscoverEntityRel;
import com.vrv.vap.data.service.DiscoverEntityRelService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class DiscoverEntityRelServiceImpl extends BaseServiceImpl<DiscoverEntityRel> implements DiscoverEntityRelService {
    @Resource
    private DiscoverEntityRelMapper discoverEntityRelMapper;

}
