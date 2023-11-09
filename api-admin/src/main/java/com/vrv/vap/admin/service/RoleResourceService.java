package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.admin.model.RoleResource;
import com.vrv.vap.base.BaseService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
public interface RoleResourceService extends BaseService<RoleResource> {



    /**
     * 构造角色权限
     * */
    Set<String> buildRole(List<Resource> resources);
    /**
     * 管理角色权限
     *
     * @param roleId
     * @param addList
     * @param delList*/
    boolean managerRoleResource(int roleId, String[] addList, String[] delList);

    List<App> getRoleApps(List<Integer> roleIds);

    List<Resource> getRoleRules(List<Integer> roleIds);

    List<Resource> getRoleWidgets(List<Integer> roleIds);

    int queryCountByResource(String resourceIds);

    void deleteByRoleIds(String[] ids);

    void deleteByResourceIds(String[] ids);

    void deleteAllRoleResource();

}
