package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface AssetSystemAttributeSettingsService  extends BaseService<AssetSystemAttributeSettings, String>{
	public List<CustomSettings> getSystemAttributeCustomSettings();
	public List<CustomSettings> getSystemAttributeCustomSettings(String treeCode);
	public void  cleanCache();
	public List<AssetSystemAttributeSettings> queryAssetSystemAttributeSettings(String treeCode);
	public void saveAssetSystemAttributeSettings(String assetSettingsGuid,List<AssetSystemAttributeSettings> systemAttributeSettings);
}
