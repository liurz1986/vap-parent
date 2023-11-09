package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.AppRole;
import com.vrv.vap.base.BaseService;

/**
 * Created by CodeGenerator on 2018/03/20.
 */
public interface AppRoleService extends BaseService<AppRole> {


    int deleteByRoleId(int roleId);
}
