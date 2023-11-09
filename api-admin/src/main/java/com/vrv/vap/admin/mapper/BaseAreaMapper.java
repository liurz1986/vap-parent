package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.BaseArea;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseAreaMapper extends BaseMapper<BaseArea> {
    List<BaseArea> findSubAreaByCode(@Param("areaCode") String areaCode);
}