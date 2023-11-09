package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.AppRoleMapper;
import com.vrv.vap.admin.model.AppRole;
import com.vrv.vap.admin.service.AppRoleService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/03/20.
 */
@Service
@Transactional
public class AppRoleServiceImpl extends BaseServiceImpl<AppRole> implements AppRoleService {

    @Resource
    private AppRoleMapper approleMapper;

    @Override
    public int deleteByRoleId(int roleId) {
        Example example = new Example(AppRole.class);
        example.createCriteria().andEqualTo("roleId",roleId);
        return approleMapper.deleteByExample(example);
    }
}
