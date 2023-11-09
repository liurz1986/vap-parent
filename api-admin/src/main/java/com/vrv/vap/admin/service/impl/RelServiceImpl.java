package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.RelMapper;
import com.vrv.vap.admin.model.Rel;
import com.vrv.vap.admin.service.RelService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/07/11.
 */
@Service
@Transactional
public class RelServiceImpl extends BaseServiceImpl<Rel> implements RelService {
    @Resource
    private RelMapper relMapper;

}
