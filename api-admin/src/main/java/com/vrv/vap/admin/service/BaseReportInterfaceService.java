package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.admin.util.Result;
import com.vrv.vap.base.BaseService;

import java.util.Collection;
import java.util.List;

public interface BaseReportInterfaceService extends BaseService<BaseReportInterface> {

    BaseReportInterface add(BaseReportInterface record);

    List<BaseReportInterface> queryByParam(BaseReportInterface record);

    BaseReportInterface findById(String id);

    List<BaseReportInterface> selectByIds(Collection<String> ids);
}
