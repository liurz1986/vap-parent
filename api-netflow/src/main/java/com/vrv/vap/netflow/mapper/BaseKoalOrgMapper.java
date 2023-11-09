package com.vrv.vap.netflow.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.netflow.model.BaseKoalOrg;
import com.vrv.vap.netflow.vo.BaseKoalOrgVO;

import java.util.List;

public interface BaseKoalOrgMapper extends BaseMapper<BaseKoalOrg> {
    List<BaseKoalOrgVO> findAll();
}