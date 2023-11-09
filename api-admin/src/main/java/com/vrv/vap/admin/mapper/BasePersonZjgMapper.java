package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.vo.BasePersonZjgQuery;
import com.vrv.vap.base.BaseMapper;

import java.util.List;
import java.util.Map;

public interface BasePersonZjgMapper extends BaseMapper<BasePersonZjg> {

   Map queryBasePersonTrend(BasePersonZjgQuery query);

   List<Map<String, Object>> getOrgPersonTop();

   List<Map> getAllUsersAuth();
}