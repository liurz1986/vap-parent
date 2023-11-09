package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;

import java.util.List;

public interface AssetTemplateInitDataService {

    public List<String> getInitDataByType(String type,List<CustomSettings> excelColumns);

    public String getTypeByTreeCode(String treeCode);
}
