package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.UserRole;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRoleMapper  extends BaseMapper<UserRole> {

    int queryCountByRole(@Param("roleIds") List<Integer> roleId);

}
