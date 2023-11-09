package com.vrv.vap.xc.mapper.core.custom;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SingleTableMapper {
    List<Map<String, Object>> queryAll(String sql);

    List<Map<String, Object>> query(Map<String, Object> param);

    int queryCount(Map<String, Object> param);

    void delete(Map<String, Object> map);

    void add(Map<String, Object> param);

    void update(Map<String, Object> param);
}
