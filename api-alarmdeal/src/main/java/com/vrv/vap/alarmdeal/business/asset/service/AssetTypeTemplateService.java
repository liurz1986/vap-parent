package com.vrv.vap.alarmdeal.business.asset.service;


import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface AssetTypeTemplateService  extends BaseService<AssetTypeTemplate, String>{
	public AssetTypeTemplate getAssetTypeTemplate(String guid);

	public AssetTypeTemplate getParentTemplate(String treeCode);

	public List<String> geteAssetTypeSnoGuids(String treeCode);
}
