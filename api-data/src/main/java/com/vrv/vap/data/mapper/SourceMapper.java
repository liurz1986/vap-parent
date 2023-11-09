package com.vrv.vap.data.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.data.model.Source;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SourceMapper extends BaseMapper<Source> {


    @Select("${sql}")
    List<Map<String, Object>> execQuery(String sql);

    List<Source> findAllbyRoleIds(@Param("roleIds") List<Integer> roleIds);


}