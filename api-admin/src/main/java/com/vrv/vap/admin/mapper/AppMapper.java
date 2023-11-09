package com.vrv.vap.admin.mapper;


import com.vrv.vap.admin.model.AppSort;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.vrv.vap.admin.model.App;

import java.util.List;

@Mapper
public interface AppMapper extends BaseMapper<App> {

    //获取全部status=0的应用
    List<App> getAll();

    // 根据角色ID获取应用 
    List<AppSort> getAppsByRoleId(@Param("role_id") int roleId);

    // 根据角色ID获取应用
    List<AppSort> getAppsByRoleIds(@Param("roleIds") List<Integer> list);

}