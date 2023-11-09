package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.SysPrivilegeMapper;
import com.vrv.vap.admin.model.SysPrivilege;
import com.vrv.vap.admin.service.SysPrivilegeService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/10/26.
 */
@Service
@Transactional
public class SysPrivilegeServiceImpl extends BaseServiceImpl<SysPrivilege> implements SysPrivilegeService {
    @Resource
    private SysPrivilegeMapper sysPrivilegeMapper;

}
