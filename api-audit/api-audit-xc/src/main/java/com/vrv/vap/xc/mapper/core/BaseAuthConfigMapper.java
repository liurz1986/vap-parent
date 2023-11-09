package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.pojo.BaseAuthConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface BaseAuthConfigMapper extends BaseMapper<BaseAuthConfig> {

    @Select("SELECT src_obj AS ip ,type_id AS typeId FROM base_auth_config WHERE create_time" +
            " BETWEEN #{params.startTime} AND #{params.endTime} GROUP BY type_id")
    List<Map<String, Object>> getIpByType(@Param("params") ReportParam model);
}
