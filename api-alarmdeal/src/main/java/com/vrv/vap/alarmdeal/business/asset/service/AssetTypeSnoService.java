package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeSnoVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface AssetTypeSnoService extends BaseService<AssetTypeSno, String> {
    /**
     * 获得三级资产平台类型List  
     * @param asset_sno_type
     * @return
     */
	public List<AssetTypeSnoVO> getAssetTypeSnoList(AssetSearchVO assetSearchVO);
	
	/**
	 * 资产品牌树
	 * @param assetTypeSnos
	 * @return
	 */
	public List<AssetTypeTreeVO> mapperTreeVO(List<AssetTypeSno> assetTypeSnos);
	
	/**
	 * 获得品牌型号
	 * @param AssetTypeSnoName
	 * @return
	 */
	public AssetTypeSno getAssetTypeSnoGuidByAssetTypeSnoName(String treeCode,String AssetTypeSnoName);
	
}
