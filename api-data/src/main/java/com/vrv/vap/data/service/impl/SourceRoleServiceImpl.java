package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.SourceRoleMapper;
import com.vrv.vap.data.model.SourceRole;
import com.vrv.vap.data.service.SourceRoleService;
import com.vrv.vap.base.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class SourceRoleServiceImpl extends BaseServiceImpl<SourceRole> implements SourceRoleService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SourceRoleMapper sourceRoleMapper;

}
