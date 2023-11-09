package com.vrv.vap.admin.mapper;


import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.AppSort;
import com.vrv.vap.admin.model.TModules;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TModulesMapper extends BaseMapper<TModules> {

}