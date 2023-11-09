package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.UserModuleMapper;
import com.vrv.vap.admin.model.UserModule;
import com.vrv.vap.admin.service.UserModuleService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by CodeGenerator on 2018/03/19.
 */
@Service
@Transactional
public class UserModuleServiceImpl extends BaseServiceImpl<UserModule> implements UserModuleService {
    @Resource
    private UserModuleMapper userModuleMapper;
}
