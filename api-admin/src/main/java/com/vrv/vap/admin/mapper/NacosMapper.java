package com.vrv.vap.admin.mapper;


import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.AppSort;
import com.vrv.vap.admin.model.NacosUser;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NacosMapper extends BaseMapper<NacosUser> {


    List<Map> getInfo(@Param("tableName") String tableName);

    void insertInfo(@Param("tableName") String tableName,@Param("keys") List<String> keys, @Param("content") Map<String,Object> content);

    void clearRows(@Param("tableName") String tableName);

    List<String> getColumns(@Param("tableName") String tableName, @Param("tableSchema") String tableSchema);

}