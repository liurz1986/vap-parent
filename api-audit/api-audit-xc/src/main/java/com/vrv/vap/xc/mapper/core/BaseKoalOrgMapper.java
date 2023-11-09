package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.BaseKoalOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BaseKoalOrgMapper extends BaseMapper<BaseKoalOrg> {

    @Select("SELECT code,name FROM base_koal_org")
    List<Map<String,String>> getOrgKeyValuePair();
}
