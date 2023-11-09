package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.admin.model.RoleResource;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface RoleResourceMapper extends BaseMapper<RoleResource> {

    // 查询角色所有应用
    List<App> queryRoleApps(@Param("roleIds") List<Integer> roleIds);


    // 查询角色的所有权限
    List<Resource> queryRoleRules(@Param("roleIds") List<Integer> roleIds);

    // 查询角色的所有视窗
    List<Resource> queryRoleWidgets(@Param("roleIds") List<Integer> roleIds);


    int queryCountByResource(@Param("resourceIds") List<Integer> roleId);



}