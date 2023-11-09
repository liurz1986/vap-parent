package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.UserIpMapper;
import com.vrv.vap.admin.model.UserIp;
import com.vrv.vap.admin.service.UserIpService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
@Service
@Transactional
public class UserIpServiceImpl extends BaseServiceImpl<UserIp> implements UserIpService {
    @Resource
    private UserIpMapper useripMapper;

}
