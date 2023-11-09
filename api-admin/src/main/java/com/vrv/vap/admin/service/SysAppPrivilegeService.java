package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SysAppPrivilege;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/10/26.
 */
public interface SysAppPrivilegeService extends BaseService<SysAppPrivilege> {
    boolean deleteByAppIds(List<Integer> appIds);

    boolean managerAppPrivilege(Integer appId, String[] addList, String[] delList);

    void insertBuiltIn(Integer id);
}
