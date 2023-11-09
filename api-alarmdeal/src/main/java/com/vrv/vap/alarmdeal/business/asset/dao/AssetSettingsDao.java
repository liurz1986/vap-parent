package com.vrv.vap.alarmdeal.business.asset.dao;

import java.util.List;
import java.util.Map;

public interface AssetSettingsDao {
	public  List<Map<String, Object>> getAssetTemplateAttribute(List<String> guids);
}
