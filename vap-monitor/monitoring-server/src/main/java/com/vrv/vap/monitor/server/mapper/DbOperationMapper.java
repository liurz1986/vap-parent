package com.vrv.vap.monitor.server.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.monitor.server.model.DbOperationInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DbOperationMapper extends BaseMapper<DbOperationInfo> {
}