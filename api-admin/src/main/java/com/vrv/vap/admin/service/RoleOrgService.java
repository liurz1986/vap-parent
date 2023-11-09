package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.RoleOrg;
import com.vrv.vap.base.BaseService;

/**
 * @author lilang
 * @date 2021/8/23
 * @description
 */
public interface RoleOrgService extends BaseService<RoleOrg> {

   void saveOrgRoles(String orgId,Integer roleId);

   void deleteByRoleIds(String[] roleIds);

}
