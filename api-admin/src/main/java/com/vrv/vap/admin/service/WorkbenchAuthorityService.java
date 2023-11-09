package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.WorkbenchAuthority;
import com.vrv.vap.base.BaseService;

import java.util.List;

public interface WorkbenchAuthorityService extends BaseService<WorkbenchAuthority> {

    /**
     * 同过roleId查询用户工作台权限
     */

    public  WorkbenchAuthority findByRoleId(String roleId);

    /**
     * 获取用户角色的code集合
     */
    public String getCodesByUserId(List<Integer> roleIds);
}
