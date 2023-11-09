package com.vrv.vap.admin.mapper;


import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseReportModelMapper extends BaseMapper<BaseReportModel> {
    int batchReplaceInto(List<BaseReportModel> list);


}