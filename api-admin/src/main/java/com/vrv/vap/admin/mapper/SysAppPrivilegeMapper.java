package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.SysAppPrivilege;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysAppPrivilegeMapper extends BaseMapper<SysAppPrivilege> {
    Integer insertBuiltIn(@Param("appId") Integer appId);
}