package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthCommonConfig;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import java.util.List;
import java.util.Map;

/**
 * 2023-08
 * @author liurz
 */
public interface BaseAuthCommonConfigService extends BaseService<BaseAuthCommonConfig, String> {
    public  List<BaseAuthCommonConfig> getBaseConfig();

    public Result<List<Map<String, Object>>> getRefBaseData(String code);

    public Map<String, Object> getColumns(String typeId);

    public List<Map> getOptType(String typeId);
}
