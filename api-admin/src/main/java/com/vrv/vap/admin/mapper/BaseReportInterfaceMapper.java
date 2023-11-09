package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.base.BaseMapper;

import java.util.List;

public interface BaseReportInterfaceMapper extends BaseMapper<BaseReportInterface> {
    int batchReplaceInto(List<BaseReportInterface> list);
}
