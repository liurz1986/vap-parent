package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.UserRole;
import com.vrv.vap.base.BaseService;

public interface UserRoleService extends BaseService<UserRole> {


    void deleteByUserIds(String[] split);

    int queryCountByRole(String roleIds);

    void saveUserRole(String roleId,Integer userId);

    void deleteAllUserRole();
}
