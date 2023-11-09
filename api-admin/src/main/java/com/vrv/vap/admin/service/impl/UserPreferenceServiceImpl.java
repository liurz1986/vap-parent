package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.mapper.UserPreferenceMapper;
import com.vrv.vap.admin.model.UserPreference;
import com.vrv.vap.admin.service.UserPreferenceService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by 涂美政
 */
@Service
@Transactional
public class UserPreferenceServiceImpl extends BaseServiceImpl<UserPreference> implements UserPreferenceService {

    @Resource
    private UserPreferenceMapper userPreferenceMapper;

}
