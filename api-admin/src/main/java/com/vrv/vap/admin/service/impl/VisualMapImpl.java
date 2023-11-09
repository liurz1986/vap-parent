package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.VisualMapMapper;
import com.vrv.vap.admin.model.VisualMapModel;
import com.vrv.vap.admin.service.VisualMapService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/03/26.
 */
@Service
@Transactional
public class VisualMapImpl extends BaseServiceImpl<VisualMapModel> implements VisualMapService {
    @Resource
    private VisualMapMapper visualMapMapper;

}
