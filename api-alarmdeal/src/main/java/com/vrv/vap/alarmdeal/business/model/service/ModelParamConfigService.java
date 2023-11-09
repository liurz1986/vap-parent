package com.vrv.vap.alarmdeal.business.model.service;

import com.vrv.vap.alarmdeal.business.model.model.ModelParamConfig;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;

import java.util.List;
import java.util.Map;

public interface ModelParamConfigService extends BaseService<ModelParamConfig, String> {
    public Result<List<ModelParamConfig>> queryModelParamList(String guid);

    public Result<Map<String, Object>> queryParamConfigByGuid(String guid);

    public  Result<List<String>> queryParamNamesByGuid(String guid);
}
