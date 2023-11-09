package com.vrv.vap.data.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;

import java.util.List;

public interface SourceService extends BaseService<Source> {

    List<Source> findAllByRoleIds(List<Integer> roleIds);

    List<SourceField> fetchTypes(Source source) throws ApiException;


}
