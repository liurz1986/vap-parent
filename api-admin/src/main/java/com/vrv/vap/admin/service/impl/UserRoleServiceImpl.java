package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.UserRoleMapper;
import com.vrv.vap.admin.model.UserRole;
import com.vrv.vap.admin.service.UserRoleService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements UserRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public void deleteByUserIds(String[] userIds) {
        Example example = new Example(UserRole.class);
        example.createCriteria().andIn("userId", Arrays.asList(userIds));
        userRoleMapper.deleteByExample(example);

    }

    @Override
    public int queryCountByRole(String roleIds) {
        List<String> s = Arrays.asList(roleIds.split(","));
        List<Integer> i = new ArrayList<>();
        for (String ss: s) {
            i.add(Integer.valueOf(ss));
        }
        return userRoleMapper.queryCountByRole(i);
    }

    @Override
    public void saveUserRole(String roleId, Integer userId) {
        if (StringUtils.isNotBlank(roleId)) {
            String[] roleIds = roleId.split(",");
            List<UserRole> userRoles = new ArrayList<>();
            for (String roleStr : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(Integer.parseInt(roleStr));
                userRoles.add(ur);
            }
            userRoleMapper.insertList(userRoles);
        }
    }

    @Override
    public void deleteAllUserRole() {
        List<UserRole> userRoleList = userRoleMapper.selectAll();
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            for (UserRole userRole : userRoleList) {
                userRoleMapper.deleteByPrimaryKey(userRole.getId());
            }
        }
    }
}
