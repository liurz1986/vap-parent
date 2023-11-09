package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.DiscoverSqlInfoMapper;
import com.vrv.vap.admin.model.DiscoverSqlInfo;
import com.vrv.vap.admin.service.DiscoverSqlInfoService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2020/10/28.
 */
@Service
@Transactional
public class DiscoverSqlInfoServiceImpl extends BaseServiceImpl<DiscoverSqlInfo> implements DiscoverSqlInfoService {
    @Resource
    private DiscoverSqlInfoMapper discoverSqlInfoMapper;

}
