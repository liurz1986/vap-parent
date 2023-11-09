package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.base.BaseService;

import java.util.Collection;
import java.util.List;

/**
 * Created by Main on 2019/07/24.
 */
public interface BaseReportModelService extends BaseService<BaseReportModel> {
    BaseReportModel findById(String id);

    Integer batchDelete(String ids);

    List<BaseReportModel> selectByIds(Collection<String> ids);
}
