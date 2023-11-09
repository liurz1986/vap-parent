package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;

import java.util.List;
import java.util.Map;

public interface AppTemplateInitDataService {

    public Map<String,List<List<String>>> getInitDataByType(String type);

}
