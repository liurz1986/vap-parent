package com.vrv.vap.xc.mapper.core.custom;


import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface DataCleanMapper {

    String getConfById(String id);

    @MapKey("id")
    Map<String, Map<String, String>> getConfMapById(String id);
}
