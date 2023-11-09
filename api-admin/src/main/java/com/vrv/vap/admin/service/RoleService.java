package com.vrv.vap.admin.service;


import com.vrv.vap.admin.model.Role;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
public interface RoleService extends BaseService<Role> {
     Role findRoleByGuid(String guid);

     int insertRoleList(List<Role> role);

    void deleteAllRole();

    int updateRoleOrg(Role role);

    List<Role> getBusinessAndOperationRole(String roleId,String dealType);
}
