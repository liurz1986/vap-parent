package com.vrv.vap.admin.service.impl;

import javax.annotation.Resource;

import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vrv.vap.admin.mapper.SuperviseStatusSubmitMapper;
import com.vrv.vap.admin.model.SuperviseStatusSubmit;
import com.vrv.vap.admin.service.SuperviseStatusSubmitService;


/**
 * Created by CodeGenerator on 2021/08/05.
 */
@Service
@Transactional
public class SuperviseStatusSubmitServiceImpl extends BaseServiceImpl<SuperviseStatusSubmit> implements SuperviseStatusSubmitService {
    @Resource
    private SuperviseStatusSubmitMapper superviseStatusSubmitMapper;

}
