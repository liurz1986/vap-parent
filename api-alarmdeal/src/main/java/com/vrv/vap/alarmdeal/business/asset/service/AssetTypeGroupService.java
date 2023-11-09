package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeGroupVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface AssetTypeGroupService extends BaseService<AssetTypeGroup, String> {
     
	/**
	 * 资产类型组数据集转换为AssetTypeTreeVO数据集
	 * @param assetTypeGroups
	 * @return
	 */
	public List<AssetTypeTreeVO> mapperTreeVO(List<AssetTypeGroup> assetTypeGroups);
	
	/**
	 * 获得资产类型组列表
	 * @param asset_type_name
	 * @return
	 */
	public List<AssetTypeGroupVO> getAssetTypeGroupList(AssetSearchVO assetSearchVO);
	
}
