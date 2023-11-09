package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.EntityMapper;
import com.vrv.vap.admin.model.Entity;
import com.vrv.vap.admin.service.EntityService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/07/11.
 */
@Service
@Transactional
public class EntityServiceImpl extends BaseServiceImpl<Entity> implements EntityService {
    @Resource
    private EntityMapper entityMapper;

}
