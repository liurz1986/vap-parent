package com.vrv.vap.admin.service;

import java.util.List;
import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.base.BaseService;
import org.apache.ibatis.annotations.Param;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
public interface ResourceService extends BaseService<Resource> {

    // 根据角色ID查询资源
    List<Resource> loadResource(int roleId);

    //根据角色id数组查询资源
    List<Resource> loadResourceByRoleIds(List<Integer> roleIds);

    // 根据uuid查询资源
    Resource findResourceByUid(String uid);
    // 根据资源id禁用资源
    int disableResource(@Param("resourceId") String resourceId);
    // 根据资源id启用资源
    int enableResource(@Param("resourceId") String resourceId);
}
