package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetTypeCacheVo;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeVO;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface AssetTypeService extends BaseService<AssetType, String> {
    public List<AssetTypeCacheVo> getAssetTypeList();

	/**
	 * 获得资产类型树
	 * @param type
	 * @return
	 */
	public List<AssetTypeTreeVO> getAssetTypeTree(String type);
	
	/**
	 * 将Asset_type_tree转换成TreeVO
	 * @param assetTypes
	 * @return
	 */
	public List<AssetTypeTreeVO> mapperTreeVO(List<AssetType> assetTypes);
	
	/**
	 * 获得二级资产类型
	 * @return
	 */
	public List<AssetTypeVO> getAssetTypeList(AssetSearchVO assetSearchVO);
	
	/**
	 * 获得资产类型树
	 * @return
	 */
	public List<AssetTypeTreeVO> getAssetTypeComboboxTree();
	
	/**
	 * 根据资产类型名称获得资产类型
	 * @param AssetTypeName
	 * @return
	 */
	public AssetType getAssetTypeByAssetTypeName(String AssetTypeName);

	/**
	 * 获得资产类型树根据设置资产偏好作用域配置
	 * @return
	 */
	public List<AssetTypeTreeVO> getAssetTypeTreeByConfigure();

	/**
	 * 获取所有一级资产类型下的所有二级资产类型
	 * 2022-06-24
	 * @return
	 */
	public List<AssetType> getAllAssetTypeByGroup();

	/**
	 * 获取所有资产类型树
	 * @return
	 */
	public List<AssetTypeTreeVO> getAllAssetTypeComboboxTree();
}
