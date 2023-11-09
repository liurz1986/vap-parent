package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.VisualDatabaseConnection;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VisualDatabaseMapper extends BaseMapper<VisualDatabaseConnection> {

    List<Map> querySql(@Param("sql") String sql);

    Integer queryDataCount(@Param("sql") String sql);
}
