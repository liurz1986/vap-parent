package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.ConditionMapper;
import com.vrv.vap.admin.model.Condition;
import com.vrv.vap.admin.service.ConditionService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/03/26.
 */
@Service
@Transactional
public class ConditionServiceImpl extends BaseServiceImpl<Condition> implements ConditionService {
    @Resource
    private ConditionMapper conditionMapper;

}
