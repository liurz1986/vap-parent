package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Role;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}