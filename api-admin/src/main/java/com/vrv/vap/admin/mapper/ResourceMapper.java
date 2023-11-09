package com.vrv.vap.admin.mapper;

import java.util.List;
import java.util.Map;

import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

	 // 根据角色ID查询资源
    List<Resource> queryByRoleId(@Param("param") Map param);

    //根据角色id数组查询资源
    List<Resource> queryByRoleIds(@Param("roleIds") List<Integer> roleIds);

    int disableResource(@Param("resourceId") String resourceId);

    int enableResource(@Param("resourceId") String resourceId);
}