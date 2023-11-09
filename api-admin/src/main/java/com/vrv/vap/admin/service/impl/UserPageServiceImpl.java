package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.mapper.UserPageMapper;
import com.vrv.vap.admin.model.UserPage;
import com.vrv.vap.admin.service.UserPageService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
@Service
@Transactional
public class UserPageServiceImpl extends BaseServiceImpl<UserPage> implements UserPageService {

    @Resource
    private UserPageMapper userPageMapper;

}
